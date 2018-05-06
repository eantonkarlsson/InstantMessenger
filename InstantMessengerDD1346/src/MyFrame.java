
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private JButton file; 
    private JTextField textField;
    private JTextPane textArea;
    private JTabbedPane tabbedPane;
    private JTextField nameField;
    private JButton setSettings;
    private boolean toggledColor = false;
    
    public void run() {    
//        menuBar = new JMenuBar();
//        menu = new JMenu("User");
//        menuItem = new JMenuItem("Name");
//        menu.add(menuItem);
//        menuItem = new JMenuItem("Color");
//        menu.add(menuItem);
//        menuBar.add(menu);
//        menu = new JMenu("Server");
//        menuBar.add(menu);
//        menu = new JMenu("Client");
//        menuBar.add(menu);
        
        // create startpage
        frame = new JFrame();
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
                String name = nameField.getText(); 
                currentUser = new User(name);
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
                String[] encryptions = { "None", "AES", "Caesarkrypto"};
                JComboBox encryption = new JComboBox(encryptions);
                panel1.add(encryption);
                setSettings = new JButton("Connect");
                panel1.add(setSettings);
                panel1.validate();
                panel1.repaint();
                
                // connect and open chat window
                setSettings.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        if (adressField.getText().length()!=0){

                            ClientThread clientThread = null;
                            try {
                                clientThread = new ClientThread(new Socket(adressField.getText(),
                                    Integer.parseInt(portField.getText())));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            clientThread.addFrame(frame);
                            clientThread.start();
                            ChatController cc = clientThread.requestAccess(currentUser.returnName());
                            JComponent panel = makeTextPanel(clientThread);
                            cc.addPanel(panel);

                        }
                        // connect as server
                        else {


                            Thread thr1 = new Thread(new Server(Integer.parseInt(portField.getText())));
                            thr1.start();
                            
                        //   Server server = new Server(Integer.parseInt(portField.getText()));
                            
                             
                        }
                      //  JComponent panel = makeTextPanel();
//                        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//                        titlePanel.setOpaque(false);
//                        JLabel titleLbl = new JLabel("Tab");
//                        titleLbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 5));
//                        titlePanel.add(titleLbl);
//                        JButton closeButton = new JButton("x");
//                        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
//
//                        closeButton.addMouseListener(new MouseAdapter(){
//                            public void mouseClicked(MouseEvent e){
//                            tabbedPane.remove(panel);
//                            }
//                        });
//                        titlePanel.add(closeButton);
//                        tabbedPane.addTab("Tab", panel);
//                        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel),titlePanel);
                    }
                });    
            }
        });       
           
//        setJMenuBar(menuBar);
                
        frame.setSize(1000,1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setVisible(true);
       
    } 

    public JComponent makeTextPanel(ClientThread clientThread) {

        JComponent panel = makeTextPanelInternal(clientThread);
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
            }
        });
        titlePanel.add(closeButton);
        tabbedPane.addTab("Tab", panel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel),titlePanel);
        return panel;

    }

    // create tab
    public JComponent makeTextPanelInternal(ClientThread clientThread) {

        textArea = new JTextPane();
        textField = new JTextField(30);
        textArea.setPreferredSize(new Dimension(1000,550));
        JButton colorButton = new JButton("Color");
        file = new JButton("File");
        send = new JButton("Send");
        JPanel panel = new JPanel();
        textArea.setEditable(false);
        JScrollPane editorScrollPane = new JScrollPane(textArea); //scroll 
        
        panel.add(textArea);
        panel.add(editorScrollPane);
        panel.add(textField, BorderLayout.PAGE_END);
        panel.add(file, BorderLayout.PAGE_END);
        panel.add(colorButton, BorderLayout.PAGE_END);
        panel.add(send, BorderLayout.PAGE_END);

        send.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e){
                clientThread.send(textField.getText());
            }

        });
     
        JFileChooser fileChooser = new JFileChooser();
        file.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int returnValue = fileChooser.showOpenDialog(panel1);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println(selectedFile.getName());
            }
        }
        });

        // choose color
        colorButton.setOpaque(true);  
        colorChooser = new JColorChooser(Color.BLACK); //defult color black
        colorButton.setBackground(Color.BLACK);
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color newColor = colorChooser.showDialog(panel1,"Color", Color.BLACK );

                colorButton.setBackground(newColor);
            } 
        });
        return panel;
    }
    
    // create close button on tabs
    public JPanel getTitlePanel(JTabbedPane tabbedPane,JPanel panel, String title){
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

    // run program
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Thread thr2 = new Thread(new MyFrame());
        thr2.start();
        //MyFrame frame = new MyFrame();
    }


}
