import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketDispatcher implements Runnable {

 private DatagramSocket socket;
 private int port;

 public SocketDispatcher(DatagramSocket socket, int port) {
  this.socket = socket;
  this.port = port;
 }

 public void run() {
  try {
   InetAddress IPAddress = InetAddress.getByName("localhost");
   while (true) {
    String msg = "hej";
    Thread.sleep(10000);
    DatagramPacket p = new DatagramPacket(
    msg.getBytes(), msg.getBytes().length, IPAddress, 3000);

    this.socket.send(p);
   }
  }
  catch(UnknownHostException e) {

  } catch(IOException e) {
  }
  catch (InterruptedException e) {

  }

 }
}
