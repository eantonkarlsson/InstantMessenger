package encryption;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class AESCipher implements AbstractCipher {

    private static final String algorithm = "AES";
    private static byte[] keyContent = null;
    private static SecretKeySpec key = null;

    private void getKey() throws NoSuchAlgorithmException {
        if (key == null || keyContent == null)
        {
            KeyGenerator AESgen = KeyGenerator.getInstance(algorithm);
            AESgen.init(128);
            key = (SecretKeySpec)AESgen.generateKey();
            keyContent = key.getEncoded();
        }
    }

    public String encrypt(String str) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        getKey();
        byte[] dataToEncrypt = str.getBytes();
        Cipher AEScipher = Cipher.getInstance("AES");
        AEScipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherData = AEScipher.doFinal(dataToEncrypt);

        // convert byte array to hex string
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i < cipherData.length; i++){
            buffer.append(Character.forDigit((cipherData[i] >> 4) & 0xF, 16));
            buffer.append(Character.forDigit((cipherData[i] & 0xF), 16));
        }
        return buffer.toString();

    }

    public String decrypt(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        getKey();
        byte[] dataToDecrypt = str.getBytes();
        byte[] incomingKey = gck.getAESKey();
        SecretKeySpec decodeKey = new SecretKeySpec(incomingKey, "AES");
        Cipher AEScipher = Cipher.getInstance("AES");
        AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
        byte[] decryptedData = AEScipher.doFinal(dataToDecrypt);
        return new String(decryptedData);
    }

    public byte[] returnKey() throws NoSuchAlgorithmException{
        getKey();
        return keyContent;
    }

    public String encryptionType(){
        return "AES";
    }

}