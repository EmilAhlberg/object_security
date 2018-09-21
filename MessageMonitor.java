import java.util.LinkedList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class MessageMonitor{

  private LinkedList<byte[]> messages = new LinkedList<byte[]>();
  private int currentState;
  private static final int HANDSHAKE = 0;
  private static final int DATA_TRANSFER = 1;
  private int g, p, a, xb;
  private int sessionKey;

  public MessageMonitor() {
    a = 2;
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
    message[0] = 1;
    message[1] = 14;
    putIntIntoByteBuffer((int) (Math.pow(message[2], a) % message[3]), message, 2);
    putIntIntoByteBuffer(10376, message, 6);
    putIntIntoByteBuffer(11, message, 10);
    InetAddress IPAddress = InetAddress.getByName("localhost");
    DatagramPacket p = new DatagramPacket(
    message, message.length, IPAddress, port);
    socket.send(p);
  }

  private void putIntIntoByteBuffer(int nbr, byte[] bytes, int pos ) {
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
    System.out.println("Recieved msg type: " + messages.get(0)[0]);
    parseMessage();
    sendMessage(port, socket);
  }

  private void sendMessage(int port, DatagramSocket socket) throws Exception {
    byte[] msg = createMessage();
    System.out.println("Sending msg type: " + msg[0]);
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
      putIntIntoByteBuffer(10101010, message, 2); //message payload
      currentState = DATA_TRANSFER;
      sessionKey = (int)Math.pow(xb, a) % p ;
      break;
      case DATA_TRANSFER:
      // crypto stuff and scanner perhaps?
      message = new byte[6];
      message[0] = 3; //message type
      message[1] = 10; //message length
      putIntIntoByteBuffer(10101010, message, 2); //message payload
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
      if(message[0] == 1) {
        // Recieve g, p determined by other party, together with xb.
        g = (message[6] & 0xff) | (message[7] & 0xff) << 8 | (message[8] & 0xff) << 16 | (message[9] & 0xff) << 24;
        p = (message[10] & 0xff) | (message[11] & 0xff) << 8 | (message[12] & 0xff) << 16 | (message[13] & 0xff) << 24;
      }
      else if (message[0] == 2) {
        currentState = DATA_TRANSFER;
      }
      // Regardless of initiating pary, xb always needs to be parsed.
      xb = (message[2] & 0xff) | (message[3] & 0xff) << 8 | (message[4] & 0xff) << 16 | (message[5] & 0xff) << 24;
      sessionKey = (int)Math.pow(xb, a) % p;
      System.out.println("g: " + g + " p: " + p + " xb: " + xb);
      break;
      case DATA_TRANSFER:
      handleDataTransfer(message);
      break;
      default:
      throw new Exception("Communication state unrecognized.");
    }
  }

  private void handleDataTransfer(byte[] message){

  }
}
