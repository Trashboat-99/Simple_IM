package Client;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//this would represent the Home PC. This program only connects to one specific computer. 

public class Client extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//GUI variables
	private JButton btn_joinchat;
    private JButton btn_send;
    private JScrollPane chatWindow;
    private JLabel labelName;
    private JTextArea chatArea;
    private JTextField textChat;
    private JTextField textUsername; 
 
    
    ArrayList<String> users = new ArrayList<String>(); 
	
	String username, address="localhost"; 
	Socket connection; //a socket is a term in java for a connection between computers for networks
	int port = 6789;
	Boolean isConnected = false; 
	
	BufferedReader br;  //for reading input
	PrintWriter writer; //for reading output 
	ObjectOutputStream output; 
	ObjectInputStream input; 
	
	//method to listen to incoming threads
	public void Listen(){
		Thread inputThread = new Thread(new InputReader());
		inputThread.start(); 
	}
	 
    public void userAdd(String data) 
    {
         users.add(data);
    }
    public void userRemove(String data) 
    {
         showMessage(data + " is now offline.\n");
    }
    public void writeUsers() 
    {
         String[ ] tempList = new String[(users.size())];
         users.toArray(tempList);
         for (String token:tempList) 
         {
            users.add(token + "\n");
         }
    }
    //method for reading input stream

    public class InputReader implements Runnable
    {
        @Override
        public void run() 
        {         
            String  text;
            String status[] = {"Connected", "Disconnected", "Active", "Sent"};
            String[] data;
            try 
            {
                while((text = br.readLine()) != null)
                {
                	
                	data = text.split(":");
                	//if the user's status is active
                     if (data[2].equals(status[2])) 
                     {
                    	 //show the username and message
                        showMessage(data[0] + ": " + data[1] + "\n");
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());
                     } 
                     else if (data[2].equals(status[0]))
                     {
                        chatArea.removeAll();
                        userAdd(data[0]);
                     }                   
                     else if (data[2].equals(status[3])) 
                     {                       
                        writeUsers(); //send to server who will then send to other client
                        users.clear();
                     }
                }
           }
            catch(Exception ex) { }
        }
    }
	//constructor for Client GUI
	public Client(){
		 	textUsername = new JTextField(); 
	        btn_joinchat = new JButton();
	        chatWindow = new JScrollPane();
	        chatArea = new JTextArea();
	        textChat = new JTextField();
	        btn_send = new JButton();
	        labelName = new JLabel();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setTitle("Stephanie's Instant Messanger: Client");
	        setName("Client"); 
	        setResizable(false);

	        //button for joining chat room 
	        btn_joinchat.setText("Join Chat Room");
	        btn_joinchat.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	 textUsername.setText("");
	     	        if (isConnected == false) 
	     	        {
	     	        	//assign the user a unique username with a random number. 
	     	            String name="User_";
	     	            Random generator = new Random(); 
	     	            int i = generator.nextInt(999) + 1;
	     	            String is=String.valueOf(i);
	     	            name = name.concat(is);
	     	            username=name;
	     	            
	     	            textUsername.setText(name);
	     	            textUsername.setEditable(false);

	     	            try 
	     	            {
	     	                connection = new Socket(address, port);
	     	                //establish input stream and input reader	     	               
	     	               InputStreamReader streamreader = new InputStreamReader(connection.getInputStream());
	     	               //establish output stream and output reader
	     	                br = new BufferedReader(streamreader);
	     	                writer = new PrintWriter(connection.getOutputStream());
	     	                writer.println(username + ":has joined the chat :Connected");
	     	                writer.flush(); 
	     	                 	                 
	     	                isConnected = true; 
	     	            } 
	     	            catch (Exception ex) 
	     	            {
	     	                showMessage("Cannot Connect! Try Again. \n");
	     	          
	     	            }
	     	            
	     	            Listen();
	     	            
	     	        } else if (isConnected == true) 
	     	        {
	     	            showMessage("You are already connected. \n");
	     	        }        
	     	    }
	      
	            
	        });

	        //coding the send button to send text to the server 
	        btn_send.setText("SEND");
	        btn_send.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	         	        String nothing = "";
	         	        if ((textChat.getText()).equals(nothing)) {
	         	            textChat.setText("");
	         	            textChat.requestFocus();
	         	        } 
	         	        else{
	         	            try{
	         	            	//sends the text to the server 
	         	   				writer.println(username + ":" + textChat.getText() + ":" + "Active" + "\n");		
	         	   				writer.flush();
	         	   				chatArea.setCaretPosition(chatArea.getDocument().getLength());
	         	   			}
	         	               
	         	            catch (Exception ex){
	         	                showMessage("Message was not sent. \n");
	         	            }
	         	            //after sending the message it clears the chat text field 
	         	            textChat.setText("");
	         	            textChat.requestFocus();
	         	        }

	         	        textChat.setText("");
	         	        textChat.requestFocus();
	         	    }
	        });

	        chatArea.setColumns(20);
	        chatArea.setRows(5);
	        chatWindow.setViewportView(chatArea);
	        labelName.setText("Stephanie's IM Client");
	        labelName.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
	        //borrowed to create chat window
	        GroupLayout layout = new GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addComponent(textChat, GroupLayout.PREFERRED_SIZE, 352, GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(btn_send, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
	                    .addComponent(chatWindow)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	                        .addGap(18, 18, 18)
	                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
	                        .addGap(18, 18, 18)
	                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false) 
	                            .addComponent(btn_joinchat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
	                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(labelName)
	                .addGap(201, 201, 201)))));
	        layout.setVerticalGroup(
	            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)   
	                    .addComponent(btn_joinchat))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED) 
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(chatWindow, GroupLayout.PREFERRED_SIZE, 310, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                    .addComponent(textChat)
	                    .addComponent(btn_send, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(labelName)));
	        pack();
	}
	
	//alter chat window
	public void showMessage(String value)
	{		
			chatArea.append(value);
			
	}
	
}
