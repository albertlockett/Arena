import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.net.*; // Networking Functionality
import java.io.*;  // Writing and reading from the net
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PlayArea extends JPanel implements Runnable, Commons
{

	private static final long serialVersionUID = 1L;
	
	// Timers and flags used in collision detection
	private long timeSinceCollisionPlayer = 0;
	private long previousTimePlayer = System.currentTimeMillis();
	private boolean firstCollisionPlayer = true;
	private boolean takeDamagePlayer = true;
	private long timeSinceAttackEnemy = 0;
	private long previousTimeAttackEnemy = System.currentTimeMillis();
	
	
	private int[] wave = new int[NUM_WAVES];
	private int currentWave = 0;
	private boolean bossWave = false;
	
	private Thread animator;
	private String job;
	private Player player;
	private Player player2;
	private Weapon[] weapon;
	private Weapon[] enemyShot;
	private Weapon specialWeapon;
	private int weaponCounter;				
	private int enemyShotCounter;
	private int eweaponCounter;
	private String enemy = "enemy";
	private boolean gameOver = false;
	Image weaponImage;
	
	private int[] enemyX = {100, 200, 300, 400, 500}; // Sets the starting positions for the enemies.
													  // Change this to adjust the waves.
	ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	
	// Networking Variables:
	Socket s;
	BufferedReader net_in;
	PrintStream net_out; 
	int connection_status; // Status of the connection attempt
		// 0 = Connection Successfully
		// 1 = Unable to connect
	boolean network_mode;
	int net_counter;
	
	// Classes for Networking Mode
	private Player[] playerN;
	private Enemy[] enemyN;
	private Weapon[] WeaponN;
	private Weapon[] eWeaponN;
	private Weapon[] specWep;
	private int networkPlayerCounter;
	private int specWep_count;
	
	public PlayArea(String job, int x) // Single Player mode Constructor
	{
		this.job = job;
		network_mode = false;
		addKeyListener(new TAdapter());
		addMouseListener(new MAdapter());
		setFocusable(true);
		setBackground(Color.BLACK);
		gameInit();
		setDoubleBuffered(true);
	}
	
	public PlayArea(String ServerIP, String job) { // Network Mode Constructor
	  // INIT Networking Parts
		network_mode = true;
		try{
			s = new Socket(ServerIP, 47681);
			net_in = new BufferedReader(new InputStreamReader(s.getInputStream())); 
			net_out   = new PrintStream(s.getOutputStream()); 
			//net_out.println("Hello Server");
			
			connection_status = 0;
			
			
			
		} catch (Exception e){
			connection_status = 1;
		}
		
		net_counter = 10;
		
	  // Rest is same as above
	 	 this.job = job;
		addKeyListener(new TAdapter());
		addMouseListener(new MAdapter());
		setFocusable(true);
		setBackground(Color.BLACK);
		//gameInit();		
		setDoubleBuffered(true);
		
		//not gameover
		gameOver = false;
		
		System.out.println("Network Mode Selected");
		
	}
	
	public void addNotify()
	{
		super.addNotify();
	}
	
	public void gameInit()
	{
		// Initialize waves of enemies. 
		for(int i = 0; i < NUM_WAVES; i++)
		{
			wave[i] = i + 1;
		}
		
		if(network_mode == true){ // Creating the network sprites ... 
			playerN = new Player[MAX_NETWORK_PLAYERS];
			for(networkPlayerCounter=0;networkPlayerCounter<MAX_NETWORK_PLAYERS;networkPlayerCounter++){
				playerN[networkPlayerCounter] = new Player("warrior");
				playerN[networkPlayerCounter].setAlive(false);
			}
			WeaponN = new Weapon[MAX_NETWORK_PLAYERS*MAX_PLAYER_WEAPONS];
			for(int i=0;i<MAX_NETWORK_PLAYERS*MAX_PLAYER_WEAPONS;i++){
				WeaponN[i] = new Weapon(0,0,"warrior");
				WeaponN[i].setAlive(false);
			}
			 enemyN = new Enemy[NUM_ENEMIES];
			for(int i=0;i<NUM_ENEMIES;i++){
				enemyN[i] = new Enemy(0,0);
				enemyN[i].setAlive(false);
			}
			eWeaponN = new Weapon[10*30];
			for(int i=0;i<10*30;i++){
				eWeaponN[i] = new Weapon(0,0,"mage");
				eWeaponN[i].setAlive(false);
			}
			specWep_count = 0;
			specWep = new Weapon[MAX_NETWORK_PLAYERS];
			for(int i=0;i<MAX_NETWORK_PLAYERS;i++){
				specWep[i] = new Weapon(0,0,"mageSpecial");
				specWep[i].setAlive(false);
			}
			System.out.println("Creating Game Sprites");
		}
		
			
		// Create the player and assign a job
		player = new Player(job);
		
		// Create the mage's special attack
		specialWeapon = new Weapon(player.getX(), player.getY(), "mageSpecial");
		specialWeapon.setAlive(false);
		
		// Creates weapon and Enemy arrays
		weapon = new Weapon[MAX_PLAYER_WEAPONS];
		for(weaponCounter = 0; weaponCounter < MAX_PLAYER_WEAPONS; weaponCounter++)
		{
			weapon[weaponCounter] = new Weapon(player.getX(), player.getY(), job);
		}
		weaponCounter = 0;
		
		enemyShot = new Weapon[MAX_ENEMY_WEAPONS];
		for(enemyShotCounter = 0; enemyShotCounter < MAX_ENEMY_WEAPONS; enemyShotCounter++)
		{
			enemyShot[enemyShotCounter] = new Weapon(0, 0, enemy);
		}
		enemyShotCounter = 0;
		
		
		if(!network_mode){
			for(int i = 0; i < wave[currentWave]; i++)
			{
				enemyList.add(new Enemy(enemyX[i], 200));
			}
		}
		
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		
		
		// Start the game thread
		if(animator == null)
		{
			animator = new Thread(this);
			animator.start();
		}
	}
	
	public void drawplayer(Graphics g)
	{
		if(player.isAlive())
		{
			// Draw player
			g.drawImage(player.getImage(), player.getX(), player.getY(), this);
			
			
			// Draw player's health bar
			g.setColor(Color.white);
			g.drawString("HP: ", 10, 20);
			g.setColor(Color.green);
			g.fillRect(35, 10, HP_BAR_WIDTH, HP_BAR_HEIGHT);
			g.setColor(Color.red);
			g.fillRect(35, 10, (player.getHP() * HP_BAR_WIDTH) / player.getMaxHP(), HP_BAR_HEIGHT);
			
			// Draw player's mana bar
			g.setColor(Color.white);
			g.drawString("MP: ", 10, 32);
			g.setColor(Color.green);
			g.fillRect(35, 22, MP_BAR_WIDTH, MP_BAR_HEIGHT);
			g.setColor(Color.blue);
			g.fillRect(35, 22, (player.getMP() * MP_BAR_WIDTH) / player.getMaxMP(), MP_BAR_HEIGHT);
			
			// Draw player's experience
			g.setColor(Color.white);
			g.drawString("XP: ", 10, 44);
			g.setColor(Color.green);
			g.fillRect(35, 34, XP_BAR_WIDTH, XP_BAR_HEIGHT);
			g.setColor(Color.yellow);
			g.fillRect(35, 34, (player.getXP() * XP_BAR_WIDTH) / player.getMaxXP(), XP_BAR_HEIGHT);
			
			// Draw Player's lives
			g.setColor(Color.white);
			g.drawString("Lives: " + player.getLives(), 150, 20);
			
			// Draw player's level
			g.drawString("Level: " + player.getLevel(), 150, 32);
			
		}
		
		// Draw Player's online friends	
		if(network_mode == true){
			for(int i=0;i<MAX_NETWORK_PLAYERS;i++){
				if(playerN[i].isAlive()){
					g.drawImage(playerN[i].getImage(), playerN[i].getX(), playerN[i].getY(), this);
				}
			}
		}
		
	}
	
	public void drawWeapon(Graphics g)	// Creates weapon on screen
	{	
		int j;	 // Weapon counter
		for(j = 0; j < MAX_PLAYER_WEAPONS; j++)	// Now it draws an array of weapons..
		{	
			if(weapon[j].isAlive()) 
			{
				g.drawImage(weapon[j].getImage(), weapon[j].getX(), weapon[j].getY(), this);
			}
		}
		if(network_mode){
			for(int i=0;i<MAX_NETWORK_PLAYERS*MAX_PLAYER_WEAPONS;i++){
				if(WeaponN[i].isAlive()){
					g.drawImage(WeaponN[i].getImage(), WeaponN[i].getX(), WeaponN[i].getY(), this);
				}
			}
			for(int i=0;i<4*30;i++){
				if(eWeaponN[i].isAlive()){
					g.drawImage(eWeaponN[i].getImage(), eWeaponN[i].getX(), eWeaponN[i].getY(), this);
				}
			}
			for(int i=0;i<specWep.length;i++){
				if(specWep[i].isAlive()){
					g.drawImage(specWep[i].getImage(), specWep[i].getX(), specWep[i].getY(), this);
				}
			}
		}
		
	}
	
	public void drawSpecialWeapon(Graphics g)
	{
		if(specialWeapon.isAlive())
		{
			g.drawImage(specialWeapon.getImage(), specialWeapon.getX(), specialWeapon.getY(), this);
		}
	}
	
	public void drawEnemyAttack(Graphics g)
	{
		int j;
		for(j = 0; j < MAX_ENEMY_WEAPONS; j++)
		{
			if(enemyShot[j].isAlive())
			{
				g.drawImage(enemyShot[j].getImage(), enemyShot[j].getX(), enemyShot[j].getY(), this);
			}
		}
		if(network_mode){
			for(int i=0;i<4*30;i++){
				if(eWeaponN[i].isAlive()){
					g.drawImage(eWeaponN[i].getImage(), eWeaponN[i].getX(), eWeaponN[i].getY(), this);
				}
			}
		}
	}
	
	public void drawEnemy(Graphics g)
	{   
		for(int i = 0; i < enemyList.size(); i++)
		{
			g.drawImage(enemyList.get(i).getImage(), enemyList.get(i).getX(), enemyList.get(i).getY(), this);
			g.setColor(Color.green);
			g.fillRect(enemyList.get(i).getX(), enemyList.get(i).getY() - 10, enemyList.get(i).getWidth(), HP_BAR_HEIGHT/2);
			g.setColor(Color.red);
			g.fillRect(enemyList.get(i).getX(), enemyList.get(i).getY() - 10, (enemyList.get(i).getHP() * enemyList.get(i).getWidth()) / enemyList.get(i).getMaxHP(), HP_BAR_HEIGHT/2);
		}
		
		if(network_mode == true){
			for(int i=0;i<NUM_ENEMIES;i++){
				if(enemyN[i].isAlive()){
					g.drawImage(enemyN[i].getImage(), enemyN[i].getX(), enemyN[i].getY(), this);
				}
			}
		}
		
	}
	
	public void drawBackground(Graphics g)
	{
		g.setColor(new Color(0xA0, 0x52,0x2D));
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		//g.drawImage(backgroundImage, 0, 0, this); // Uncomment to draw a background image besides the solid color
	}

	public void drawGameOver(Graphics g)
	{
			g.setColor(Color.black);
			g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
			g.setColor(Color.white);
			g.drawString("GAME OVER!", BOARD_WIDTH/2, BOARD_HEIGHT/2);
	}
	
	public void paint(Graphics g)
	{
		int j;
		
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		
		if(gameOver)
		{
			drawGameOver(g);
		}
		
		if(!gameOver)
		{
			drawBackground(g);
			drawplayer(g);
			drawEnemy(g);
			drawSpecialWeapon(g);
			
			for(j = 0; j < MAX_ENEMY_WEAPONS; j++)
			{	
				if(enemyShot[j].isAlive())
				{
					drawEnemyAttack(g);
				}
			}
		
			//for(j = 0; j < MAX_PLAYER_WEAPONS; j++)
			//{		// Again Draws an array of weapons
			//	if(weapon[j].isAlive())
			//	{
					drawWeapon(g);
			//	}
			//}
			
			drawplayer(g);
			drawEnemy(g);
		}	
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	public void animationCycle()
	{	int j;
	
		if(player.getLives() <= 0)
		{
			gameOver = true;
		}
	
		repaint();
		player.move();
		specialWeapon.move();
		updateEnemyPosition();
		enemyAttack();
		
		for(j = 0; j < MAX_ENEMY_WEAPONS; j++)
		{		// Loop thru weapon array
			if(enemyShot[j].isAlive())	// Moves the weapon (unless it's dead)
			{
				enemyShot[j].move();
			}
		}
		
		for(int i = 0; i < enemyList.size(); i++)
		{
			enemyList.get(i).move();
			
		}
		
		for(j = 0; j < MAX_PLAYER_WEAPONS; j++)
		{		// Loop thru weapon array
			if(weapon[j].isAlive())	// Moves the weapon (unless it's dead)
			{
				weapon[j].move();
			}
		}
		
		
		if(enemyList.isEmpty() && currentWave < NUM_WAVES-1)
		{
			currentWave++;
			for(int i = 0; i < wave[currentWave]; i++)
			{
				enemyList.add(new Enemy(enemyX[i], 200));
			}
		}
		
		if(enemyList.isEmpty() && currentWave == NUM_WAVES-1)
		{
			bossWave = true;
		}
		
		if(bossWave)
		{
			for(int i = 0; i < 3; i++)
			{
				enemyList.add(new Enemy(enemyX[i], 200));
			}
			enemyList.add(new Enemy(400, 100));
			for(int i = 4; i < 7; i++)
			{
				enemyList.add(new Enemy(enemyX[i-4]+500, 200));
			}
			bossWave = false;
		}
		
		
		checkCollisions();
	}
	
	public void multiplayerCycle() throws Exception{
		/* This is the animation cycle that is called in multiplayer mode. It is similar
		* to the anmiamtion cycle for single player mode except that it also has to 
		* transmit and recieve information about the positions of the enemies and players
		*/
		
	String dat_in = new String();
		//Scanner sc = new Scanner(dat_in);
		boolean new_dat = false;
		int X = 0, Y = 0;
		int J = 0;
		String Xs, Ys;
		int i,j,connected_players,net_weapons;
		int enemy_num = 0;
		
	
		repaint();
		
	// Move Local Sprites
		player.move(); 
		for(j = 0; j < MAX_PLAYER_WEAPONS; j++){	// Loop thru weapon array
			if(weapon[j].isAlive()){	// Moves the weapon (unless it's dead)
				weapon[j].move();
			}
		}
		specialWeapon.move();	
		
	// Ready transmit data:
		// Players Job -----
		String s = new String();
		if(player.getJob()) s = new String("1,");
		else 			  s = new String("0,");
		// Player's Position -----	
		s = new String(s+player.getX() + "," + player.getY());	
		// Weapon Positions -----
		String tmp = new String();
		j = 0;
		for(i=0;i<MAX_PLAYER_WEAPONS;i++){
			if(weapon[i].isAlive()){
				if(player.getJob()){
					tmp = tmp+",1,"+Integer.toString(weapon[i].getX())+","+Integer.toString(weapon[i].getY());
					j++;
				}else{
					tmp = tmp+",0,"+Integer.toString(weapon[i].getX())+","+Integer.toString(weapon[i].getY());
					j++;
				}
			}
		}
		s = s+","+Integer.toString(j)+tmp;
		// Special Weapon transmit
		if(specialWeapon.isAlive()){
			tmp = new String(",1,"+specialWeapon.getX()+","+specialWeapon.getY());
		} else {
			tmp = new String(",0");
		}
		s = new String(s+tmp);
		//System.out.println("Sending data" + s);
	
	
	// Send Data to Server
		net_out.println(s);

	// Recieve The data
		if(net_in.ready()){
			dat_in = net_in.readLine();
			Scanner sc = new Scanner(dat_in).useDelimiter(",");
			//System.out.println("Recieved Data "+ dat_in);

		// Update the other players
			connected_players = sc.nextInt();
			for(i=0;i<connected_players;i++){
				playerN[i].setAlive(true);
				J = sc.nextInt();
				X = sc.nextInt();
				Y = sc.nextInt();
				if(J == 0) playerN[i].setJob(false);
				else	   playerN[i].setJob(true);
				playerN[i].setX(X);
				playerN[i].setY(Y);
				playerN[i].move();
			}
			for(i=connected_players;i<playerN.length;i++){
				playerN[i].setAlive(false);
			}
			
		// Update the Player's weapons
			net_weapons = sc.nextInt();
			for(i=0;i<net_weapons;i++){
				WeaponN[i].setAlive(true);
				X = sc.nextInt();
				Y = sc.nextInt();
				J = sc.nextInt();
				if(J == 0) WeaponN[i].setJob(false);
				else	   WeaponN[i].setJob(true);
				WeaponN[i].setX(X);
				WeaponN[i].setY(Y);
				WeaponN[i].move();
			}
			for(i=net_weapons;i<WeaponN.length;i++){
				WeaponN[i].setAlive(false);
			}
			
		// Update the game's enemies
			enemy_num = sc.nextInt();	
			 for(i = 0; i < enemy_num; i++) {	
				enemyN[i].setAlive(true);
				enemyN[i].setX(sc.nextInt());
				enemyN[i].setY(sc.nextInt());
			}
			for(i=enemy_num;i<enemyN.length;i++){
					enemyN[i].setAlive(false);
			}
		// Update Enemy Weapons
			eweaponCounter = sc.nextInt();
			for(i=0;i<eweaponCounter;i++){
				eWeaponN[i].setAlive(true);
				X = sc.nextInt();
				Y = sc.nextInt();
				eWeaponN[i].setX(X);
				eWeaponN[i].setY(Y);
				eWeaponN[i].move();
			}
			for(i=eweaponCounter;i<eWeaponN.length;i++){
				eWeaponN[i].setAlive(false);
			}
		// Special Weapons
			specWep_count = sc.nextInt();
			System.out.println("Special weapon count = "+specWep_count);
			for(i=0;i<specWep_count;i++){
				specWep[i].setAlive(true);
				X = sc.nextInt();
				Y = sc.nextInt();
				specWep[i].setX(X);
				specWep[i].setY(Y);
				specWep[i].move();
			}
			for(i=specWep_count;i<specWep.length;i++){
				specWep[i].setAlive(false);
			}
			
			
			sc.close();
			
		}
		
		// Collision Detection
		checkCollisionsNetwork(enemy_num, eweaponCounter);
		
	}
	
	@Override
	public void run() 
	{	
		while(true)
		{
			
			if (network_mode == false) animationCycle();
			else{ 
				try {
					multiplayerCycle();  }
				catch(Exception e){
					System.out.println("Exception in MultiCycle"); 
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		
			try
			{
				Thread.sleep(1000/60);
			}
			catch(InterruptedException e)
			{
				System.out.println("Interrupted.");
			}
		}
	}
	
	private class TAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			player.keyPressed(e);
			
			if(e.getKeyCode() == KeyEvent.VK_UP)
			{
				weapon[weaponCounter].setAlive(true);
				weapon[weaponCounter].setX(player.getX() + player.getWidth()/2);
				weapon[weaponCounter].setY(player.getY() + player.getHeight()/2);
				weapon[weaponCounter].setDX(0);
				weapon[weaponCounter].setDY(-5);
			}
			if(e.getKeyCode() == KeyEvent.VK_DOWN)
			{	
				weapon[weaponCounter].setAlive(true);
				weapon[weaponCounter].setX(player.getX() + player.getWidth()/2);
				weapon[weaponCounter].setY(player.getY() + player.getHeight()/2);
				weapon[weaponCounter].setDX(0);
				weapon[weaponCounter].setDY(5);
			}
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
			{
				weapon[weaponCounter].setAlive(true);
				weapon[weaponCounter].setX(player.getX() + player.getWidth()/2);
				weapon[weaponCounter].setY(player.getY() + player.getHeight()/2);
				weapon[weaponCounter].setDX(-5);
				weapon[weaponCounter].setDY(0);
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			{
				weapon[weaponCounter].setAlive(true);
				weapon[weaponCounter].setX(player.getX() + player.getWidth()/2);
				weapon[weaponCounter].setY(player.getY() + player.getHeight()/2);
				weapon[weaponCounter].setDX(5);
				weapon[weaponCounter].setDY(0);
			}
			if(e.getKeyCode() == KeyEvent.VK_SPACE && job.equals("mage"))
			{
				specialAttack();
			}
			
			weaponCounter++;
			if(weaponCounter == MAX_PLAYER_WEAPONS)
			{
				weaponCounter = 0;
			}
			
			
		}
		
		public void keyReleased(KeyEvent e)
		{
			player.keyReleased(e);
		}
	}
	
	public void specialAttack()
	{
		if(!specialWeapon.isAlive() && player.getMP() >= 10)
		{
			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			
			specialWeapon.setAlive(true);
			specialWeapon.setX(player.getX() - player.getWidth());
			specialWeapon.setY(player.getY() - player.getHeight()/2);
			
			double directionX = ((int)b.getX()) - player.getX();
			double directionY = ((int)b.getY()) - player.getY();
			
			double length = Math.sqrt(directionX*directionX + directionY*directionY);
			directionX /= length;
			directionY /= length;
			directionX *= 10;
			directionY *= 10;
			
			specialWeapon.setDX((int)directionX);
			specialWeapon.setDY((int)directionY);
			
			player.loseMP(10);
		}
	}
	
	private class MAdapter extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{			
			weapon[weaponCounter].setAlive(true);
			weapon[weaponCounter].setX(player.getX() + player.getWidth()/2);
			weapon[weaponCounter].setY(player.getY() + player.getHeight()/2);
			
			double directionX = (e.getX()) - player.getX();
			double directionY = (e.getY()) - player.getY();
			
			double length = Math.sqrt(directionX*directionX + directionY*directionY);
			directionX /= length;
			directionY /= length;
			directionX *= 10;
			directionY *= 10;
			
			weapon[weaponCounter].setDX((int)directionX);
			weapon[weaponCounter].setDY((int)directionY);
			
			weaponCounter++;
			if(weaponCounter == MAX_PLAYER_WEAPONS)
			{
				weaponCounter = 0;
			}
		}
	}
	
	public void updateEnemyPosition()
	{
		for(int i = 0; i < enemyList.size(); i++)
		{
			double directionX = player.getX() - enemyList.get(i).getX();
			double directionY = player.getY() - enemyList.get(i).getY();
			
			double length = Math.sqrt(directionX*directionX + directionY*directionY);
			directionX /= length;
			directionY /= length;
			directionX *= enemyList.get(i).getSpeed();
			directionY *= enemyList.get(i).getSpeed();
			
			enemyList.get(i).setDX((int)directionX);
			enemyList.get(i).setDY((int)directionY);
		}
	}
	
	public void enemyAttack()
	{
		long currentTimeAttackEnemy = System.currentTimeMillis();
		long deltaTimeAttackEnemy = currentTimeAttackEnemy - previousTimeAttackEnemy;
		timeSinceAttackEnemy += deltaTimeAttackEnemy;
		
		// For each enemy in the current wave, add a random chance to attack every second
		Random attackGenerator = new Random();
		
		for(int i = 0; i < enemyList.size(); i++)
		{
			int chance = attackGenerator.nextInt(100) + 1;
		
			if(chance >= 99 && timeSinceAttackEnemy > 30000)
			{
				previousTimeAttackEnemy = 0;
				timeSinceAttackEnemy = 0;
				enemyShot[enemyShotCounter] = new Weapon(enemyList.get(i).getX() + enemyList.get(i).getWidth()/2,
						enemyList.get(i).getY() + enemyList.get(i).getHeight()/2, enemy);
				
				enemyShot[enemyShotCounter].setAlive(true);
				//enemyShot.setX(enemyList.get(i).getX());
				//enemyShot.setY(enemyList.get(i).getY());
				

				double directionX = (player.getX() - enemyShot[enemyShotCounter].getX());
				double directionY = (player.getY() - enemyShot[enemyShotCounter].getY());
				
				double length = Math.sqrt(directionX*directionX + directionY*directionY);
				directionX /= length;
				directionY /= length;
				directionX *= 10;
				directionY *= 10;
				
				//System.out.println(directionX);
				//System.out.println(directionY);
				
				enemyShot[enemyShotCounter].setDX((int)directionX);
				enemyShot[enemyShotCounter].setDY((int)directionY);
				
				enemyShotCounter++;
				if(enemyShotCounter == MAX_ENEMY_WEAPONS)
				{
					enemyShotCounter = 0;
				}
			}
		}
		
		
		
		
	}
	
	public void checkCollisions()
	{
		long currentTimePlayer = System.currentTimeMillis();
		long deltaTimePlayer = currentTimePlayer - previousTimePlayer;
		timeSinceCollisionPlayer += deltaTimePlayer;
		
		/*
		long currentTimeEnemy = System.currentTimeMillis();	
		long deltaTimeEnemy = currentTimePlayer - previousTimeEnemy;
		timeSinceCollisionEnemy += deltaTimeEnemy;
		*/
		
		//Enemy Shot hits Player
		for(int i = 0; i < enemyList.size(); i++)
		{
			Rectangle[] enemyShotRect = new Rectangle[enemyShotCounter];
			for(int j = 0; j < enemyShotCounter; j++)
			{
				if(enemyShot[j].isAlive())
				{
					enemyShotRect[j] = new Rectangle(
							enemyShot[j].getX(),
							enemyShot[j].getY(),
							enemyShot[j].getWidth(),
							enemyShot[j].getHeight());
					Rectangle playerRect = new Rectangle(
							player.getX(),
							player.getY(),
							player.getWidth(),
							player.getHeight());
					if(enemyShotRect[j].intersects(playerRect))
					{
						player.takeDamage(enemyList.get(i).getStrength());
						if(player.getHP() <= 0)
						{
							player.removeLife();
							player.setHP(player.getMaxHP());
							player.setX(PLAYER_X);
							player.setY(PLAYER_Y);
						}
						enemyShot[j].setAlive(false);
					}
				}
			}
		}
		
		//Player hits enemy
		Rectangle[] weaponRect = new Rectangle[weaponCounter];
		for(int i = 0; i < weaponCounter; i++)
		{
			if(weapon[i].isAlive())
			{
				weaponRect[i] = new Rectangle(
						weapon[i].getX(), 
						weapon[i].getY(), 
						weapon[i].getWidth(), 
						weapon[i].getHeight());
				Rectangle[] enemyRect = new Rectangle[enemyList.size()];
				for(int j = 0; j < enemyList.size(); j++)
				{
					enemyRect[j] = new Rectangle(
							enemyList.get(j).getX(), 
							enemyList.get(j).getY(), 
							enemyList.get(j).getWidth(), 
							enemyList.get(j).getHeight());
					
					if(weaponRect[i].intersects(enemyRect[j]))
					{
						enemyList.get(j).takeDamage(player.getStrength());
						weapon[i].setAlive(false);
						if(enemyList.get(j).getHP() <= 0)
						{
							player.gainEXP(enemyList.get(j).getEXP());
							if(player.getEXP() >= 100)
							{
								player.gainLevel();
							}
							enemyList.remove(j);
						}
					}
				}
			}
		}
		
		// Player hits enemy with special attack
			if(specialWeapon.isAlive())
			{
				Rectangle specialRect = new Rectangle(specialWeapon.getX(),
													  specialWeapon.getY(),
													  specialWeapon.getWidth(),
													  specialWeapon.getHeight());
				Rectangle[] enemyRect = new Rectangle[enemyList.size()];
				for(int j = 0; j < enemyList.size(); j++)
				{
					enemyRect[j] = new Rectangle(
							enemyList.get(j).getX(), 
							enemyList.get(j).getY(), 
							enemyList.get(j).getWidth(), 
							enemyList.get(j).getHeight());
					
					if(specialRect.intersects(enemyRect[j]))
					{
							enemyList.get(j).takeDamage(player.getSpecialDamage());
							if(enemyList.get(j).getHP() <= 0)
							{
								player.gainEXP(enemyList.get(j).getEXP());
								if(player.getEXP() >= 100)
								{
									player.gainLevel();
								}
								enemyList.remove(j);
							}
					}
				}
			}
		
		
		
		// Player gets hit
		if(timeSinceCollisionPlayer > 20000 || firstCollisionPlayer == true)
		{
			takeDamagePlayer = true;
			firstCollisionPlayer = false;
			// Do a collision, then reset timeSinceCollision to 0
		
			Rectangle[] enemyRect = new Rectangle[enemyList.size()];
			Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
			for(int i = 0; i < enemyList.size(); i++)
			{
				enemyRect[i] = new Rectangle(enemyList.get(i).getX(), enemyList.get(i).getY(), enemyList.get(i).getWidth(), enemyList.get(i).getHeight());
	
				if(playerRect.intersects(enemyRect[i]))
				{
					previousTimePlayer = System.currentTimeMillis(); // get time of collision
					if(takeDamagePlayer == true)
					{
						player.takeDamage(enemyList.get(i).getStrength());
						if(player.getHP() <= 0)
						{
							player.removeLife();
							player.setHP(player.getMaxHP());
							player.setX(PLAYER_X);
							player.setY(PLAYER_Y);
						}
						takeDamagePlayer = false;
					}
				}
			}
			
			timeSinceCollisionPlayer = 0;
		}
		
		
	}

	public void checkCollisionsNetwork(int enemy_num, int enemy_arrow_num){
		int HitBoxXp = 50;
		int HitBoxXn = 20; 
		int HitBoxYp = 100;
		int HitBoxYn = 50;
		double xscale = 0.75;
		double yscale = 0.9;
		
		
		int wHitBoxX = 100;
		int wHitBoxY = 150;
		
		for(int i = 0; i<enemy_num;i++){
			if(player.getX() > (enemyN[i].getX()-HitBoxXn) && player.getX() < (enemyN[i].getX() + HitBoxXp)){
			if(player.getY() > (enemyN[i].getY()-HitBoxYn) && player.getY() < (enemyN[i].getY() + HitBoxYp)){
				player.takeDamage((int)(player.getMaxHP()*0.02));
				if(player.getHP() <= 0)	{
					player.removeLife();
					player.setHP(player.getMaxHP());
					player.setX(PLAYER_X);
					player.setY(PLAYER_Y);
				}
			}} 
			for(int j=0; j<weapon.length; j++){
			if(weapon[j].isAlive()){
				if(weapon[j].getX() > (enemyN[i].getX()-((int)HitBoxXn*xscale)) && weapon[j].getX() < (enemyN[i].getX() + ((int)HitBoxXp*xscale))){
				if(weapon[j].getY() > (enemyN[i].getY()-((int)HitBoxYn*yscale)) && weapon[j].getY() < (enemyN[i].getY() + ((int)HitBoxYp*yscale))){			
					System.out.println("Player weapon collision detected");
					weapon[j].setAlive(false);
					player.gainEXP(enemyN[i].getEXP());
					if(player.getEXP() >= 100)	player.gainLevel();
				}}
			}
			

		}
			
			}
			
		for(int i=0; i<enemy_arrow_num; i++){
			if(eWeaponN[i].getX() > player.getX() && eWeaponN[i].getX() < (player.getX()+wHitBoxX)) {
			if(eWeaponN[i].getY() > player.getY() && eWeaponN[i].getY() < (player.getY()+wHitBoxY)) {
				eWeaponN[i].setAlive(false);
				player.takeDamage((int)(player.getMaxHP()*0.05));
				System.out.println("Bullet collision Detected");
				if(player.getHP() <= 0)	{
					player.removeLife();
					player.setHP(player.getMaxHP());
					player.setX(PLAYER_X);
					player.setY(PLAYER_Y);
				}
			
			}}
		}
		
		
		
		
		
		
	}

	public int getConnectionStatus(){
		return connection_status;
	}

}
