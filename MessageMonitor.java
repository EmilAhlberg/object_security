import java.util.LinkedList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageMonitor{

 private  LinkedList<String> messages = new LinkedList<String>();
 private int currentState;
 private static final int HANDSHAKE = 0;
 private static final int DATA_TRANSFER = 1;

 public MessageMonitor() {
  messages.add("första meddelandet!");
  currentState = HANDSHAKE;
 }

 public synchronized void newMessage(DatagramPacket packet){
  String msg = new String(packet.getData()).trim();
  System.out.println("Recieved message: " + msg);
  messages.add(msg);
  notifyAll();
 }

 public synchronized void sendMessage(int port, DatagramSocket socket) throws Exception {
  while (messages.isEmpty()) {
    System.out.println("wait!");
     wait();
  }
  parseMessage();
  // InetAddress IPAddress = InetAddress.getByName("localhost");
  // String msg = "hej din port är: " + port;
  // DatagramPacket p = new DatagramPacket(
  // msg.getBytes(), msg.getBytes().length, IPAddress, port);
  socket.send(p);

}

private void parseMessage(){
    byte[] message = messages.pop();
    currentState = message[0];
    switch(currentState){
        case HANDSHAKE:
            handleHandshake(message);
            break;
        case DATA_TRANSFER:
            handleDataTransfer(message);
            break;
        default:
            break;
    }
}

private void handleHandshake(byte[] message){

}

private void handleDataTransfer(byte[] message){

}

}
