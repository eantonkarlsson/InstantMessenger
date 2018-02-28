import java.io.*;
import java.net.*;

public class Client {
    public Client(String Adress, int port) {
        // Socket som ansluter till servern
        Socket clientSocket = null;

	// StrÃ¶mmar fÃ¶r att lÃ¤sa frÃ¥n/skriva till servern
        PrintWriter out = null;
        BufferedReader in = null;
        
        // Anslut till server:
        try {
            clientSocket = new Socket(Adress, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n" + e);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to host.\n" + e);
            System.exit(1);
        }
	// Kommer vi hit har anslutningen gÃ¥tt bra
	System.out.println("Connection successful!");
    }
 // Replaced with ClientThread for a multithreaded client

}

