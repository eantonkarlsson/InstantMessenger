import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    private boolean done = false;
    private boolean firstTime = true;
    private ChatController cc;
    private JFrame jFrame;
    private boolean allChat;
    private Server serv;
    private static JPanel allPanel = null;

    

    // Konstruktorn sparar socketen lokalt
    public ClientThread(Socket sock, boolean isAllChat){
		clientSocket = sock;
		allChat = isAllChat;
	}

	public void acceptConnection() {firstTime = false;}

	public void setServer(Server server){ serv = server; }

    public void addFrame(JFrame frame){
    	jFrame = frame;
	}

	public void addController(ChatController chatController)
	{
		cc = chatController;
	}

	public ChatController returnController(){ return cc; }

    public void run(){
		// Setup
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
				// This means the user on the other end ended the communication
				if(incomingMsg==null){
					System.out.println("User disconnected!");
					done = true;
				}
				else{
					Message newMsg = cc.deTransformMessage(incomingMsg);
					// If multi-part server -> Echo message to all
					if (allChat){
						serv.sendToAll(newMsg);
					}
					// If single-part server -> Echo message to sender
					else{
						if (serv != null){
							echo(newMsg);
						}
					}
				}
			}catch(IOException e){
			System.out.println("readLine failed: " + e);
			System.exit(1);
			}catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | ParserConfigurationException | SAXException | BadPaddingException | IllegalBlockSizeException e) {
				e.printStackTrace();
			}
		}

		// If we arrive here the communication should end
		killThread();


		}

	public void startWrapper(boolean isRequesting, MyFrame myFrame){
    	// Setup
		try{
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		}catch(IOException e){
			System.out.println("getOutputStream failed: " + e);
			System.exit(1);
		}
		// If client, request access
		if (isRequesting) {
			if(requestAccess(cc.returnName())){
				JPanel temp = myFrame.makeTextPanel(this, cc);
				cc.addPanel(myFrame.tabs.get(temp));
				this.start();
			}
			else{
				killThread();
			}
		}
		// If server, respond to access requests
		else {
			long endTime = System.currentTimeMillis() + 3000;
			while (true) {
				// Assume simple user if no request message within 3 seconds
				if (System.currentTimeMillis() > endTime) {
					if (incomingConnection("")) {
						// Print welcome message and set up chat panel
						out.println("Connection accepted");
						if (allChat){
							if (allPanel == null){
								allPanel = myFrame.makeTextPanel(this, cc);
								cc.addPanel(myFrame.tabs.get(allPanel));
							}
						}
						else {
							if (myFrame != null) {
								JPanel temp = myFrame.makeTextPanel(this, cc);
								cc.addPanel(myFrame.tabs.get(temp));
							}
						}
						this.start();
						break;
					} else {
						out.println("Connection denied");
						killThread();
						break;
					}
				} else {
					try {
						String firstMsg = in.readLine();
						if (incomingConnection(firstMsg)) {
							out.println("<request reply=\"yes\"> </request>");
							if (allChat){
								if (allPanel == null){
									allPanel = myFrame.makeTextPanel(this, cc);
									cc.addPanel(myFrame.tabs.get(allPanel));
								}
							}
							else {
								if (myFrame != null) {
									JPanel temp = myFrame.makeTextPanel(this, cc);
									cc.addPanel(myFrame.tabs.get(temp));
								}
							}
							this.start();
							break;
						} else {
							out.println("<request reply=\"no\"> </request>");
							killThread();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

    private boolean incomingConnection(String str) {
		// Respond to connection request
		// From an user implementing the B1 criteria
		if (str.startsWith("<request>") && str.endsWith("</request>")){
			Object[] options = {"Accept", "Decline"};
			int n = JOptionPane.showOptionDialog(jFrame,
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
		// From a simple user
		else {
			Object[] options = {"Accept", "Decline"};
			int n = JOptionPane.showOptionDialog(jFrame,
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

	private boolean requestAccess(String userID) {
		// Send out message request
		out.println("<request>" + userID + "</request>");
		while (true) {
			try {
				String msg = in.readLine();
				if (msg.startsWith("<request") && msg.endsWith("</request>")) {
					Document xml = XMLHandler.StringToXML(msg);
					String response = xml.getElementsByTagName("request").item(0).getAttributes().item(0).toString();
					if (response.endsWith("yes\"")){
						return true;
					}
					else if (response.endsWith("no\"")){
						return false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(Message newMsg) {
		// If sending from the multi-part server thread, we need to send it to all
		if (allChat){
			serv.sendToAll(newMsg);
		}
		// If any other thread, send regularly
		else{
			String outgoing = newMsg.returnXML();
			out.println(outgoing);
		}
		// If we are sending from a server, the message will not get echoed and needs to be printed manually
		if (serv != null){
			cc.updatePanel(newMsg);
		}
	}

	public void echo(Message newMsg) {
		String outgoing = newMsg.returnXML();
		out.println(outgoing);
	}

	public void disconnect(){
		String outStr = (cc.returnName() + " logged out" + " <disconnect/>");
		Message msg = cc.createMessage(outStr);
		send(msg);
		killThread();
	}

	private void killThread(){
		try{
			in.close();
			out.close();
			clientSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
