import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  private final ServerSocket serverSocket;
  private final ExecutorService executorService;

  Server(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
    this.executorService = Executors.newCachedThreadPool();
  }

  public void serverStart() {
    try {
      String thread = Thread.currentThread().getName();
      while (!serverSocket.isClosed()) {
        System.out.println("Thread: " + thread + " waiting for Clinet");
        Socket serversidedClientSocket = serverSocket.accept();
        
        ClientHandler clientHandler = new ClientHandler(serversidedClientSocket);

        executorService.submit(clientHandler);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void serverClose(){
    try{
      serverSocket.close();
      executorService.shutdown();
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(5000);
      System.out.println("Starting SERVER....");
      Server server = new Server(serverSocket);
      System.out.println("SERVER Started");
      System.out.flush();
      new Thread(() -> server.serverStart()).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
