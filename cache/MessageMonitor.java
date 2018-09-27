import java.util.LinkedList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.nio.ByteBuffer;

public class MessageMonitor{
    private LinkedList<byte[]> messages = new LinkedList<byte[]>();
    private int currentState;
    private static final int HANDSHAKE = 0;
    private static final int DATA_TRANSFER = 1;
    private int g, p, a, xb;
    private int sessionKey;
    private static SecretKeySpec secretKey;
    // Replay protection variables.
    private static int slidingWindowThreshold = 0;
    private static int currentSequenceNumber = 1; // needs to be larger than threshold to be accepted

    public MessageMonitor() {
        a =  new SecureRandom().nextInt(5) + 1; //between 1-5, proof-of-concept!
        System.out.println("Private a: " + a);
        g = 3;
        p = 2081; //TODO: check format if primes, relative primes  etc...
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

    public synchronized void sendHandshakeHello(int bPort, int cachePort, DatagramSocket socket) throws Exception{
        byte[] message = MessageFactory.buildMessage(bPort, MessageFactory.TYPE_ONE, g, p, a, currentSequenceNumber++);
        InetAddress IPAddress = InetAddress.getByName("localhost");
        DatagramPacket p = new DatagramPacket(
        message, message.length, IPAddress, cachePort);
        socket.send(p);
    }

    public synchronized void handleMessageRecieved(int bPort, int cachePort, DatagramSocket socket) throws Exception {
        while (messages.isEmpty()) {
            System.out.println("Dispatcher waiting for messages!");
            wait();
        }
        //System.out.println("Recieved msg type: " + messages.get(0)[0]);
        parseMessage();
        sendMessage(bPort, cachePort, socket);
    }

    private void sendMessage(int bPort, int cachePort, DatagramSocket socket) throws Exception {
        byte[] msg = createMessage(bPort);
        //System.out.println("Sending msg type: " + msg[0]);
        InetAddress IPAddress = InetAddress.getByName("localhost");
        DatagramPacket p = new DatagramPacket(
        msg, msg.length, IPAddress, cachePort);
        socket.send(p);
    }

    private byte[] createMessage(int bPort) throws Exception{
        byte[] message;
        switch(currentState) {
            case HANDSHAKE:
            message = MessageFactory.buildMessage(bPort, MessageFactory.TYPE_TWO, g, p, a, currentSequenceNumber++);
            initDataTransferMode();
            break;
            case DATA_TRANSFER:
            message = MessageFactory.buildMessage(bPort, MessageFactory.TYPE_THREE, secretKey, Integer.toString(sessionKey), currentSequenceNumber++);
            break;
            default:
            throw new Exception("Communication state unrecognized.");
        }
        return message;
    }

    private void parseMessage() throws Exception{
        byte[] message = messages.pop();
        int headerAndPayloadLength = message[MessageFactory.PROTOCOL_POS_MSG_LENGTH];
        byte[] hmac = Arrays.copyOfRange(message, headerAndPayloadLength, headerAndPayloadLength + MessageFactory.HMAC_BYTE_LENGTH );
        switch(currentState){
            case HANDSHAKE:
            // CHECK HMAC
            if(!MessageFactory.checkHMAC(hmac, Arrays.copyOfRange(message, 0, headerAndPayloadLength), "password")){
                throw new Exception("HANDSHAKE hmac is not correct");
            }
            checkSequenceNumber(message);
            //slidingWindowThreshold =MessageFactory.parseIntFromByte(message, MessageFactory.PROTOCOL_POS_SEQUENCE_NBR);
            byte msgType = message[MessageFactory.PROTOCOL_POS_MSG_TYPE];
            // Regardless of initiating pary, xb always needs to be parsed.
            xb = MessageFactory.parseIntFromByte(message, MessageFactory.PROTOCOL_POS_X);
            if(msgType == MessageFactory.TYPE_ONE) {
                // Recieve g, p determined by other party, together with xb.
                g = MessageFactory.parseIntFromByte(message, MessageFactory.PROTOCOL_POS_G);
                p = MessageFactory.parseIntFromByte(message, MessageFactory.PROTOCOL_POS_P);
            }
            System.out.println("\n-------\nValues negotiated:\ng: " + g + " p: " + p + " xb: " + xb);
            if (message[MessageFactory.PROTOCOL_POS_MSG_TYPE] == MessageFactory.TYPE_TWO) {
                initDataTransferMode();
            }
            break;
            case DATA_TRANSFER:
            handleDataTransfer(message);
            break;
            default:
            throw new Exception("Communication state unrecognized.");
        }
    }

    private void checkSequenceNumber(byte[] message) throws Exception {
        int recievedSequenceNumber = MessageFactory.parseIntFromByte(message, MessageFactory.PROTOCOL_POS_SEQUENCE_NBR);
        if(recievedSequenceNumber >= slidingWindowThreshold) {
            slidingWindowThreshold = recievedSequenceNumber + 1;
        } else {
            throw new Exception("Replay attack!");
        }
    }

    private void handleDataTransfer(byte[] message) throws Exception{
        int headerAndPayloadLength = message[MessageFactory.PROTOCOL_POS_MSG_LENGTH];
        byte[] hmac = Arrays.copyOfRange(message, headerAndPayloadLength, headerAndPayloadLength + MessageFactory.HMAC_BYTE_LENGTH);
        byte[] integrityString = Arrays.copyOfRange(message, 0, headerAndPayloadLength);
        if(MessageFactory.checkHMAC(hmac, integrityString, Integer.toString(sessionKey))){
            checkSequenceNumber(message);
            byte [] encryptedMsg = Arrays.copyOfRange(message, MessageFactory.HEADER_LENGTH, headerAndPayloadLength);
            String encryptedString = new String(encryptedMsg, "UTF-8");;
            String decryptedString = decryptString(encryptedString);
            System.out.println("I decrypted: " + encryptedString + " as: " + decryptedString);
        }
        else {
            //System.out.println("Hmac check fail");
            throw new Exception("HMAC check failed!");
        }

    }

    private void initDataTransferMode() throws Exception {
        currentState = DATA_TRANSFER;
        System.out.println("\n-------------\nHANDSHAKE SUCCESSFUL\n-------------\nDATA TRANSFER MODE INITIATED\n-------------\n");
        System.out.println("\n-------\ng: " + g + " \np: " + p + " \nxb: " + xb + "\na: " + a + "\nxb: " + xb);
        Double s = (Math.pow(xb, a)% p);
        sessionKey =s.intValue();
        System.out.println("Calculating sessionKey: " + sessionKey);
        byte[] keyBytes = new byte[4];
        MessageFactory.putIntIntoByteBuffer(sessionKey, keyBytes, 0);

        keyBytes = Arrays.copyOf(keyBytes, 16);
        secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String decryptString(String encryptedString) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedString)));
    }
}
