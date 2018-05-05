
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
    private static User currentUser;
	private ArrayList<User> outgoingUsers = new ArrayList<>();
	private String encryptionMethod;
	private static final String[] allowedEncryptionMethods = {"caesar","AES", "none"};

	public ChatController(User user) {
		outgoingUsers.add(user);
		encryptionMethod = "none";
		currentColorRGB = "#000000";
	}

	public void addUser(User u){
		outgoingUsers.add(u);
	}


	public String transformText(String str) throws ParserConfigurationException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, TransformerException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element msg = doc.createElement("message");
		doc.appendChild(msg);
		Attr sender = doc.createAttribute("sender");
		sender.setValue(currentUser.toString());
		msg.setAttributeNode(sender);

		Element txt = doc.createElement("text");
		doc.appendChild(txt);
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

		StringWriter sw = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		t.setOutputProperty(OutputKeys.METHOD, "xml");
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		t.transform(new DOMSource(doc), new StreamResult(sw));

		return sw.toString();

	}

	public void createMessage(String str) {

		try {
			Message newMsg = new Message(str, transformText(str), currentUser.toString(), currentColorRGB);
			outgoingMessage = newMsg.returnXML();
			messages.add(newMsg);
		} catch (ParserConfigurationException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void importMessage(Message msg) {
		messages.add(msg);
	}

	public void deTransformMessage(String msg) throws ParserConfigurationException, IOException, SAXException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(msg));
		Document doc = builder.parse(source);
		doc.getDocumentElement().normalize();

		String[] content = new String[4];
		content[0] = doc.getElementsByTagName("message").item(0).getAttributes().item(0).toString();
		content[1] = doc.getElementsByTagName("text").item(0).getAttributes().item(0).toString();
		content[2] = doc.getElementsByTagName("encrypted").item(0).getAttributes().item(0).toString();
		if (content[2] == null) {
			content[3] = doc.getElementsByTagName("text").item(0).getTextContent();
			Message newMsg = new Message(content[3], msg, content[0], content[1]);
			messages.add(newMsg);
		}
		else {
			content[3] = doc.getElementsByTagName("encrypted").item(0).getTextContent();
			String decryptedMsg = decryptCipher.decrypt(content[3]);
			Message newMsg = new Message(decryptedMsg, msg, content[0], content[1]);
			messages.add(newMsg);
		}
	}


    public void setColor(String RGB) {
		currentColorRGB = RGB;
    }

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
