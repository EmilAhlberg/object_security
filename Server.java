import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
//import java.net.InetAddress;
import java.net.SocketException;

public class Server {

  private  DatagramSocket socket;
  private int port;

  public Server() throws SocketException {
    this.port = 3000;
    this.socket = new DatagramSocket(this.port);
  }

  private void listen() throws Exception {
    String msg;

    while(true) {
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf,buf.length);
      socket.receive(packet);
      msg = new String(packet.getData()).trim();
      System.out.println("Recieved message: " + msg);
    }
  }

  public static void main(String[] args) throws Exception {
    Server server = new Server();
    server.listen();
  }

}
