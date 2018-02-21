
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;


public class MyFrame extends JFrame {
    
    private JPanel panel;
    //private Color textColor;
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
    private JTextArea textArea;
    private JTabbedPane tabbedPane;
    
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
        
        JTextField nameField = new JTextField(20);
   //     add(nameField);
    //    JButton color = new JButton("Color");
    //    add(color);
    //    JButton setsettings = new JButton("Set");
        
        
        JTabbedPane tabbedPane = new JTabbedPane();
        JComponent panel1 = makeTextPanel();
        tabbedPane.addTab("Tab 1",panel1);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel), 
                getTitlePanel(tabbedPane, panel, "Tab1"));
        JComponent panel2 = makeTextPanel();
        tabbedPane.addTab("Tab 2", panel2);   
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel), 
                getTitlePanel(tabbedPane, panel, "Tab2"));
        
        
        add(tabbedPane);
        setSize(1000,1000);
//        setJMenuBar(menuBar);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setVisible(true);
    } 
    
    public JComponent makeTextPanel() {
        textField = new JTextField(40);
        textArea = new JTextArea(30,60);
        file = new JButton("File");
        send = new JButton("Send");
        panel = new JPanel();
        textArea.setEditable(false);
        JScrollPane editorScrollPane = new JScrollPane(textArea); //scroll 
        
        JFileChooser fileChooser = new JFileChooser();
        file.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int returnValue = fileChooser.showOpenDialog(panel);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println(selectedFile.getName());
            }
        }
        });
        
        panel.add(textArea); 
        panel.add(textField);
        panel.add(file);
        panel.add(send);
     
        pack();
        return panel;
    }
    
    private static JPanel getTitlePanel(final JTabbedPane tabbedPane, 
            final JPanel panel, String title){
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 5));
        titlePanel.add(titleLbl);
        JButton closeButton = new JButton("x");
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        

        closeButton.addMouseListener(new MouseAdapter(){
        @Override
        public void mouseClicked(MouseEvent e){
            tabbedPane.remove(panel);
        }
        });
        titlePanel.add(closeButton);

        return titlePanel;
    }
     
    public void changeUser() {
    }

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        MyFrame frame = new MyFrame();
    }

	

}
