import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

  private  DatagramSocket socket;
  private int port;
  private SocketListener socketListener;
  private SocketDispatcher socketDispatcher;

  public Server(int port) throws SocketException {
    this.port = port;
    this.socket = new DatagramSocket(this.port);
    Thread listener = new Thread( new SocketListener(socket, port));
    Thread dispatcher = new Thread (new SocketDispatcher(socket, port));
    listener.start();
    dispatcher.start();
  }

  public static void main(String[] args) throws Exception {
    Server server = new Server(Integer.parseInt(args[0]));
  }
}
