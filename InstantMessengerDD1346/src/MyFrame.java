
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MyFrame extends JFrame {
    
    private JPanel panel1;
    private JPanel panel2;
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
    private JButton colorButton;
    private JTextField textField;
    private JTextArea textArea;
    private JTabbedPane tabbedPane;
    private JTextField nameField;
    private JButton setSettings;
    private boolean toggledColor = false;
    
    public MyFrame(){    
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
        
        panel1 = new JPanel();
        JLabel label = new JLabel("Name:"); 
        nameField = new JTextField(20);
        panel1.add(label);
        panel1.add(nameField);
        JButton setName = new JButton("Set");
        panel1.add(setName);
        JTabbedPane tabbedPane = new JTabbedPane();
        add(panel1,BorderLayout.PAGE_START);
        add(tabbedPane, BorderLayout.CENTER);
        pack();

        

        
        

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
                setSettings = new JButton("Set");
                panel1.add(setSettings);
                panel1.validate();
                panel1.repaint();
                
                setSettings.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){       
                        JComponent panel = makeTextPanel();
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

                        
                      //  tabbedPane.addTab(getTitlePanel(tabbedPane, panel, "Tab1"), panel);
                        //tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel), 
                      //  getTitlePanel(tabbedPane, panel, "Tab1"));
                  
                    }
       });
        
        
                
                
                
            }
        });
        
        
//        add(tabbedPane);
       
        
//        
        
 //       
//        JComponent panel2 = makeTextPanel();
//        tabbedPane.addTab("Tab 2", panel2);   
//        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel1), 
//                getTitlePanel(tabbedPane, panel1, "Tab2"));
////        
        
        
//        setJMenuBar(menuBar);
        
        
        
        setSize(1000,1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setVisible(true);
            
        
        
    } 
    
    public JComponent makeTextPanel() {
        textField = new JTextField(30);
        textArea = new JTextArea(35,90);
        file = new JButton("File");
        send = new JButton("Send");
        JPanel panel = new JPanel();
        textArea.setEditable(false);
        JScrollPane editorScrollPane = new JScrollPane(textArea); //scroll 
        
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
        colorButton = new JButton("Color");
        colorButton.setOpaque(true);  
        colorChooser = new JColorChooser(Color.BLACK); //defult color black
        colorButton.setBackground(Color.BLACK);
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                toggleColorChooser(); // show and hide the color chooser
                colorChooser.showDialog(panel1,"Color", Color.BLACK );
               

            } 
        });
        colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colorChanged(); // change background color of "button"
            }
        });
        
        panel.add(textArea); 
        panel.add(textField);
        panel.add(file);
        panel.add(colorButton);
        panel.add(send);
     

        return panel;
    }
    
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
    
    private void toggleColorChooser() {
    if (toggledColor) {
        panel1.remove(colorChooser);
    } else {
        colorChooser.setVisible(true);
        panel1.add(colorChooser);
    }
    toggledColor = !toggledColor;
    panel1.validate();
    panel1.repaint();
    }  
    
    private void colorChanged() {
        colorButton.setBackground(colorChooser.getSelectionModel().getSelectedColor());
}
     
    public void changeUser() {
    }

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        MyFrame frame = new MyFrame();
    }

	

}
