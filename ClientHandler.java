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
  private BufferedReader buffReader;
  private BufferedWriter buffWriter;
  private String name;

  ClientHandler(Socket socket){
    try{
      this.socket = socket;
      this.buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.name = buffReader.readLine();
      clientHandlers.add(this);
      broadcastMessage("SERVER " + name + "has joined the chat");
    }
    catch(IOException e){
      closeAll(socket, buffReader, buffWriter);
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    String message = "";

    while (socket.isConnected()) {
      try {
        message = buffReader.readLine();
        if(message == null){
          break;
        }
        broadcastMessage(name + ": " + message);
      } catch (IOException e) {
        closeAll(socket, buffReader, buffWriter);
      }
    }
  }

  public void broadcastMessage(String message){
    for(ClientHandler clienthandler : clientHandlers){
      try{
        if(this.name != clienthandler.name){
          clienthandler.buffWriter.write(message);
          clienthandler.buffWriter.newLine();
          clienthandler.buffWriter.flush();
        }
      }
      catch(IOException e){
        closeAll(socket, buffReader, buffWriter);
      }
    }
  }

  public void removeClienthandler() {
    clientHandlers.remove(this);
    broadcastMessage("SERVER " + this.name + "has left");
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