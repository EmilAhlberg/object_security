import java.util.Random;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.Mac;
import java.util.Arrays;

public class MessageFactory {
    private static String[] strings = {"HEMLIS", "CRYPTO", "WHISTLEBLOWER", "HUSH", "SECRET", "PASSWORD"};
    public static final int TYPE_ONE = 1;
    public static final int TYPE_TWO = 2;
    public static final int TYPE_THREE = 3;
    //msg type/msg length/hmac
    public static final int HEADER_LENGTH = 1 + 1 + 4;
    //  x/g/p
    private static final int TYPE_ONE_PAYLOAD_LENGTH = 4 + 4 + 4;
    // x
    private static final int TYPE_TWO_PAYLOAD_LENGTH = 4;
    /*        CURRENT PROTOCOL HEADER FORMAT:
    1 byte        1 byte         4 bytes
    |    TYPE.    |   LENGTH.    |    HMAC      |
    */

    public static final int PROTOCOL_POS_MSG_TYPE = 0;
    private static final int PROTOCOL_POS_MSG_LENGTH = 1;
    private static final int PROTOCOL_POS_HMAC = 2;
    public static final int PROTOCOL_POS_X = 6;
    public static final int PROTOCOL_POS_G = 10;
    public static final int PROTOCOL_POS_P = 14;

    // Constructs type 1 and type 2 messages (i.e. handshake messages)
    public static byte[] buildMessage(int messageType, int g, int p, int a) {
        byte[] message = new byte[64];
        switch(messageType) {
            case TYPE_ONE:
            message[PROTOCOL_POS_MSG_TYPE] = (byte)messageType;
            message[PROTOCOL_POS_MSG_LENGTH] = HEADER_LENGTH + TYPE_ONE_PAYLOAD_LENGTH;
            putIntIntoByteBuffer(((int) Math.pow(g, a) % p), message, PROTOCOL_POS_X);
            putIntIntoByteBuffer(g, message, PROTOCOL_POS_G);
            putIntIntoByteBuffer(p, message, PROTOCOL_POS_P);
            break;
            case TYPE_TWO:
            message[PROTOCOL_POS_MSG_TYPE] = (byte)messageType; //message type
            message[PROTOCOL_POS_MSG_LENGTH] = HEADER_LENGTH + TYPE_TWO_PAYLOAD_LENGTH; //message length
            putIntIntoByteBuffer((int)Math.pow(g, a) % p, message, PROTOCOL_POS_X); //message payload
            break;
        }
        return message;
    }

    // For type 3 messages (i.e. during data transfer mode)
    public static byte[] buildMessage(int messageType, SecretKeySpec secretKey, int sessionKey) throws Exception {
        byte[] message = new byte[64];
        Random r = new Random();
        String cipherText = strings[r.nextInt(strings.length)];
        String msg = encryptString(cipherText, secretKey);
        byte[] byteMsg = msg.getBytes("UTF-8");
        byte[] hmac = createHMAC(byteMsg, Integer.toString(sessionKey));
        message[PROTOCOL_POS_MSG_TYPE] = (byte)messageType; //message type
        message[PROTOCOL_POS_MSG_LENGTH] = (byte)(HEADER_LENGTH+byteMsg.length); //message length
        System.arraycopy(hmac, 0, message, 2, 4);
        System.arraycopy(byteMsg, 0, message, HEADER_LENGTH, byteMsg.length);
        System.out.println("The cryptoText " + msg+ " is in plainText: " + cipherText);
        return message;
    }

    private static String encryptString(String cipherText, SecretKeySpec secretKey) throws Exception {
        byte [] cipherTextBytes =  Base64.getEncoder().encode(cipherText.getBytes());

        //ENCRYPT
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(cipherText.getBytes("UTF-8")));
    }

    //Helper function for the HMAC
    private static byte[] makeKey(String password) throws Exception {
        PBEKeySpec ks = new PBEKeySpec(password.toCharArray(), null, 10, 32); //Default value of salt is null
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return skf.generateSecret(ks).getEncoded();
    }

    private static byte[] createHMAC(byte[] encryptedMessage, String password) throws Exception {
        // Generate HMAC Key
        byte[] key = makeKey(password);

        // Perform HMAC using SHA-256
        SecretKeySpec hmacKey = new SecretKeySpec(key, "HmacSHA256");
        Mac m = Mac.getInstance("HmacSHA256");
        m.init(hmacKey);
        byte[] hmac = m.doFinal(encryptedMessage);
        return hmac;
    }

    //Checks if the HMAC is correct
    public static boolean checkHMAC(byte[] hmac, byte[] encryptedMessage, String password) throws Exception {

        // Regenerate HMAC key
        byte[] hmacKey = makeKey(password);

        // Perform HMAC using SHA-256
        SecretKeySpec hks = new SecretKeySpec(hmacKey, "HmacSHA256");
        Mac m = Mac.getInstance("HmacSHA256");
        m.init(hks);
        byte[] chmac = m.doFinal(encryptedMessage);

        // Compare Computed HMAC vs Recovered HMAC
        if (MessageDigest.isEqual(hmac, Arrays.copyOfRange(cmac, 0, 4))) {
            return true;
        }else{
            return false;
        }
    }

    // Used as helper method in MessageMonitor, should be in helper class ideally.
    public static void putIntIntoByteBuffer(int nbr, byte[] bytes, int pos ) {
        for(int i = 0; i< 4; i++) {
            bytes[pos + i] = (byte) (nbr & 0xFF);
            nbr >>=8;
        }
    }

    public static int parseIntFromByte(byte [] message, int startPos) {
        return (message[startPos] & 0xff) |
        (message[startPos + 1] & 0xff) << 8 |
        (message[startPos +2] & 0xff) << 16 |
        (message[startPos +3] & 0xff) << 24;
    }

}
