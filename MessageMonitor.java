import java.util.LinkedList;

public class MessageMonitor{

    LinkedList<String> messages = new LinkedList<String>();

    public synchronized void NewMessage(String message){
        messages.add(message);
        notifyAll();
    }

    public synchronized String GetLatestMessage(){
     try{
        while(messages.isEmpty()){
            wait();
        }
        return messages.getFirst();
       }
       catch(InterruptedException e) {
        System.out.println(e.getMessage());
       }
       return null;
    }

}
