package Encryption;

public class CaesarCipher implements AbstractCipher{

    private static final String algorithm = "AES";
    private int shift = -1;

    public void generateKey(){
        if (shift < 0) {
            shift = (int) (Math.random() * 26);
        }
    }

    public void setKey(int i){
        shift = i;
    }

    public int returnKey(){

        generateKey();
        return shift;
    }

    public String encrypt(String str){

        generateKey();
        StringBuilder strBuilder = new StringBuilder();
        int length = str.length();
        char c;
        for (int j = 0; j < length; j++)
        {
            c = str.charAt(j);
            // Shift character ONLY if it is a letter
            if (Character.isLetter(c))
            {
                c = (char) (str.charAt(j) + shift);
            }

            if ((Character.isLowerCase(str.charAt(j)) && c > 'z') || (Character.isUpperCase(str.charAt(j)) && c > 'Z'))
            {
                c = (char) (str.charAt(j) - 26 + shift);
            }
            strBuilder.append(c);
        }
        return strBuilder.toString();
    }

    public String decrypt(String str){

        StringBuilder strBuilder = new StringBuilder();
        int length = str.length();
        char c;
        for (int j = 0; j < length; j++)
        {
            c = str.charAt(j);
            // Shift character ONLY if it is a letter
            if (Character.isLetter(c))
            {
                c = (char) (str.charAt(j) - shift);
            }

            if ((Character.isLowerCase(str.charAt(j)) && c < 'a') || (Character.isUpperCase(str.charAt(j)) && c < 'A'))
            {
                c = (char) (str.charAt(j) + 26 - shift);
            }
            strBuilder.append(c);
        }
        return strBuilder.toString();
    }

    public String encryptionType(){
        return algorithm;
    }
}