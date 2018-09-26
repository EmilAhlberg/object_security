import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SocketListener implements Runnable {

    private DatagramSocket socket;
    private MessageMonitor monitor;

    public SocketListener(DatagramSocket socket, MessageMonitor monitor) {
        this.socket = socket;
        this.monitor = monitor;
    }

    public void run() {
        try {
            while(true) {
                byte[] buf = new byte[64];
                DatagramPacket packet = new DatagramPacket(buf,buf.length);
                socket.receive(packet);
                monitor.newMessage(packet);
            }
        }
        catch(IOException e) {
            System.out.println("Listener exception:" + e.getMessage());
        }
    }
}
