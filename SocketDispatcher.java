import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketDispatcher implements Runnable {

 private DatagramSocket socket;
 private int otherPort;

 public SocketDispatcher(DatagramSocket socket, int otherPort) {
  this.socket = socket;
  this.otherPort = otherPort;
 }

 public void run() {

  try {
   InetAddress IPAddress = InetAddress.getByName("localhost");
   while (true) {
    String msg = "hej din port är: " + otherPort;
    Thread.sleep(10000);
    DatagramPacket p = new DatagramPacket(
    msg.getBytes(), msg.getBytes().length, IPAddress, otherPort);

    this.socket.send(p);
   }
  } catch(UnknownHostException e) {
   System.out.println("UnknownHostException");
  } catch(IOException e) {
   System.out.println("IOException");
  }
  catch (InterruptedException e) {
    System.out.println("InterruptedException");
  }

 }
}
