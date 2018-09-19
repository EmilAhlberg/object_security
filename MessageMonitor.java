import java.util.LinkedList;

public class MessageMonitor{

    LinkedList<String> messages = new LinkedList<String>();

    public synchronized void NewMessage(String message){
        messages.add(message);
        notifyAll();
    }

    public synchronized String GetLatestMessage(){
        while(messages.isEmpty()){
            wait();
        }
        return messages.getFirst();
    }

}
