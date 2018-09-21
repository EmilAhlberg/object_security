import java.util.LinkedList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.lang.System;

//TODO: remove all static method signatures

public class MessageMonitor{

  private LinkedList<byte[]> messages = new LinkedList<byte[]>();
  private int currentState;
  private static final int HANDSHAKE = 0;
  private static final int DATA_TRANSFER = 1;
  private int g, p, a, xb;
  private int sessionKey;
  private static SecretKeySpec secretKey;
  private String[] strings = {"HEMLIS", "CRYPTO", "WHISTLEBLOWER", "HUSH", "SECRET", "PASSWORD"};

  public MessageMonitor() {
    a = 1234; //TODO: random to a value that can be stored on 4 bytes
    currentState = HANDSHAKE;
  }

  public synchronized boolean hasNotConnected(){
    return (messages.isEmpty() && currentState == HANDSHAKE);
  }

  public synchronized void newMessage(DatagramPacket packet){
    byte[] msg = packet.getData();
    messages.add(msg);
    notifyAll();
  }

  public synchronized void sendHandshakeHello(int port, DatagramSocket socket) throws Exception{
    byte[] message = new byte[14];
    g = 5;
    p = 10781; //TODO: check format if primes, relative primes  etc...
    message[0] = 1;
    message[1] = 14;
    putIntIntoByteBuffer(((int) Math.pow(g, a) % p), message, 2);
    putIntIntoByteBuffer(g, message, 6);
    putIntIntoByteBuffer(p, message, 10);
    InetAddress IPAddress = InetAddress.getByName("localhost");
    DatagramPacket p = new DatagramPacket(
    message, message.length, IPAddress, port);
    socket.send(p);
  }

  private static void putIntIntoByteBuffer(int nbr, byte[] bytes, int pos ) {
    for(int i = 0; i< 4; i++) {
      bytes[pos + i] = (byte) (nbr & 0xFF);
      nbr >>=8;
    }
  }

  public synchronized void handleMessageRecieved(int port, DatagramSocket socket) throws Exception {
    while (messages.isEmpty()) {
      System.out.println("Dispatcher waiting for messages!");
      wait();
    }
    //System.out.println("Recieved msg type: " + messages.get(0)[0]);
    parseMessage();
    sendMessage(port, socket);
  }

  private void sendMessage(int port, DatagramSocket socket) throws Exception {
    byte[] msg = createMessage();
    //System.out.println("Sending msg type: " + msg[0]);
    InetAddress IPAddress = InetAddress.getByName("localhost");
    DatagramPacket p = new DatagramPacket(
    msg, msg.length, IPAddress, port);
    socket.send(p);
  }

  private byte[] createMessage() throws Exception{
    byte[] message;
    switch(currentState) {
      case HANDSHAKE:
      message = new byte[6];
      message[0] = 2; //message type
      message[1] = 6; //message length
      putIntIntoByteBuffer((int)Math.pow(g, a) % p, message, 2); //message payload
      initDataTransferMode();
      break;
      case DATA_TRANSFER:
      Random r = new Random();
      String cipherText = strings[r.nextInt(6)];
      String msg = encryptString(cipherText);
      byte[] byteMsg = msg.getBytes("UTF-8");
      message = new byte[2+byteMsg.length];
      message[0] = 3; //message type
      message[1] = (byte)message.length; //message length
      System.arraycopy(byteMsg, 0, message, 2, byteMsg.length);
      System.out.println("The cryptoText " + msg+ " is in plainText: " + cipherText);
      break;
      default:
      throw new Exception("Communication state unrecognized.");
    }
    return message;
  }

  private void parseMessage() throws Exception{
    byte[] message = messages.pop();
    switch(currentState){
      case HANDSHAKE:
      xb = (message[2] & 0xff) | (message[3] & 0xff) << 8 | (message[4] & 0xff) << 16 | (message[5] & 0xff) << 24;
      if(message[0] == 1) {
        // Recieve g, p determined by other party, together with xb.
        g = (message[6] & 0xff) | (message[7] & 0xff) << 8 | (message[8] & 0xff) << 16 | (message[9] & 0xff) << 24;
        p = (message[10] & 0xff) | (message[11] & 0xff) << 8 | (message[12] & 0xff) << 16 | (message[13] & 0xff) << 24;
      }
      System.out.println("\n-------\nValues negotiated:\ng: " + g + " p: " + p + " xb: " + xb);
      if (message[0] == 2) {
        initDataTransferMode();
      }
      // Regardless of initiating pary, xb always needs to be parsed.

      break;
      case DATA_TRANSFER:
      handleDataTransfer(message);
      break;
      default:
      throw new Exception("Communication state unrecognized.");
    }
  }

  private void handleDataTransfer(byte[] message) throws Exception{
    byte [] encryptedMsg = Arrays.copyOfRange(message, 2, message[1]);
    String encryptedString = new String(encryptedMsg, "UTF-8");;
    String decryptedString = decryptString(encryptedString);
    System.out.println("I decrypted: " + encryptedString + " as: " + decryptedString);
  }

  private void initDataTransferMode() {
    System.out.println("\n-------------\nHANDSHAKE SUCCESSFUL\n-------------\nDATA TRANSFER MODE INITIATED\n-------------\n");
    currentState = DATA_TRANSFER;
    sessionKey = (int)Math.pow(xb, a) % p;
    System.out.println("\nSession key: " + sessionKey +"\n");
    byte[] keyBytes = new byte [4];
    putIntIntoByteBuffer(sessionKey, keyBytes,0);
    MessageDigest sha = null;
    //sha = MessageDigest.getInstance("SHA-1");
    //key = sha.digest(key);
    keyBytes = Arrays.copyOf(keyBytes, 16);
    secretKey = new SecretKeySpec(keyBytes, "AES");
  }



  //TODO: private visibility, non static
  public static String encryptString(String cipherText) throws Exception {
    byte [] cipherTextBytes =  Base64.getEncoder().encode(cipherText.getBytes());

    //ENCRYPT
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return Base64.getEncoder().encodeToString(cipher.doFinal(cipherText.getBytes("UTF-8")));
  }

  public static String decryptString(String encryptedString) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedString)));
  }

  /*
  public static void main(String[] args) throws Exception{
  String cipherText = "HEJHEJ";
  int secretKey = 12345;
  String encryptedString = MessageMonitor.encryptString(cipherText, secretKey);
  System.out.println(encryptedString);

  System.out.println(MessageMonitor.decryptString(cryptoShit, secretKey));
}
*/
}
