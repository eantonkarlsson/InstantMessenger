import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    private boolean isRequesting = false;
    private boolean firstTime = true;
    private ChatController cc;
    private ChatController allcc;
    private JFrame myFrame;
  
    

    // Konstruktorn sparar socketen lokalt
    public ClientThread(Socket sock, boolean requestor){
		clientSocket = sock;
		isRequesting = requestor;
	}

    public void addFrame(JFrame frame){
    	myFrame = frame;
	}

	public void addPanel(JTextPane panel){
    	cc.addPanel(panel);
	}

	public void addController(ChatController chatController)
	{
		cc = chatController;
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

		if (firstTime) {
			if (isRequesting) {
				requestAccess(cc.returnName());
			}

			System.out.println("Connection Established: "
					+ clientSocket.getInetAddress());

			if (!isRequesting) {
				boolean loopMe = false;
				while (!loopMe) {

					try {
						String firstMsg = in.readLine();
						if (firstMsg.equals("")) {
							// print response if simple user
							out.println("Awaiting response on connection. New messages will not be received.");
						}
						if (acceptingConnection(firstMsg)) {
							out.println("<request reply=\"yes\"> </request>");
							loopMe = true;
						} else {
							out.println("<request reply=\"no\"> </request>");
							loopMe = true;
							done = true;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}
		firstTime = false;

		while(!done){

			try{
				String incomingMsg = in.readLine();
				if(incomingMsg==null){
					System.out.println("Client disconnect!");
					done = true;
				}else{

					String newMsg = cc.deTransformMessage(incomingMsg);
					System.out.println(newMsg);
			}
			}catch(IOException e){
			System.out.println("readLine failed: " + e);
			System.exit(1);
			} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | ParserConfigurationException | SAXException | BadPaddingException | IllegalBlockSizeException e) {
				e.printStackTrace();
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
			Object[] options = {"Accept", "Decline"};
			int n = JOptionPane.showOptionDialog(myFrame,
					"User " + str.substring(9, str.length()-10)
							+ " would like to connect.",
					"Incoming connection",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
			if (n == JOptionPane.YES_OPTION){
				decision = true;
				// cc = new ChatController(new User("anton"));
			}
			else{
				decision = false;
			}
		}
		else {
			// check if user wants to accept simple connection
			// block thread until decision has been made

        }

    	return decision;
	}
        


	private void newUser() {

	}

	public ChatController requestAccessWrapper(String userID)
	{
		requestAccess(userID);
		return cc;

	}

	public void requestAccess(String userID) {

		try{
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			out.println("<request>" + userID + "</request>");
		}catch(IOException e){
			System.out.println("getOutputStream failed: " + e);
			System.exit(1);
		}

		boolean loopMe = true;
		while (loopMe) {
			try {
				String msg = in.readLine();
				if (msg.startsWith("<request") && msg.endsWith("</request>")) {
					Document xml = XMLHandler.StringToXML(msg);
					String response = xml.getElementsByTagName("request").item(0).getAttributes().item(0).toString();
					System.out.println(response);
					if (response.endsWith("yes\"")){
						// cc = new ChatController(new User(userID));
						// allcc = new ChatController(new User("hi"));
						loopMe = false;
						firstTime = false;

					}
					else if (response == "no"){
						loopMe = false;
						firstTime = false;
						done = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(String newMsg) {
            String outgoing = cc.createMessage(newMsg);
            out.println(outgoing);
	}

	public void kill() {
    	done = true;
	}

	public String requestKey(String text) {
		return null;
	}
}
