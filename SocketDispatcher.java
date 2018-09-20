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
                if(monitor.hasNotConnected()){
                    //Handshake initiation
                    System.out.println("skickat");
                    Thread.sleep(5000);
                }else{
                    monitor.sendMessage(otherPort, socket);
                }

            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
