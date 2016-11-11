package Client;

import java.awt.EventQueue;
import java.io.IOException;



public class ClientTest{

	public static void main(String[] args) throws IOException {
		 EventQueue.invokeLater(new Runnable() 
	        {
	            @Override
	            public void run() 
	            {
	                new Client().setVisible(true);
	            }
	        });
	}
}
