

public abstract class GenericCryptoKey {
    private int caesarKey;
    private byte[] AESKey;

    abstract byte[] getAESKey();
    abstract int getCaesarKey();
}
