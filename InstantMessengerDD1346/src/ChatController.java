
import Encryption.AESCipher;
import Encryption.AbstractCipher;
import Encryption.CaesarCipher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Node {

	private List<Node> childNodes = new ArrayList<>();
	private Node rootNode;
	private String element;
	private Map<String, String> attributes = new HashMap<>();

	Node() {
		this.rootNode = this;
	}
	Node(Node rootNode) {
		setRoot(rootNode);
	}

	void addChild(Node node) {
		childNodes.add(node);
	}

	private void setRoot(Node node) {
		rootNode = node;
	}

	void setElement(String str) {
		element = str;
	}

	void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	private String exportStartXML() {
		// first part
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<");
		strBuilder.append(element);
		for (String str : attributes.keySet()) {
			strBuilder.append(" ");
			strBuilder.append(str);
			strBuilder.append("=\"");
			strBuilder.append(attributes.get(str));
			strBuilder.append("\"");
		}
		strBuilder.append("\">");
		for (Node node : childNodes) {
			strBuilder.append(node.exportStartXML());
		}
		return strBuilder.toString();
	}

	private String exportEndXML() {
		StringBuilder strBuilder = new StringBuilder();
		for (Node node : childNodes) {
			strBuilder.append(node.exportEndXML());
		}
		strBuilder.append("<");
		strBuilder.append(element);
		strBuilder.append("\">");
		return strBuilder.toString();
	}

	public String formatXML(String str) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(rootNode.exportStartXML());
		strBuilder.append(str);
		strBuilder.append(rootNode.exportEndXML());
		return strBuilder.toString();
	}
}

public class ChatController{

    private AbstractCipher encryptCipher;
    private AbstractCipher decryptCipher;
    private ArrayList<Message> messages = new ArrayList<>();
    private Message outgoingMessage;
    private Message incomingMessage;
    private String currentColorRGB;
    private static User selfUser;
	private User otherUser;
	private String encryptionMethod;
	private static final String[] allowedEncryptionMethods = {"caesar","AES", "none"};

	public ChatController(User user) {
		otherUser = user;
		encryptionMethod = "none";
		currentColorRGB = "#000000";
	}


	public String transformText(String str) {

		Node msg = new Node();
		Node txt = new Node(msg);

		msg.addChild(txt);

		msg.setElement("message");
		msg.setAttribute("sender",selfUser.returnName());
		txt.setElement("text");
		txt.setAttribute("color",currentColorRGB);

		if (encryptCipher != null) {
			Node enc = new Node(msg);
			enc.setElement("encrypted");
			enc.setAttribute("type", encryptCipher.encryptionType());
			try {
				str = encryptCipher.encrypt(str);
				txt.addChild(enc);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
		}

		return msg.formatXML(str);

	}

	public void createMessage(String str) {

		outgoingMessage = new Message(transformText(str));
		messages.add(outgoingMessage);
	}

	public void deTransformMessage(String str) {

	}


    public void setColor(String RGB) {
		currentColorRGB = RGB;
    }

    public void setSelfUser(String name) {

	}

	public void changeCipher(AbstractCipher cipher, String type) {

		if (type == "AES") {
			cipher = new AESCipher();

		}
		else if (type == "caesar") {
			cipher = new CaesarCipher();
		}
		else {
			cipher = null;
		}
	}

}
