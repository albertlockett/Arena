import java.io.*;
import java.net.*;
import java.util.*;


public class connection implements Runnable{

	private Socket s;
	private PrintStream net_out;
	private BufferedReader net_in;

	private String in_data;
	private String out_data;
	private boolean has_data;
	private boolean dropped;

	private long old_time;
	private long new_time;


	public connection(){
		in_data =  new String();
		out_data = new String();
		has_data = false;
		dropped = false;
	}

	public connection(ServerSocket ss){
		try{
			s = ss.accept();
			net_in =  new BufferedReader(new InputStreamReader(s.getInputStream()));
			net_out = new PrintStream(s.getOutputStream());
		} catch (Exception e){
			System.out.println("Success - Exception caught constructing connection");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------------------------");
		}

		in_data =  new String();
		out_data = new String();
		has_data = false;
		dropped = false;
	}

	public void run(){
		new_time = System.currentTimeMillis();
		old_time = new_time;
		while(true){
			try{
				if(net_in.ready()){
					in_data = net_in.readLine();
					has_data = true;
					old_time = new_time;
					//new_time = System.currentTimeMillis();
					//System.out.println("Debug - Data Recieved By Connection "+in_data);
				}
				Thread.sleep(10);
				new_time = System.currentTimeMillis();
				if((new_time - old_time) > 20000){
					System.out.println("Dropped = True");
					this.closeConnection();
					dropped = true;
					break;
				}
			

			} catch (Exception e) {
				System.out.println("Success - Exception was caught in connection run");
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.out.println("------------------------------------------------");
			}
		}
	}

	public boolean data_ready(){
		boolean tmp = has_data;
		has_data = false;
		//System.out.println("Has data checked, returning: "+tmp);
		return(tmp);
	}

	public String getConDatIn() {return in_data;}
	
	public String getConDatOut(){return out_data;};
	
	public void netWrite(String s){
		try{
			net_out.println(s);
			//System.out.println("Sending Data "+s);
		} catch (Exception e) {
			System.out.println("Success - Exception was caught in connection net Writer");
			System.out.println(e.getMessage());
			System.out.println("-------------------------------------------------------");
		}

	}

	public boolean getdropped(){ return dropped;}

	private void closeConnection(){
		try{
			s.close();
			net_out.close();
			net_in.close();
		} catch (Exception e){
			System.out.println("Sucess - Eception caught closing connection");
			System.out.println(e.getMessage());
			System.out.println("-------------------------------------------");
		}
	
	}

}
