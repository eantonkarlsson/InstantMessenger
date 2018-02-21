
import java.awt.*;
import java.io.File;
import java.lang.reflect.Array;

public class Message {
    private String text;
    private Color color;
    private Array userArray;
    private String privateKey;
    private String encryptionMethod;
    private static final String[] allowedEncryptionMethods = {"RSA","AES","blowfish"};

    public Message() {

    }

    private String Decrypt() {
        return null;
    }

    private String FormatXML(String text) {
    	try {

    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    		// root element
    		Document xml = docBuilder.newDocument();
    		Element rootElement = xml.createElement("encrypted");
    		xml.appendChild(rootElement);

    		// child element
    		Element child = xml.createElement("text");
    		rootElement.appendChild(child);

    		// set attribute to child element
    		Attr attr = xml.createAttribute("type");
    		if (isEncryptionMethodValid(encryptionMethod))
    		{
    			attr.setValue(encryptionMethod);
    		}
    		else
    		{
    			attr.setValue("none")
    		}
    		rootElement.setAttributeNode(attr);


    		// write the content into xml file
    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
    		Transformer transformer = transformerFactory.newTransformer();
    		DOMSource source = new DOMSource(doc);
    		StreamResult result = new StreamResult(new File("C:\\file.xml"));

    		// Output to console for testing
    		// StreamResult result = new StreamResult(System.out);

    		transformer.transform(source, result);

    		System.out.println("File saved!");

    	  } catch (ParserConfigurationException pce) {
    		pce.printStackTrace();
    	  } catch (TransformerException tfe) {
    		tfe.printStackTrace();
    	  }
    	return null;
    }

    private void Encrypt() {

    }

    public String DeformatXML() {
	return null;
    }

    public void setColor(Color color) {
    }

    public void setEncryptType(String type) {
    }
    
    public boolean isEncryptionMethodValid(String method) {
    	boolean validity = false;
    	for(String s: allowedEncryptionMethods)
    	{
    		if (s == method)
    		{
    			validity = true;
    			return validity;
    		}
    	}
    	return validity;
    }

    public void attachFile(File file) {
    }

    public void deattachFile() {

    }

}
