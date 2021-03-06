
import Encryption.AESCipher;
import Encryption.AbstractCipher;
import Encryption.CaesarCipher;
import java.awt.Color;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.swing.text.Style;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatController{

    private AbstractCipher encryptCipher;
    private AbstractCipher decryptCipher;
    private ArrayList<Message> messages = new ArrayList<>();
    private String currentColorRGB;
    private String currentName;
    private String encryptionMethod;
    private static final String[] allowedEncryptionMethods = {"caesar","AES", "none"};
    private JTextPane chatPane;

    public ChatController() {
            encryptionMethod = "none";
            currentColorRGB = "#000000";
            currentName = MyFrame.returnName();
    }

    //prints message on textarea
    public void updatePanel(Message msg){
        StringWriter sw = new StringWriter();
        StyledDocument doc = (StyledDocument) chatPane.getDocument();
        sw.append("[");
        sw.append(msg.returnUser());
        sw.append("]: ");
        sw.append(msg.returnMsg());
        sw.append("\n");
        String s = sw.toString();
        Style style = doc.addStyle("StyleName", null);
        StyleConstants.setForeground(style, msg.returnColor());

        try {
            doc.insertString(doc.getLength(), s, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    //prints disconnect message
    public void printDisconnect(String str){
        javax.swing.text.Document doc = chatPane.getDocument();
        try {
            doc.insertString(doc.getLength(), str, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void addPanel(JTextPane panel) {
        chatPane = panel;
    }

    public String transformText(String str) throws ParserConfigurationException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, TransformerException {

        // Build XML Document with structure
        // <message sender=name><text color=RGB> msg </text></message>
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element msg = doc.createElement("message");
        doc.appendChild(msg);
        Attr sender = doc.createAttribute("sender");
        sender.setValue(currentName);
        msg.setAttributeNode(sender);

        Element txt = doc.createElement("text");
        msg.appendChild(txt);
        Attr color = doc.createAttribute("color");
        color.setValue(currentColorRGB);
        txt.setAttributeNode(color);

        if (encryptCipher != null) {
            Element enc = doc.createElement("encrypted");
            doc.appendChild(enc);
            Attr type = doc.createAttribute("type");
            type.setValue(encryptCipher.toString());
            enc.setAttributeNode(type);

            str = encryptCipher.encrypt(str);
            Text content = doc.createTextNode(str);
            enc.appendChild(content);
        }
        else {
            Text content = doc.createTextNode(str);
            txt.appendChild(content);
        }

        return XMLHandler.XMLtoString(doc);

    }

    // create a message
    public Message createMessage(String str) {

        try {
            Message newMsg = new Message(str, transformText(str), currentName, currentColorRGB, false);
            messages.add(newMsg);
            // updatePanel(newMsg);
                return newMsg;
        } catch (ParserConfigurationException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | TransformerException e) {
            e.printStackTrace();
        }
        return new Message("Parser error", "Encryption error", currentName, currentColorRGB, false);
    }

    public void importMessage(Message msg) {
        messages.add(msg);
    }

    //converts xml to message
    public Message deTransformMessage(String msg) throws ParserConfigurationException, IOException, SAXException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Document xml = XMLHandler.StringToXML(msg);

        String[] content = new String[4];
        content[0] = xml.getElementsByTagName("message").item(0).getAttributes().item(0).toString();
        content[0] = content[0].substring(8,content[0].length()-1);
        content[1] = xml.getElementsByTagName("text").item(0).getAttributes().item(0).toString();
        content[1] = content[1].substring(content[1].length()-8,content[1].length()-1);

        try {
            content[2] = xml.getElementsByTagName("encrypted").item(0).getAttributes().item(0).toString();
        }catch (NullPointerException e){
            content[2] = null;
        }

        if (content[2] == null) {
            content[3] = xml.getElementsByTagName("text").item(0).getTextContent();
            if (content[3].contains("<disconnect><disconnect/>")) {
                content[3] = content[3].replace("<disconnect><disconnect/>", "");
                Message newMsg = new Message(content[3], msg, content[0], content[1], false);
                updatePanel(newMsg);
                return new Message("", "", "", "", true);
            }
            Message newMsg = new Message(content[3], msg, content[0], content[1], false);
            messages.add(newMsg);
            updatePanel(newMsg);
            return newMsg;
        }
        else {
            content[3] = xml.getElementsByTagName("encrypted").item(0).getTextContent();
            String decryptedMsg = decryptCipher.decrypt(content[3]);
            Message newMsg = new Message(decryptedMsg, msg, content[0], content[1], false);
            messages.add(newMsg);
            updatePanel(newMsg);
            return newMsg;
        }

	}


    public void setColor(String RGB) {
	currentColorRGB = RGB;
    }

    public void setName(String name) { 
        currentName = name; 
    }
    
    public String returnName(){
        return currentName; 
    }


    public void changeCipher(String type) {
        if (type == "AES") {
            encryptCipher = new AESCipher();
        }
        else if (type == "caesar") {
            decryptCipher = new CaesarCipher();
        }
        else {
            decryptCipher = null;
        }
    }
    public void changeDecryptionCipher(String type) {
        if (type == "AES") {
            decryptCipher = new AESCipher();
        }
        else if (type == "caesar") {
            decryptCipher = new CaesarCipher();
        }
        else {
            decryptCipher = null;
        }
    }
}
