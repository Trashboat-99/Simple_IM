package Instant_Messanger;
import java.awt.EventQueue;
import java.io.IOException;


public class SeverTest {

	public static void main(String[] args) throws IOException {
		 
		EventQueue.invokeLater(new Runnable()
				{
				public void run(){
					new Server().setVisible(true);
				}
		});
	}

}

	 