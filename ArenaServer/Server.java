import java.net.*;
import java.io.*;
import java.util.*;


public class Server extends Thread {
	// This class handles the connections and disconnections of the various clients
	// Because these tend to happen in parallel, the server extends itself into a thread
	// and so the main class handles the connections whereas the thread method handles
	// the disconnections

	private ServerSocket ss;
	private connection c[];
	private int con_num;

	public Server(int port_number, int max_players){
		try{
			ss = new ServerSocket(port_number);
		} catch (Exception e){
			System.out.println("Succes - Exception was caught in Server Constructor");
			System.out.println(e.getMessage());
			System.out.println("---------------------------------------------------");
		}

		c = new connection[max_players];
		for(con_num=0;con_num<max_players;con_num++){
			c[con_num] = null;
		}
		con_num = 0;
	}

	public static void main(String argv[]){
		
		int i;

		// Creating a new server
		int max_players = 4;
		int port_num = 47681;	
		Server Serv = new Server(port_num,max_players); 

		// Setting up threads		
		Thread th[];
		th = new Thread[max_players];	
		for(i=0;i<max_players;i++)th[i] = null;

		// Setting up Connection Handler
		connectionHandler ch = new connectionHandler(max_players);
		Thread tch = new Thread(ch);
		tch.start();

		// setting up the thread to remove connections
		Serv.start();

		// accepting connections
		connection c_tmp;
		while(true){
			c_tmp = new connection(Serv.ss);  // Accept the incoming connection
			Serv.c[Serv.con_num]=c_tmp;	  	  // Add the connection to the array
			ch.addConx(Serv.c[Serv.con_num]); // Add to the connection Handler
			th[Serv.con_num] = new Thread(Serv.c[Serv.con_num]);
			th[Serv.con_num].start();	      // Start the connection;
			Serv.con_num++;			          // Increment connection Count
		}

	}

	public void run(){
		int i;
		try{
			
			while(true){
				Thread.sleep(2000);
				for(i=0;i<this.con_num;i++){
					System.out.println("cheching connection "+i);
					if(this.c[i].getdropped()){
						this.remove_connection(i);
						System.out.println("Server: Connection "+i+" dropped");
					}
				}
			}
		}catch(Exception e){
			System.out.println("Success - Exception caught in server run");
			System.out.println(e.getMessage());
			System.out.println("----------------------------------------");
		}
	}
	
	private void remove_connection(int i){
		int j;
		for(j=i;j<con_num-1;j++){
			this.c[j] = this.c[j+1];
		}
		this.c[con_num] = null;
		this.con_num --;
	}

}
