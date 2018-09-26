import java.net.DatagramSocket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


public class SocketDispatcher implements Runnable {

    private DatagramSocket socket;
    private int bPort;
    private  int cachePort;
    private  MessageMonitor monitor;
    private int isSendingHello;
    private boolean hasInitiatedConnection = false;

    public SocketDispatcher(DatagramSocket socket, MessageMonitor monitor, int bPort, int cachePort, int isSendingHello) {
        this.socket = socket;
        this.monitor = monitor;
        this.bPort = bPort;
        this.cachePort = cachePort;
        this.isSendingHello = isSendingHello;
    }

    public void run() {
        try {
            while(true) {
                if(monitor.hasNotConnected()){
                    if(isSendingHello != 0 && !hasInitiatedConnection) {
                        monitor.sendHandshakeHello(bPort, cachePort, socket);
                        hasInitiatedConnection = true;
                    }

                }else{
                    monitor.handleMessageRecieved(bPort, cachePort, socket);
                }
                Thread.sleep(5000);
            }
        } catch(Exception e) {
            System.out.println("Dispatcher exception:" + e.getMessage());
        }
    }
}
