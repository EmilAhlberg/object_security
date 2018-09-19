import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SocketListener implements Runnable {

 private DatagramSocket socket;
 private int port;

 public SocketListener(DatagramSocket socket, int port) {
  this.socket = socket;
  this.port = port;
 }

 public void run() {
  String msg;

  try {
   while(true) {
    byte[] buf = new byte[256];
    DatagramPacket packet = new DatagramPacket(buf,buf.length);
    socket.receive(packet);
    msg = new String(packet.getData()).trim();
    System.out.println("Recieved message: " + msg);
   }
  }

  catch(IOException e) {
   System.out.println("Listener exception:" + e.getMessage());
  }

 }
}