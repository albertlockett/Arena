import javax.swing.JFrame;


public class Arena extends JFrame implements Commons
{
	boolean inmenu;
	MainMenu mmenu;
	PlayArea pA;
	
	public Arena()
	{
		// Main Menu Section:
		mmenu = new MainMenu();
		add(mmenu);
		setTitle("Arena");		// Window Title
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(BOARD_WIDTH, BOARD_HEIGHT);	// From Commons - Full Screen
		setLocationRelativeTo(null);		// Non-relative (Full Screen)
		setVisible(true);
		setResizable(false);
		
		
		// Display Main Menu to User
		inmenu = true;
		while(inmenu == true){
			if(mmenu.status != 0) inmenu = false;
			}
		
		if(mmenu.status == 1){		 // Single Player
			setVisible(false);
			remove(mmenu);
			add(new PlayArea(mmenu.getJob(), 0)); // '0' does nothing. 
			setVisible(true);
			
		} else if(mmenu.status == 2){// MultiPlayer (networked) mode
			pA = new PlayArea(mmenu.Server_IP,mmenu.getJob());
			if (pA.getConnectionStatus() == 0){ // Can connect?
				setVisible(false);
				remove(mmenu);
				add(pA);
				setVisible(true);
				pA.gameInit();
			} else {	// Couldn't Connect to server
				System.out.println("DEBUG - can't connect to servier");
			}
		
	}
	
	public static void main(String[] args)
	{
		new Arena();
	}
}
