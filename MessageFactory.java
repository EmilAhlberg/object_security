import java.util.Random;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.util.Arrays;

public class MessageFactory {
  private static String[] strings = {"HEMLIS", "CRYPTO", "WHISTLEBLOWER", "HUSH", "SECRET", "PASSWORD"};
  private static final int TYPE_ONE = 1;
  private static final int TYPE_TWO = 2;
  private static final int TYPE_THREE = 3;

  // Constructs type 1 and type 2 messages (i.e. handshake messages)
  public static byte[] buildMessage(int messageType, int g, int p, int a) {
    byte[] message = new byte[64];
    switch(messageType) {
      case TYPE_ONE:
      message[0] = (byte)messageType;
      message[1] = 14; //message content length!!!
      putIntIntoByteBuffer(((int) Math.pow(g, a) % p), message, 2);
      putIntIntoByteBuffer(g, message, 6);
      putIntIntoByteBuffer(p, message, 10);
      break;
      case TYPE_TWO:
      message[0] = (byte)messageType; //message type
      message[1] = 6; //message length
      putIntIntoByteBuffer((int)Math.pow(g, a) % p, message, 2); //message payload
      break;
    }
    return message;
  }

  // For type 3 messages (i.e. during data transfer mode)
  public static byte[] buildMessage(int messageType, SecretKeySpec secretKey) throws Exception {
    byte[] message = new byte[64];
    Random r = new Random();
    String cipherText = strings[r.nextInt(strings.length)];
    String msg = encryptString(cipherText, secretKey);
    byte[] byteMsg = msg.getBytes("UTF-8");
    message[0] = (byte)messageType; //message type
    message[1] = (byte)(2+byteMsg.length); //message length
    System.arraycopy(byteMsg, 0, message, 2, byteMsg.length);
    System.out.println("The cryptoText " + msg+ " is in plainText: " + cipherText);
    return message;
  }

  //TODO: private visibility, non static
  private static String encryptString(String cipherText, SecretKeySpec secretKey) throws Exception {
    byte [] cipherTextBytes =  Base64.getEncoder().encode(cipherText.getBytes());

    //ENCRYPT
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return Base64.getEncoder().encodeToString(cipher.doFinal(cipherText.getBytes("UTF-8")));
  }

  // Used as helper method in MessageMonitor, should be in helper class ideally.
  public static void putIntIntoByteBuffer(int nbr, byte[] bytes, int pos ) {
    for(int i = 0; i< 4; i++) {
      bytes[pos + i] = (byte) (nbr & 0xFF);
      nbr >>=8;
    }
  }

  private static byte[] generateHMAC(int sessionKey) throws Exception{
    // Perform HMAC using SHA-256
    byte[] keyBytes = new byte [4];
    putIntIntoByteBuffer(sessionKey, keyBytes,0);
    keyBytes = Arrays.copyOf(keyBytes, 16);
    SecretKeySpec hks = new SecretKeySpec(keyBytes, "HmacSHA256"); //- Define as constant key somewhere and clutch heal in report?
    Mac m = Mac.getInstance("HmacSHA256");                          //Alternativt hitta en key-derive funktion med sessionKey som input.
    m.init(hks);
    byte[] hmac = m.doFinal();
    return hmac;
  }

}
