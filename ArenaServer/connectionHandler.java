import java.io.*;
import java.util.*;

public class connectionHandler implements Runnable {
	// This class runs a thread that handles the connections. It checks each connection
	// to see if data has been received, and then gives that data to the Game class for 
	// the state to be updated. It then pulls the updated game state (ie positions of 
	// game sprites) from the game class and transmits them back to the connection.

	
	private	connection c[];
	private int con_num;
	private Game g1;
	
	public connectionHandler(int max_players){
		int i;
		c = new connection[max_players];
		for(i=0;i<max_players;i++){
			c[i] = null;
		}
		con_num = 0;
		g1 = new Game();
	}

	public void addConx(connection con){
		c[con_num] = con;	
		con_num ++;
		System.out.println("Debug - Connection Added to Connection Handler");
		g1.AddPlayer(con_num);
	}

	public void rmConx(int j){
		int i;
		System.out.println("Removing connection " + j);
		for(i=j;i<con_num-1;i++){
			c[i] = c[i+1];
		}
		con_num --;
	}

	public void run(){
		int i;
		String dat_in = new String();
		String dat_out = new String();
		while(true){
				// Update the Game
				for(i=0;i<con_num;i++){
					if(c[i].getdropped()){
						rmConx(i);
						this.g1.RmPlayer(i);
					} else if(c[i].data_ready()){
						dat_in = c[i].getConDatIn();
						g1.Update(i,dat_in);
					} else {
						g1.noDatUpdate();
					}
				}
				// Send data to the games
				for(i=0;i<con_num;i++){
					dat_out = g1.getDataOut(i);
					c[i].netWrite(dat_out);
				}
		
			
			try{
				Thread.sleep(15);
			}catch(Exception e){
				System.out.println("Success - Exception Caught in connecitonHAndler Thread");
				System.out.println(e.getMessage());
				System.out.println("--------------------------- --------------------------");
			}
		}

	}



}
