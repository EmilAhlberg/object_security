import java.util.LinkedList;

public class MessageMonitor{

  LinkedList<String> messages = new LinkedList<String>();

  public synchronized void NewMessage(String message){
    messages.add(message);
  }

  public synchronized String GetLatestMessage(){
    return messages.getFirst();
  }


}
