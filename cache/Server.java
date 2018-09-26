import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {


    public Server(int myPort, int bPort, int cachePort, int mode) throws SocketException {
        DatagramSocket socket = new DatagramSocket(myPort);
        MessageMonitor monitor = new MessageMonitor();
        Thread cachePoller = new Thread(new CachePoller(socket, myPort, cachePort));
        cachePoller.start();
        Thread dispatcher = new Thread (new SocketDispatcher(socket, monitor, bPort, cachePort, mode));
        Thread listener = new Thread( new SocketListener(socket, monitor));
        listener.start();
        dispatcher.start();
    }

    public Server(int myPort) throws SocketException {
        DatagramSocket socket = new DatagramSocket(myPort);
        CacheMonitor monitor = new CacheMonitor();
        Thread dispatcher = new Thread(new CacheDispatcher(socket, monitor));
        Thread listener = new Thread( new CacheListener(socket, monitor));

        listener.start();
        dispatcher.start();

    }

    public static void main(String[] args) throws Exception {
        if(args.length == 1) {
            Server server = new Server(Integer.parseInt(args[0]));
        }
        else {
            Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
            Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        }

    }
}
