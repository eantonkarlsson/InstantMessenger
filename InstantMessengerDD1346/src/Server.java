
import java.io.IOException;
import java.net.*;

public class Server extends Thread{
    private ServerSocket serverSocket;
    private User[] users;
    private User user;
    private int port;

    public Server(int port) {
        this.port = port;
        // Start server socket
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could not listen on port:" + port);
            System.exit(-1);
        }
    }
    
    public void run(){

        // Listen after clients
        while(true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                
            } catch (IOException e) {
                System.out.println("Accept failed:"+port);
                System.exit(-1);
            }
            Thread thr = new ClientThread(clientSocket);
            thr.start();
        }
    }


    public void incomingConnection() {
    }

    public void incomingFile() {
    }

    public void addUser() {
    }

}
