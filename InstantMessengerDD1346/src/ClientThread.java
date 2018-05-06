import org.xml.sax.SAXException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
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
    private ChatController cc;
    private ChatController allcc;
    private JFrame myFrame;
    private boolean accepted = false; 
    

    // Konstruktorn sparar socketen lokalt
    public ClientThread(Socket sock){
		clientSocket = sock;
	}

    public void addFrame(JFrame frame){
    	myFrame = frame;
	}
    
    public boolean returnAccepted(){
        return accepted; 
    }
    
    public ChatController returnChatController(){
        return cc; 
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

        while(!done){

            try {
                String firstMsg = in.readLine();
                if (firstMsg.equals("")){
                        // print response if simple user
                        out.println("Awaiting response on connection. New messages will not be received.");
                }
                if (acceptingConnection(firstMsg)){
                        done = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        done = false;

        while(!done){

//			if (!msgList.isEmpty())
//			{
//				Message msg = msgList.remove(0);
//				out.println(msg.returnEncryptedText());
//			}
            try{
            String incomingMsg = in.readLine();
                if(incomingMsg==null){
                    System.out.println("Client disconnect!");
                    done = true;
                }else{
                //---------------------------- HERE WE HAVE A MSG -----------------------//
                    try {
                        String newMsg = cc.deTransformMessage(incomingMsg);
                        System.out.println(newMsg);
                    } catch (ParserConfigurationException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | SAXException e) {
                        e.printStackTrace();
                    }
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
            Object[] options = {"Accept", "Decline"};
            int n = JOptionPane.showOptionDialog(myFrame,"User " + str+ 
                    " would like to connect.","Incoming connection",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
            if (n == JOptionPane.YES_OPTION){
                decision = true;
                cc = new ChatController(new User("anton"));  //fixa user
                accepted = true; 


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

	public ChatController requestAccess(String userID) {
            try{
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            }catch(IOException e){
                System.out.println("getOutputStream failed: " + e);
                System.exit(1);
            }
            out.println("<request>" + userID + "</request>");
            cc = new ChatController(new User(userID));
            return cc;
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