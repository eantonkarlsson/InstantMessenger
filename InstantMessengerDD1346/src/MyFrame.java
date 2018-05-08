
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.paint.Color.color;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MyFrame extends Thread{ //extends JFrame
    // defining variables
    private JFrame frame;
    private JPanel panel1;
    private JColorChooser colorChooser; 
    private User currentUser;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private Message message;
    private Session session;
    private User user;
    private JButton send;
    //private JButton file; 
    private JTextField textField;
    private JTextPane textArea;
    private JTabbedPane tabbedPane;
    private JTextField nameField;
    private JButton setSettings;
    public HashMap<JPanel, JTextPane> tabs = new HashMap<JPanel, JTextPane>();
    private boolean toggledColor = false;
    private MyFrame myFrame = this;
    private static String name; 
    private String newHexColor = "#000000";

    
    public void run() {    
        
        // create startpage
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        panel1 = new JPanel();
        JLabel label = new JLabel("Name:"); 
        nameField = new JTextField(20);
        panel1.add(label);
        panel1.add(nameField);
        JButton setName = new JButton("Set");
        panel1.add(setName);
        tabbedPane = new JTabbedPane();
        frame.add(panel1,BorderLayout.PAGE_START);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.pack();

        // set name and open connect settings
        setName.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                name = nameField.getText(); 
                
                panel1.remove(nameField);
                panel1.remove(label);
                panel1.remove(setName);
                
                
                JLabel adress = new JLabel("Adress:"); 
                JTextField adressField = new JTextField(20);
                panel1.add(adress);
                panel1.add(adressField);
                JLabel port = new JLabel("Port:"); 
                JTextField portField = new JTextField(20);
                panel1.add(port);
                panel1.add(portField);
               // String[] encryptions = { "None", "AES", "Caesarkrypto"};
               // JComboBox encryption = new JComboBox(encryptions);
               // panel1.add(encryption);
                setSettings = new JButton("Connect");
                panel1.add(setSettings);
                panel1.validate();
                panel1.repaint();
                
                // connect and open chat window
                setSettings.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        if (adressField.getText().length()!=0){ //skapar client

                            ClientThread clientThread = null;
                            ClientThread allClientThread = null;
                            try {
                                clientThread = new ClientThread(new Socket(adressField.getText(),
                                    Integer.parseInt(portField.getText())), true, false);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                allClientThread = new ClientThread(new Socket(adressField.getText(),
                                        Integer.parseInt(portField.getText()) + 1), true, false);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            ChatController cc = new ChatController();
                            ChatController allCC = new ChatController();
                            clientThread.addController(cc);
                            allClientThread.addController(allCC);
                            clientThread.addFrame(frame);
                            allClientThread.addFrame(frame);
                            JPanel all = makeTextPanel(allClientThread,cc);
                            JPanel temp = makeTextPanel(clientThread,cc);
                            clientThread.addPanel(tabs.get(temp));
                            allClientThread.addPanel(tabs.get(all));
                            clientThread.startWrapper(true);
                            allClientThread.startWrapper(true);

                        }
                        // connect as server
                        else {


                            Server thr1 = new Server(Integer.parseInt(portField.getText()));
                            thr1.addFrame(myFrame);
                            thr1.start();

                            
                        //   Server server = new Server(Integer.parseInt(portField.getText()));
                            
                             
                        }
                    }
                
                });    
            }
        });       

        frame.setPreferredSize(new Dimension(750,750));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.pack();
        frame.setVisible(true);
       
    } 

    public JPanel makeTextPanel(ClientThread clientThread, ChatController cc) {

        JPanel panel = makeTextPanelInternal(clientThread, cc);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLbl = new JLabel("Tab");
        titleLbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 5));
        titlePanel.add(titleLbl);
        JButton closeButton = new JButton("x");
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        closeButton.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                tabbedPane.remove(panel);
                clientThread.disconnect();
            }
        });
        titlePanel.add(closeButton);
        tabbedPane.addTab("Tab", panel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel),titlePanel);
        return panel;

    }

    // create tab
    public JPanel makeTextPanelInternal(ClientThread clientThread, ChatController cc) {

        JTextPane newtextArea = new JTextPane();
        JTextField newTextField = new JTextField(30);
        newtextArea.setPreferredSize(new Dimension(500,500));
        JScrollPane editorScrollPane = new JScrollPane(newtextArea); //scroll
        //editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JButton colorButton = new JButton("Color");
        //file = new JButton("File");
        JButton newSend = new JButton("Send");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
    
        
        panel.setBounds(0, 0, 600, 600);
        panel.setPreferredSize(new Dimension(600,600));
        tabs.put(panel, newtextArea);
        newtextArea.setEditable(false);
        
        panel.add(editorScrollPane, BorderLayout.CENTER);
        JPanel subPanel = new JPanel();
        
        subPanel.add(newTextField);
        //panel.add(file, BorderLayout.PAGE_END);
        subPanel.add(colorButton);
        subPanel.add(newSend);
        panel.add(subPanel, BorderLayout.SOUTH); 

        newSend.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e){
                clientThread.send(clientThread.returnController().createMessage(newTextField.getText()));
            }

        });
     
//        JFileChooser fileChooser = new JFileChooser();
//        file.addActionListener(new ActionListener() {
//        public void actionPerformed(ActionEvent e) {
//            int returnValue = fileChooser.showOpenDialog(panel1);
//            if (returnValue == JFileChooser.APPROVE_OPTION) {
//                File selectedFile = fileChooser.getSelectedFile();
//                System.out.println(selectedFile.getName());
//            }
//        }
//        });

        // choose color
        colorButton.setOpaque(true);  
        colorChooser = new JColorChooser(Color.BLACK); //defult color black
        colorButton.setBackground(Color.BLACK);
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color currentColor = colorChooser.showDialog(panel1,"Color", Color.BLACK );
                String hexColour = Integer.toHexString(currentColor.getRGB() & 0xffffff);
                if (hexColour.length() < 6) {
                    hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
                }
                newHexColor = "#" + hexColour;
                cc.setColor(newHexColor);
                colorButton.setBackground(currentColor);
            } 
        });
        return panel;
    }
    
    // create close button on tabs
    public JPanel getTitlePanel(JTabbedPane tabbedPane, JPanel panel, String title){
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 5));
        titlePanel.add(titleLbl);
        JButton closeButton = new JButton("x");
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));    

        closeButton.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent e){
                tabbedPane.remove(panel);
                }
            });
        titlePanel.add(closeButton);

        return titlePanel;
    }
     
    public void changeUser() {
    }
    
    public static String returnName(){
        return name; 
    }



    // run program
    public static void main(String[] args) {
     //   System.setProperty("apple.laf.useScreenMenuBar", "true");
        Thread thr2 = new Thread(new MyFrame());
        thr2.start();
        //MyFrame frame = new MyFrame();
    }

    void add(JScrollPane editorScrollPane) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
