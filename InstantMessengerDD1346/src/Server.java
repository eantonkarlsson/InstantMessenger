
import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Server extends Thread{
    private ServerSocket serverSocket;
    private ServerSocket allServerSocket;
    private User[] users;
    private User user;
    private int port;
    private MyFrame mainFrame;
    private ArrayList<ClientThread> allThreads = new ArrayList<>();
    private JPanel all = null;
    private ChatController allCC = null;

    public Server(int port) {
        this.port = port;
        // Start server socket
        try {
            serverSocket = new ServerSocket(port);
            allServerSocket = new ServerSocket(port+1);
        } catch (IOException e) {
            System.out.println("Could not listen on ports:" + port + ", " + (port + 1));
            System.exit(-1);
        }
    }

    public void addFrame(MyFrame myFrame){
        mainFrame = myFrame;
    }
    
    public void run(){

        // Listen after clients
        while(true){
            Socket clientSocket = null;
            Socket allClientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Accept failed:"+port);
                System.exit(-1);
            }
            try {
                allClientSocket = allServerSocket.accept();
            } catch (IOException e) {
                System.out.println("Accept failed:"+port);
                System.exit(-1);
            }
            ClientThread thr = new ClientThread(clientSocket, false, false);
            ClientThread allThr = new ClientThread(allClientSocket, false, true);
            thr.setServer(this);
            //multi-chat
            allThr.setServer(this);
            allThreads.add(allThr);
            ChatController cc = new ChatController();

            if (all == null){
                allCC = new ChatController();
                all = mainFrame.makeTextPanel(allThr, allCC);
                allThr.addController(allCC);
                allThr.addPanel(mainFrame.tabs.get(all));

            }
            allThr.addController(allCC);
            JPanel temp = mainFrame.makeTextPanel(thr, cc);
            thr.addController(cc);
            thr.addPanel(mainFrame.tabs.get(temp));

            thr.startWrapper(false);
            allThr.startWrapper(false);
           
        }
    }

    public void sendToAll(Message msg){
        for (ClientThread thr: allThreads){
            thr.send(msg);
        }
    }

}
