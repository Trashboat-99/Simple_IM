package Instant_Messanger;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//building a simple Instant Messenger application 
//class for GUI 
public class Server extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//GUI variables
	private JTextArea chatArea; //displays the conversation
	private JButton btn_clear; 
	private JButton btn_end;
	private JButton btn_start; 
	private JButton btn_users; 
	private JScrollPane chatWindow; 
	private JLabel labelName; 
	
	ArrayList clientOutputStreams; //create an Arraylist of client output streams
	ArrayList<String> users; //create an Arraylist of strings to hold the users
	
	public class ServerConnection implements Runnable{
		
		 BufferedReader br; 
		 Socket clientConnection; //a socket is a term in java for a connection between computers for networks
		 PrintWriter writer; 

		//constructor
		public ServerConnection(Socket clientSocket, PrintWriter user){
			writer = user; 
			try{
				//all the sever to receive input streams 
				clientConnection = clientSocket;
				//establish input stream 
				InputStreamReader isReader = new InputStreamReader(clientConnection.getInputStream());
                br = new BufferedReader(isReader);
				
			}
			catch(Exception e){
				chatArea.append("/n Error!");
			}
		}
		 @Override
	       public void run(){
	            String message;
	            String status[] = {"Connected", "Disconnected", "Active"};            
	            String[] data;	
	        	try{
	            	while((message = br.readLine()) != null)
	            	{
	            		showMessage("Received: " + message + "\n");
            			data = message.split(":");
            				for(String token:data)
            				{
            					showMessage(token + " ");
            				}
	            			//if a user has connected 
	            			if(data[2].equals(status[0]))
	            			{	            			
	            				//notify all client users the a new user has joined the IM. Set Status to Chat
	            				notifyAllUsers(data[0] + ":" + data[1] + ":" + status[2]);
	            				//add the user to the user array
	            				userAdd(data[0]);
	            			}
	            			else if(data[2].equals(status[1]))
	                        {
	                            notifyAllUsers((data[0] + ": has disconnected" + ":" + status[2]));
	                            userRemove(data[0]);
	                        } 
	                        else if (data[2].equals(status[2])) 
	                        {
	                            notifyAllUsers(message);
	                        } 
	                       

	            	}
	        	}
	        	catch (IOException e) 
	            {
	            	e.printStackTrace();
	            }  
	         }
	} //end of Server Connection class
	
		 //constructor for Server class 
		public Server(){
			chatArea = new JTextArea();
			chatWindow = new JScrollPane(); 
			btn_start = new JButton(); 
			btn_end = new JButton(); 
			btn_users = new JButton(); 
			btn_clear = new JButton(); 
			labelName = new JLabel(); 
			
			chatArea.setColumns(20);
			chatArea.setRows(5);
			chatWindow.setViewportView(chatArea);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			
			//set text for buttons
			btn_start.setText("Start");
			btn_end.setText("End");
			btn_clear.setText("Clear");
			btn_users.setText("Online Users");
			
			setTitle("Stephanie's Instant Messanger: Server");
			setResizable(false);
			setName("Server");
			
			labelName.setText("Stephanie's IM Server");
			labelName.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
			//start button action listener 
			btn_start.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent event){
							//start new Thread
							Thread startThread = new Thread(new StartRunning()); 
							startThread.start();
						}
					});
		
			//clear button action listener 
			btn_clear.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent event){
							chatArea.setText("");
						}
					});
			btn_users.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent event){
							for(String currentUsers : users){
								chatArea.append(currentUsers);
								chatArea.append("\n");
							}
						}
					});
			//user button action listener
			
		//borrowed code to create GUI Frame
			GroupLayout layout = new GroupLayout(getContentPane());
		        getContentPane().setLayout(layout);
		        layout.setHorizontalGroup(
		            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addContainerGap()
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                    .addComponent(chatWindow)
		                    .addGroup(layout.createSequentialGroup()
		                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
		                            .addComponent(btn_start, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
		                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 291, Short.MAX_VALUE)
		                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
		                            .addComponent(btn_clear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                            .addComponent(btn_users, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))))
		                .addContainerGap())
		            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                .addComponent(labelName)
		                .addGap(209, 209, 209))
		        );
		        layout.setVerticalGroup(
		            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addContainerGap()
		                .addComponent(chatWindow, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
		                .addGap(18, 18, 18)
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                    .addComponent(btn_start)
		                    .addComponent(btn_users))
		                .addGap(18, 18, 18)
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                    .addComponent(btn_clear)
		                .addGap(4, 4, 4)
		                .addComponent(labelName))));
		        pack();
		} //END of Server constructor 

	public class StartRunning implements Runnable{	
		//method for setting up and running the server
		
		@Override
		public void run(){
				clientOutputStreams = new ArrayList<ObjectOutputStream>(); 
				users = new ArrayList<String>(); 
				 
				
			try{
				//establish the server connection				
				ServerSocket socket = new ServerSocket(6789); //(port number)
				try{
				while(true){
						//wait for connection, then display connection information
						showMessage("\n Waiting for a connection to client... \n"); 
						Socket clientSocket = socket.accept(); //create socket when someone finally connects
						//notify user that they are connected
						showMessage("Now connected to "+ clientSocket.getInetAddress().getHostName() + " on port " + clientSocket.getPort());	
						//establish an output stream to client 
						PrintWriter output = new PrintWriter(clientSocket.getOutputStream()); 
						output.flush(); //flush out the buffer
						//establish input stream from client 
						
						showMessage("\n Streams are established");
						//add output stream to the arraylist
						clientOutputStreams.add(output);
						//create a new thread
						Thread listener = new Thread(new ServerConnection(clientSocket, output));
						listener.start();

				}
				}catch(EOFException eof){
					showMessage("\n Error.");
				}finally{
					//end the connection				
					socket.close(); 
				}
		
			}
			catch (IOException e){
				e.printStackTrace();
				showMessage("\n Error... cannot establish connection,");
				
			}
			
		}
	
	} //END of StartRunning class
		
		
    //updates chat window. Parameter is String value that appears in chat window
	public void showMessage(String text){
				chatArea.append(text);
			 }
	

	//method to add new user to IM 
		 public void userAdd (String data) 
		    {
		        String message, add = ": :Connected",sent="Server: :Sent", name = data;
		        showMessage(name + " joined the chat room. \n");
	
		        users.add(name);

		        String[] tempList = new String[(users.size())];
		        users.toArray(tempList);

		        for (String token:tempList) 
		        {
		            message = (token + add);
		            notifyAllUsers(message);
		        }
		        notifyAllUsers(sent);
		    }	
	//method to send message to entire arraylist of users 
	public void notifyAllUsers(String message){			
			Iterator itr = clientOutputStreams.iterator(); 
			while(itr.hasNext())
			{
				try 
				{
				PrintWriter toClient = (PrintWriter) itr.next();                
                toClient.println(message);
                showMessage("Sending: " + message + "\n");
                toClient.flush();
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
				} 
            catch (Exception ex) 
            {
            	ex.printStackTrace();
            	showMessage("Error. Could not send message all users \n");
            }
        } 

	}
	
	    //remove a user and let the other users know
	    public void userRemove (String data) 
	    {
	        String message, add = ": :Connected", done = "Server: :Sent", name = data;
	        users.remove(name);
	        String[] tempList = new String[(users.size())];
	        users.toArray(tempList);

	        for (String token:tempList) 
	        {
	            message = (token + add);
	            notifyAllUsers(message);
	        }
	       notifyAllUsers(done);
	    }

	}