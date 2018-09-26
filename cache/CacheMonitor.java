import java.util.LinkedList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class CacheMonitor {

    private HashMap<Integer, LinkedList <byte[]>> messages;
    private LinkedList<Integer> dataIsRequested;

    public CacheMonitor() {
        //hard coded ports, proof-of-concept!
        messages = new HashMap<Integer, LinkedList<byte[]>>();
        messages.put(3000, new LinkedList<byte[]>());
        messages.put(4000, new LinkedList<byte[]>());
        dataIsRequested = new LinkedList <Integer>();
    }


    public synchronized void newMessage(DatagramPacket packet){
        byte[] msg = packet.getData();
        int destPort = MessageFactory.parseIntFromByte(msg, MessageFactory.PROTOCOL_POS_DEST_PORT);
        if (msg[MessageFactory.PROTOCOL_POS_MSG_TYPE] == MessageFactory.TYPE_FOUR) {
            System.out.println("Cache was polled from port: " + destPort);
            dataIsRequested.add(destPort);
            notifyAll();
        } else {
            System.out.println("Message recieved heading to port: " + destPort);
            messages.get(destPort).add(msg);
        }
    }

    public synchronized void sendMessage(DatagramSocket socket) throws Exception{
        while (dataIsRequested.isEmpty()) {
            wait();
        }
        //get the destination
        int destPort = dataIsRequested.pop();
        LinkedList<byte[]> bufferedMessages = messages.get(destPort);
        //send all buffered messages for the specified port
        while(!bufferedMessages.isEmpty()) {
            System.out.println("sending to: " + destPort);
            byte[] message = bufferedMessages.pop();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            DatagramPacket p = new DatagramPacket(
            message, message.length, IPAddress, destPort);
            socket.send(p);
        }
    }


}
