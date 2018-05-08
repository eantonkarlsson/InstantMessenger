
import Encryption.AESCipher;
import Encryption.AbstractCipher;
import Encryption.CaesarCipher;
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

public class ChatController{

    private AbstractCipher encryptCipher;
    private AbstractCipher decryptCipher;
    private ArrayList<Message> messages = new ArrayList<>();
    private String outgoingMessage;
    private Message incomingMessage;
    private String currentColorRGB;
    private String currentName;
	private ArrayList<User> outgoingUsers = new ArrayList<>();
	private String encryptionMethod;
	private static final String[] allowedEncryptionMethods = {"caesar","AES", "none"};
	private JTextPane chatPane;

	public ChatController(User user) {
		outgoingUsers.add(user);
		encryptionMethod = "none";
		currentColorRGB = "#000000";
		currentName = "";
	}

	public void updatePanel(Message msg){
		StringWriter sw = new StringWriter();
		javax.swing.text.Document doc = chatPane.getDocument();

		sw.append("[");
		sw.append(msg.returnUser());
		sw.append("]: ");
		sw.append(msg.returnMsg());
		sw.append("\\n");
		String s = sw.toString();

		try {
			doc.insertString(doc.getLength(), s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void addPanel(JTextPane panel) {
		chatPane = panel;
	}

	public void addUser(User u){
		outgoingUsers.add(u);
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

	public String createMessage(String str) {

		try {
			Message newMsg = new Message(str, transformText(str), currentName, currentColorRGB);
			messages.add(newMsg);
			updatePanel(newMsg);
			return outgoingMessage = newMsg.returnXML();
		} catch (ParserConfigurationException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | TransformerException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void importMessage(Message msg) {
		messages.add(msg);
	}

	public String deTransformMessage(String msg) throws ParserConfigurationException, IOException, SAXException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

		Document xml = XMLHandler.StringToXML(msg);

		String[] content = new String[4];
		content[0] = xml.getElementsByTagName("message").item(0).getAttributes().item(0).toString();
		content[1] = xml.getElementsByTagName("text").item(0).getAttributes().item(0).toString();
		try {
			content[2] = xml.getElementsByTagName("encrypted").item(0).getAttributes().item(0).toString();
		}catch (NullPointerException e){
			content[2] = null;
		}
		if (content[2] == null) {
			content[3] = xml.getElementsByTagName("text").item(0).getTextContent();
			Message newMsg = new Message(content[3], msg, content[0], content[1]);
			messages.add(newMsg);
			updatePanel(newMsg);
			return content[3];
		}
		else {
			content[3] = xml.getElementsByTagName("encrypted").item(0).getTextContent();
			String decryptedMsg = decryptCipher.decrypt(content[3]);
			Message newMsg = new Message(decryptedMsg, msg, content[0], content[1]);
			messages.add(newMsg);
			updatePanel(newMsg);
			return decryptedMsg;
		}
	}


    public void setColor(String RGB) {
		currentColorRGB = RGB;
    }

    public void setName(String name) { currentName = name; }



public void setSelfUser(String name) {

    }

    public void changeCipher(String type) {

            if (type == "AES") {
                    encryptCipher = new AESCipher();

            }
            else if (type == "caesar") {
                    encryptCipher = new CaesarCipher();
            }
            else {
                    encryptCipher = null;
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
