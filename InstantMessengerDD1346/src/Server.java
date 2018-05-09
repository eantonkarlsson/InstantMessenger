
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    private ServerSocket serverSocket;
    private ServerSocket allServerSocket;
    private int port;
    private MyFrame mainFrame;
    private ArrayList<ClientThread> allThreads = new ArrayList<>();
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

    public void addFrame(MyFrame myFrame)
    {
        mainFrame = myFrame;
    }
    
    public void run(){
        // Listen after clients
        while(true){
            try {
                // Thread/Socket for solo chat
                Socket clientSocket = serverSocket.accept();
                ClientThread thr = new ClientThread(clientSocket, false);
                thr.setServer(this);
                ChatController cc = new ChatController();
                thr.addController(cc);
                thr.startWrapper(false, mainFrame);

            } catch (IOException e) {
                System.out.println("Accept failed:"+port);
                System.exit(-1);
            }
            try {
                // Thread/Socket for all-chat
                Socket allClientSocket = allServerSocket.accept();
                ClientThread allThr = new ClientThread(allClientSocket, true);
                allThr.setServer(this);
                allThreads.add(allThr);
                if (allCC == null){
                    allCC = new ChatController();
                }
                allThr.addController(allCC);
                allThr.startWrapper(false, mainFrame);
            } catch (IOException e) {
                System.out.println("Accept failed:"+port);
                System.exit(-1);
            }
        }
    }

    public void sendToAll(Message msg){
        for (ClientThread thr: allThreads){
            thr.echo(msg);
        }
       //allCC.updatePanel(msg);
    }

}
