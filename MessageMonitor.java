import java.util.LinkedList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageMonitor{

 private  LinkedList<String> messages = new LinkedList<String>();

 public MessageMonitor() {
  messages.add("första meddelandet!");
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
  messages.pop();
  InetAddress IPAddress = InetAddress.getByName("localhost");
  String msg = "hej din port är: " + port;
  DatagramPacket p = new DatagramPacket(
  msg.getBytes(), msg.getBytes().length, IPAddress, port);
  socket.send(p);

}

public synchronized String getLatestMessage(){
 try{
  while(messages.isEmpty()){
   wait();
  }
  return messages.getFirst();
 }
 catch(InterruptedException e) {
  System.out.println(e.getMessage());
 }
 return null;
}

}
