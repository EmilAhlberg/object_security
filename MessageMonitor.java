import java.util.LinkedList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageMonitor{

    private  LinkedList<byte[]> messages = new LinkedList<byte[]>();
    private int currentState;
    private static final int HANDSHAKE = 0;
    private static final int DATA_TRANSFER = 1;
    private int g, p, xa;

    public MessageMonitor() {
        currentState = HANDSHAKE;
    }

    public synchronized boolean hasNotConnected(){
        return (messages.isEmpty() && currentState == HANDSHAKE);
    }

    public synchronized void newMessage(DatagramPacket packet){
        byte[] msg = packet.getData();
        System.out.println("Recieved message: " + msg);
        messages.add(msg);
        notifyAll();
    }

    public synchronized void sendHandshakeHello(int port, DatagramSocket socket) throws Exception{
        byte[] message = new byte[5];
        message[0] = 1;
        message[1] = 5;
        message[2] = 10;
        message[3] = 11;
        message[4] = (byte) ((byte) Math.pow(message[2], 10) % message[3]);
        InetAddress IPAddress = InetAddress.getByName("localhost");
        DatagramPacket p = new DatagramPacket(
        message, message.length, IPAddress, port);
        socket.send(p);
    }

    public synchronized void sendMessage(int port, DatagramSocket socket) throws Exception {
        while (messages.isEmpty()) {
            System.out.println("wait!");
            wait();
        }
        parseMessage();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        String msg = "hej din port är: " + port;
        DatagramPacket p = new DatagramPacket(
        msg.getBytes(), msg.getBytes().length, IPAddress, port);
        socket.send(p);

    }

    private void parseMessage() throws Exception{
        byte[] message = messages.pop();

        switch(currentState){
            case HANDSHAKE:
            handshakeHello(message);
            currentState = DATA_TRANSFER;
            break;
            case DATA_TRANSFER:
            handleDataTransfer(message);
            break;
            default:
            break;
        }
    }

    private void handshakeHello(byte[] message) throws Exception{
        if(message[0] == 1){
            int lenght = message[1];
            g = message[2];
            p = message[3];
            xa = message[4];
            System.out.println("g: " + g + "p: " + p + "xa: " + xa);
        } else{
            throw new Exception("Wrong message type in handshake");
        }
    }

    private void handleDataTransfer(byte[] message){

    }

}
