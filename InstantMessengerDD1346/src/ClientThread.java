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
    private boolean allChat;
    private Server serv;

    

    // Konstruktorn sparar socketen lokalt
    public ClientThread(Socket sock, boolean requestor, boolean isAllChat){
		clientSocket = sock;
		isRequesting = requestor;
		allChat = isAllChat;
	}

	public void acceptConnection() {firstTime = false;}

	public void setServer(Server server){ serv = server; }

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

	public ChatController returnController(){ return cc; }

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


		while(!done){

			try{
				String incomingMsg = in.readLine();
				if(incomingMsg==null){
					System.out.println(cc.returnName()+" disconnected!");
					done = true;
				}else{

					Message newMsg = cc.deTransformMessage(incomingMsg);
					if (allChat){
						serv.sendToAll(newMsg);
					}
					else if (serv != null){
						send(newMsg);
					}
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
		}catch(IOException e){
			e.printStackTrace();
		}
		}

	public void startWrapper(boolean isRequesting){
		try{
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		}catch(IOException e){
			System.out.println("getOutputStream failed: " + e);
			System.exit(1);
		}
		if (isRequesting) {
			requestAccess(cc.returnName());
		}
		else {
			long endTime = System.currentTimeMillis() + 3000;
			while (firstTime) {
				// Assume simple user if no request message within 3 seconds
				if (System.currentTimeMillis() > endTime) {
					if (incomingConnection("")) {
						out.println("<request reply=\"yes\"> </request>");
						firstTime = false;
					} else {
						out.println("<request reply=\"no\"> </request>");
						done = true;
						firstTime = false;
					}
				} else {
					try {
						String firstMsg = in.readLine();
						if (incomingConnection(firstMsg)) {
							out.println("<request reply=\"yes\"> </request>");
							firstTime = false;
						} else {
							out.println("<request reply=\"no\"> </request>");
							done = true;
							firstTime = false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				this.start();
			}
		}
	}

    private boolean incomingConnection(String str) {

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
			return n == JOptionPane.YES_OPTION;
		}
		else {
			Object[] options = {"Accept", "Decline"};
			int n = JOptionPane.showOptionDialog(myFrame,
					"A simple user would like to connect.",
					"Incoming connection",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
			return n == JOptionPane.YES_OPTION;
        }
	}
        


	private void newUser() {

	}

	public ChatController requestAccessWrapper(String userID)
	{
		requestAccess(userID);
		return cc;

	}

	public void requestAccess(String userID) {

		out.println("<request>" + userID + "</request>");
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
					else if (response.endsWith("no\"")){
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

	public void send(Message newMsg) {
            String outgoing = newMsg.returnXML();
            out.println(outgoing);
	}

        public void disconnect(){
            String msg = (cc.returnName()+" logged out"+" <disconnect/>");
            out.println(msg);

        }





	public String requestKey(String text) {
		return null;
	}
}
