import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.*;



public class MainMenu extends JPanel implements ActionListener{

	private JLabel title;
	private JButton play;
	
	private JButton warriorButton;
	private JButton mageButton;
	private String job;
	
	private JTextField IP_Addr;
	private JButton netplay;
	volatile public int status; // Status of what user picks
		// 0 - Idle: No Choice made yet
		// 1 - Play: User has clicked 'Play' button
		// 2 - Network Play: User is going to try and play online
	public String Server_IP;
	
	
	public MainMenu(){
		status = 0;
		setLayout(new GridLayout(5,5));
		
		title = new JLabel("WELCOME TO ARENA: Buffalo Soldier Edition");
		title.setFont(new Font("title", Font.BOLD, 24));
		add(title, "North");
		
		warriorButton = new JButton("Warrior");
		add(warriorButton);
		warriorButton.addActionListener(this);
		
		mageButton = new JButton("Mage");
		add(mageButton);
		mageButton.addActionListener(this);
		
		play  = new JButton("PLAY");
		add(play);
		play.addActionListener(this);
		
		netplay = new JButton("Network Play (Beta)");
		add(netplay);;
		netplay.addActionListener(this);
		
		IP_Addr = new JTextField();
		add(IP_Addr);
		IP_Addr.addActionListener(this);
		
		
		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource()==play){
			status = 1;
		}
		
		if(arg0.getSource()==IP_Addr){ 
		// TODO Error check user input as either IP addr OR LOCALHOST
			Server_IP = IP_Addr.getText();
		}
		
		if(arg0.getSource() == netplay){
			status = 2;
		}
		
		if(arg0.getSource() == warriorButton)
		{
			job = "warrior";
		}
		
		if(arg0.getSource() == mageButton)
		{
			job = "mage";
		}
		
	}
	
	public int getchoice(){
		return status;
		
	}
	
	public String getJob()
	{
		return job;
	}
	
}
