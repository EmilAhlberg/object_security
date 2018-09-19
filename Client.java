import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Client {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int port;

    private Client(int port) throws IOException {
        //this.serverAddress = InetAddress.getByName(destinationAddr);
        this.port = port;
        socket = new DatagramSocket(this.port);
    }

    public static void main(String[] args) throws NumberFormatException, IOException,InterruptedException {
        Client sender = new Client(4000);
        System.out.println("-- Running UDP Client at " + InetAddress.getLocalHost() + " --");
        sender.start();
    }

    private int start() throws IOException,InterruptedException {
      InetAddress IPAddress = InetAddress.getByName("localhost");
        while (true) {
            String msg = "hej";
            Thread.sleep(10000);
            DatagramPacket p = new DatagramPacket(
                msg.getBytes(), msg.getBytes().length, IPAddress, 3000);

            this.socket.send(p);
        }
    }
}
