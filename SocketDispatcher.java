import java.net.DatagramSocket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


public class SocketDispatcher implements Runnable {

    private DatagramSocket socket;
    private int otherPort;
    private  MessageMonitor monitor;
    private int isSendingHello;

    public SocketDispatcher(DatagramSocket socket, MessageMonitor monitor, int otherPort, int isSendingHello) {
        this.socket = socket;
        this.monitor = monitor;
        this.otherPort = otherPort;
        this.isSendingHello = isSendingHello;
    }

    public void run() {
        try {
            while(true) {
                if(monitor.hasNotConnected()){
                    //Handshake initiation
                    if(isSendingHello != 0) {
                        monitor.sendHandshakeHello(otherPort, socket);
                    }

                }else{
                    monitor.handleMessageRecieved(otherPort, socket);
                }
                Thread.sleep(5000);
            }
        } catch(Exception e) {
            System.out.println("Dispatcher exception:" + e.getMessage());
        }
    }
}
