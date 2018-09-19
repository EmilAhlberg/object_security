import java.net.DatagramSocket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


public class SocketDispatcher implements Runnable {

 private DatagramSocket socket;
 private int otherPort;
 private  MessageMonitor monitor;

 public SocketDispatcher(DatagramSocket socket, MessageMonitor monitor, int otherPort) {
  this.socket = socket;
  this.monitor = monitor;
  this.otherPort = otherPort;
 }

 public void run() {
  try {
   while(true) {
     monitor.sendMessage(otherPort, socket);
     System.out.println("skickat");
     Thread.sleep(5000);
   }
  } catch(Exception e) {
   System.out.println(e.getMessage());
  }
 }
}
