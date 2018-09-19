import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

  private MessageMonitor monitor;

  public Server(int myPort, int otherPort) throws SocketException {
    DatagramSocket socket = new DatagramSocket(myPort);
    monitor = new MessageMonitor();
    Thread listener = new Thread( new SocketListener(socket, monitor));
    Thread dispatcher = new Thread (new SocketDispatcher(socket, monitor, otherPort));
    listener.start();
    dispatcher.start();
  }

  public static void main(String[] args) throws Exception {
    Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
  }
}


 enum ProtocolState {
 IDLE,
 HELLO,
 HELLO_RESPONSE,
 IN_SESSION
}
