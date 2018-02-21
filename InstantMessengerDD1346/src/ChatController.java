
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.lang.reflect.Array;
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

	String formatXML(String str) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(rootNode.exportStartXML());
		strBuilder.append(str);
		strBuilder.append(rootNode.exportEndXML());
		return strBuilder.toString();
	}
}


public class ChatController{

    private AbstractCipher cipher;
    private String currentColorRGB;
    private String selfUser;
	private String encryptionMethod;
	private String otherUser;
	private static final String[] allowedEncryptionMethods = {"caesar","AES", "none"};



	public ChatController() {
	}


	public String transformText(String str) {

		Node msg = new Node();
		Node txt = new Node(msg);

		msg.addChild(txt);

		msg.setElement("message");
		msg.setAttribute("sender",selfUser);
		txt.setElement("text");
		txt.setAttribute("color",currentColorRGB);

		if (cipher != null) {
			Node enc = new Node(msg);
			enc.setElement("encrypted");
			enc.setAttribute("type",encryptionMethod);
			try {
				str = cipher.encrypt(str);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
				enc = null;
			}
			if (enc != null) {
				txt.addChild(enc);
			}
		}

		return msg.formatXML(str);

	}

	public void createMessage(String str) {

	}


    public void setColor(String RGB) {
		currentColorRGB = RGB;
    }

    public void setSelfUser(String name) {
		selfUser = name;
	}

	public void setEncryptionMethod(String type) {

		encryptionMethod = type;
		if (type == "AES") {
			cipher = new AESCipher();

		}
		else if (type == "caesar") {
			cipher = new CaesarCipher();
		}
		else {
			cipher = null;
			encryptionMethod = "none";
		}
	}




}

}
