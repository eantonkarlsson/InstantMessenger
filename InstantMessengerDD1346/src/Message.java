
import java.awt.Color;



public class Message {

    private String msg;
    private String xml;
    private String user;
    private String colorRGB;

    public Message(String message, String xmlMessage, String sender, String color){
        msg = message;
        xml = xmlMessage;
        user = sender;
        colorRGB = color;
    }

    public void UpdateText(String str) {
        msg = str;
    }

    public String returnXML(){
        return xml;
    }

    public String returnUser() { return user; }

    public String returnMsg() { return msg; }
    
    public Color returnColor(){
        //System.out.println(colorRGB);
        
        Color color = Color.decode(colorRGB);
        //System.out.println(color);
        return color; 
    }


    public void updateRGB(String RGB){
        this.colorRGB = RGB;
    }

}
