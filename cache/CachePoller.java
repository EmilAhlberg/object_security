import java.net.DatagramSocket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CachePoller implements Runnable {

    private DatagramSocket socket;
    private int cachePort;
    private int sourcePort;

    public CachePoller(DatagramSocket socket, int sourcePort, int  cachePort) {
        this.socket = socket;
        this.sourcePort = sourcePort;
        this.cachePort = cachePort;
    }

    public void run() {
            byte[] pollMessage= new byte[64];
            MessageFactory.putIntIntoByteBuffer(sourcePort, pollMessage, MessageFactory.PROTOCOL_POS_DEST_PORT);
            pollMessage[MessageFactory.PROTOCOL_POS_MSG_TYPE] = MessageFactory.TYPE_FOUR;
        try {
            while(true) {
                InetAddress IPAddress = InetAddress.getByName("localhost");
                DatagramPacket p = new DatagramPacket(
                pollMessage, pollMessage.length, IPAddress, cachePort);
                socket.send(p);
                Thread.sleep(10000);
            }
        } catch(Exception e) {
            System.out.println("CacheDispatcher exception:" + e.getMessage());
        }
    }
}
