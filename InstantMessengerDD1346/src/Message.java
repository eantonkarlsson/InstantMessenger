
import java.awt.Color;



public class Message {

    private String msg;
    private String xml;
    private String user;
    private String colorRGB;
    private boolean killThread;

    public Message(String message, String xmlMessage, String sender, String color, boolean lastMessage){
        msg = message;
        xml = xmlMessage;
        user = sender;
        colorRGB = color;
        killThread = lastMessage;
    }

    public void UpdateText(String str) {
        msg = str;
    }

    public String returnXML(){
        return xml;
    }

    public String returnUser() { 
        return user; 
    }

    public String returnMsg() { 
        return msg; 
    }
    
    public Color returnColor(){       
        Color color = Color.decode(colorRGB);
        return color; 
    }

    public boolean isLastMessage(){
        return killThread;
    }


    public void updateRGB(String RGB){
        this.colorRGB = RGB;
    }

}
