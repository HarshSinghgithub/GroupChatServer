import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ClientHandler implements Runnable {
  public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String name;

  ClientHandler(Socket socket){
    try{
      this.socket = socket;
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.name = bufferedReader.readLine();
      clientHandlers.add(this);
      System.out.println("SERVER " + name + " is connected");
      broadcastMessage("SERVER: " + name + " has joined the chat");
    }
    catch(IOException e){
      closeAll(socket, bufferedReader, bufferedWriter);
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    String message;

    try{
      while(socket.isConnected()){
        message = bufferedReader.readLine();
        if(message == null){
          break;
        }
        else{
          broadcastMessage(name + " : " + message);
        }
      }
    }
    catch(IOException e){
      System.out.println("SERVER : " + name + " has stopped responding");
    }
    finally{
      System.out.println("SERVER : " + name + " left");
      closeAll(socket, bufferedReader, bufferedWriter);
    }
  }

  public void broadcastMessage(String message){
    for(ClientHandler clienthandler : clientHandlers){
      try{
        if(!clienthandler.name.equals(name)){
          clienthandler.bufferedWriter.write(message);
          clienthandler.bufferedWriter.newLine();
          clienthandler.bufferedWriter.flush();
        }
      }
      catch(IOException e){
        e.printStackTrace();
        closeAll(socket, bufferedReader, bufferedWriter);
      }
    }
  }

  public void removeClienthandler() {
    broadcastMessage("SERVER : " + name + " has left");
    clientHandlers.remove(this);
  }

  public void closeAll(Socket socket, BufferedReader buffreader, BufferedWriter buffwriter){
    removeClienthandler();
    try{
      if(socket != null){
        socket.close();
      }

      if(buffreader != null){
        buffreader.close();
      }

      if(buffwriter != null){
        buffwriter.close();
      }
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
}