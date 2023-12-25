import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Client {
  Socket socket;
  BufferedReader bufferedReader;
  BufferedWriter bufferedWriter;
  private String name;

  Client(Socket socket, String name) {
    try {
      this.socket = socket;
      this.name = name;
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    } catch (IOException e) {
      System.out.println("Client object not created");
      e.printStackTrace();
    }
  }

  public void SendMessage() {
    try {
      bufferedWriter.write(name);
      bufferedWriter.newLine();
      bufferedWriter.flush();

      Scanner sc = new Scanner(System.in);

      while (socket.isConnected()) {
        synchronized(bufferedWriter){
        String message = sc.nextLine();
        bufferedWriter.write(name + "." + message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    }

    sc.close();
    } catch (IOException e) {
      closeAll(socket, bufferedReader, bufferedWriter);
      e.printStackTrace();
    }
  }

  public void ReadMessage(){
    new Thread(
      new Runnable(){
        @Override
        public void run(){
          String message;

          while(socket.isConnected()){
            try{
              message = bufferedReader.readLine();
              if(message == null){
                System.out.println("Server disconnected. Exiting...");
                break;
              }
              System.out.print(message);
            }
            catch(IOException e){
              closeAll(socket, bufferedReader, bufferedWriter);
              e.printStackTrace();
            }
          }
        }
      }).start();
  }

  public void closeAll(Socket socket, BufferedReader buffReader, BufferedWriter buffWriter) {
    try {
      if (buffReader != null) {
        buffReader.close();
      }
      if (buffWriter != null) {
        buffWriter.close();
      }
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter your name");
    System.out.flush();
    String name = sc.nextLine();
    sc.close();
    System.out.println("Connecting to Server");

    try{
      InetAddress localhost = InetAddress.getLocalHost();
      Socket socket = new Socket(localhost.getHostAddress(), 5000);
      System.out.println("Connection established");
      Client client = new Client(socket, name);
      client.ReadMessage();
      client.SendMessage();
    }
    catch(IOException e){
      e.printStackTrace();
      System.out.println("Connection Failed");
    }
  }
}