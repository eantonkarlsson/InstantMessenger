package encryption;

public class CaesarCipher implements AbstractCipher{

    private static int shift = -1;

    public static void generateKey(){
        if (shift < 0) {
            shift = (int) (Math.random() * 26);
        }
    }

    public static int returnKey(){

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

    public String decrypt(String str, GenericCryptoKey gck){

        StringBuilder strBuilder = new StringBuilder();
        int privateShift = gck.getCaesarKey();
        int length = str.length();
        char c;
        for (int j = 0; j < length; j++)
        {
            c = str.charAt(j);
            // Shift character ONLY if it is a letter
            if (Character.isLetter(c))
            {
                c = (char) (str.charAt(j) - privateShift);
            }

            if ((Character.isLowerCase(str.charAt(j)) && c < 'a') || (Character.isUpperCase(str.charAt(j)) && c < 'A'))
            {
                c = (char) (str.charAt(j) + 26 - privateShift);
            }
            strBuilder.append(c);
        }
        return strBuilder.toString();
    }

    public String encryptionType(){
        return "caesar";
    }
}