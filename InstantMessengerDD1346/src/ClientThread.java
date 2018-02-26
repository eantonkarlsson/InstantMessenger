import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Denna kod beskriver de trådar som hanterar
 * anslutningar till MultiThreadedServerDemo
 * Varje tråd läser in sin klients meddelanden,
 * konverterar det till versaler och skickar
 * tillbaka det. Tråden avslutar när klienten
 * kopplar ner.
 */
public class ClientThread extends Thread{

    private Socket clientSocket = null;
    private PrintWriter out;
    private BufferedReader in;
    private ArrayList<Message> msgList = new ArrayList<>();
    private boolean done = false;

    // Konstruktorn sparar socketen lokalt
    public ClientThread(Socket sock){
	clientSocket = sock;
    }

    public void run(){

		try{
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		}catch(IOException e){
			System.out.println("getOutputStream failed: " + e);
			System.exit(1);
		}
		try{
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		}catch(IOException e){
			System.out.println("getInputStream failed: " + e);
			System.exit(1);
		}

		System.out.println("Connection Established: "
				   + clientSocket.getInetAddress());

		try{
			String firstMsg = in.readLine();
			if (firstMsg.equals("")){
				// print response if simple user
				out.println("Awaiting response on connection. New messages will not be received.");
			}
			if (!acceptingConnection(firstMsg)){
				done = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(!done){

			if (!msgList.isEmpty())
			{
				Message msg = msgList.remove(0);
				out.println(msg.returnEncryptedText());
			}
			try{
			String incomingMsg = in.readLine();
			if(incomingMsg==null){
				System.out.println("Client disconnect!");
				done = true;
			}else{
				//---------------------------- HERE WE HAVE A MSG -----------------------//
			}
			}catch(IOException e){
			System.out.println("readLine failed: " + e);
			System.exit(1);
			}
		}

		try{
			in.close();
			out.close();
			clientSocket.close();
		}catch(IOException e){}
		}


	private boolean acceptingConnection(String str) {

    	boolean decision = false;
    	if (str.startsWith("<request>") && str.endsWith("</request>")){
    		// check if user wants to accept advanced connection
			// block thread until decision has been made
		}
		else {
			// check if user wants to accept simple connection
			// block thread until decision has been made

		}

    	return decision;
	}

	private void newUser() {

	}

	public void newMessage(Message newMsg) {
		msgList.add(newMsg);
	}

	public void kill() {
    	done = true;
	}

	public String requestKey(String text) {
		return null;
	}
}