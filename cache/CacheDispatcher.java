import java.net.DatagramSocket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class CacheDispatcher implements Runnable {

    private DatagramSocket socket;
    private int otherPort;
    private CacheMonitor monitor;
    private int isSendingHello;

    public CacheDispatcher(DatagramSocket socket, CacheMonitor monitor) {
        this.socket = socket;
        this.monitor = monitor;
        this.otherPort = otherPort;
        this.isSendingHello = isSendingHello;
    }

    public void run() {
        try {
            while(true) {
                monitor.sendMessage(socket);
            }
        } catch(Exception e) {
            System.out.println("CacheDispatcher exception:" + e.getMessage());
        }
    }
}
