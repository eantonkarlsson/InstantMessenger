public class Message {

    private String encryptedMsg;

    public Message(String str){
        encryptedMsg = str;
    }

    public void UpdateText(String str) {
        encryptedMsg = str;
    }

    public String returnEncryptedText(){
        return encryptedMsg;
    }
}
