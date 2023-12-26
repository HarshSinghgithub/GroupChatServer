import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Client {
  Socket clientSocket;
  BufferedReader bufferedReader;
  BufferedWriter bufferedWriter;

  Client(Socket socket, String name) {
    try {
      clientSocket = socket;
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      synchronized(bufferedWriter){
        bufferedWriter.write(name);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    } 
    catch (IOException e) {
      System.out.println("Client object not created");
      e.printStackTrace();
    }
  }

  public void SendMessage(String message) {
    try {
      synchronized(bufferedWriter){
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    } 
    catch (IOException e) {
      System.out.println("SERVER has stopped responding");
      closeAll(clientSocket, bufferedReader, bufferedWriter);
    }
  }

  public void ReadMessage(){
    new Thread(
      new Runnable() {
        @Override
        public void run(){
          try{
            String message;
            while(clientSocket.isConnected()){
              message = bufferedReader.readLine();

              if(message == null){
                break;
              }
              
              System.out.println(message);
            }
          }
          catch(IOException e){
            e.printStackTrace();
            System.out.println("SERVER has stopped responding");
          }
          finally{
            System.out.println("SERVER is disconnected");
            closeAll(clientSocket, bufferedReader, bufferedWriter);
          }
        }
      }
    ).start();
  }

  public static void closeAll(Socket socket, BufferedReader buffReader, BufferedWriter buffWriter) {
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

      System.exit(0);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter your name");
    System.out.flush();
    String name = sc.nextLine();
    System.out.println("Connecting to Server");

    try{
      InetAddress localhost = InetAddress.getLocalHost();
      Socket clinetSocket = new Socket(localhost.getHostAddress(), 5000);
      System.out.println("Connection established");
      Client client = new Client(clinetSocket, name);
      client.ReadMessage();
      
      while (true) {
        System.out.println("Enter your message (or type 'exit' to quit): ");
        System.out.flush();
        String message = sc.nextLine();

        if(message.equalsIgnoreCase("exit")){
          break;
        }

        client.SendMessage(message);
      }
    }
    catch(IOException e){
      e.printStackTrace();
      System.out.println("Connection Failed");
    }
    finally{
      sc.close();
      System.out.println("Disconnecting from SERVER");
      System.exit(0);
    }
  }
}