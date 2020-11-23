import java.util.*;
import java.lang.Math.*;

public class Game implements Commons{
	// This class manages all the data for the current game inlcuding the location of 
	// Enemies and players and all their weapons.
	// also performs hit detection on the enemies and their weapons but the game
	// actually has to manage that as well 
	// All the weapons and enemies and other players have their positions stored in multi
	// dimmensional arrays. Designers are cognizant that all classes could have become 
	// serializable and be transmitted a classes themselves however since the server really
	// only keeps track of their positions representing them as arrays of integers helps
	// to keep the amount of data being transmitted at a minimum. 

	int players[][];
	int player_num;
	int arrows[][][];
	int arrow_num[];
	int enemy[][];
	int enemy_num;
	int enemy_arrows[][][];
	int enemy_arrow_num[];
	int shoot_timer;
	int specWep[][];
	
	public Game(){
		// Constructor - Just intiailizes some of the arrays and what not
		players = new int[0][3];
		player_num = 0;
		arrows  = new int[0][MAX_PLAYER_WEAPONS][3];
		arrow_num = new int[0];
		enemy = new int[0][3];
		enemy_num = 0;
		enemy_arrows = new int[0][30][4];
		enemy_arrow_num = new int[0];
		specWep = new int[0][2];
		shoot_timer = 0;
	}
	
	public void Update(int i, String s){
	// Runs when remote host sends data
	// to update the position of the player and his weapons
	// Also performs hit detections of the weapons on the enemies
		int j;
		int arrX,arrY,arrJ;
		
		Scanner sc = new Scanner(s).useDelimiter(",");
		// update Players
		players[i][0] = sc.nextInt();
		players[i][1] = sc.nextInt();
		players[i][2] = sc.nextInt();
		//update Arrows
		arrow_num[i] = sc.nextInt();
		for(j=0;j<arrow_num[i];j++){
			arrJ = sc.nextInt();
			arrX = sc.nextInt();
			arrY = sc.nextInt();
			if(checkCollisions(arrX,arrY)){
				System.out.println("Collision Detected");
				arrow_num[i] -= 1;
			} else {
				arrows[i][j][0] = arrX;
				arrows[i][j][1] = arrY;
				arrows[i][j][2] = arrJ;
			}
			if(enemy_num == 0){
				Random r_gen1 = new Random();
				addEnemy(r_gen1.nextInt(10));
			}
			
		}
		
		// Receive Special Weapons
		if(sc.nextInt() != 0){
			arrX = sc.nextInt();
			arrY = sc.nextInt();
			if(checkCollisions(arrX, arrY)){
				System.out.println("Collision Detected");
			} else{
				specWep[i][0] = arrX;
				specWep[i][1] = arrY;
			}
		} else {
			specWep[i][0] = 0;
			specWep[i][1] = 0;
		}
		
		moveEnemies();
		// Add Arrows
		if(shoot_timer > 30){
			Random r_gen = new Random();
			for(j=0;j<enemy_num;j++){
				if((r_gen.nextInt(1000)%3) != 0) {
					AddEnemyArrow(j);
					shoot_timer = 0;
				}
			}
		} else {
			shoot_timer ++;
			System.out.println("Incrementing Shoot Timer");
		}
		moveEnemyArrows();
		checkPlayerCollisions(players[i][1], players [i][2]);
	}
	
	public void noDatUpdate(){
	// runs when remote host sends no data
	// To move the enemies and their weapons
		int j;
		moveEnemies();
			// Add Arrows
			if(shoot_timer > 60){
				Random r_gen = new Random();
				for(j=0;j<enemy_num;j++){
					if((r_gen.nextInt(1000)%3) != 0) {
						AddEnemyArrow(j);
						shoot_timer = 0;
					}
				}
			} else {
				shoot_timer ++;
				System.out.println("Incrementing Shoot Timer");
			}
			moveEnemyArrows();
		
	}
	
	public String getDataOut(int num){
	// returns the data to be sent to a connected host regarding the postions of other
	// enemies and players and their weapons.
		int i,j,total;
		String tmp = new String();
		// Add players
		String dat_out = new String(Integer.toString(player_num-1));
		for(i=0;i<player_num;i++){
			if(i!=num){
				dat_out = new String(dat_out+","+players[i][0]+","+players[i][1]+","+players[i][2]);
			}
		}
		// Add Arrows
		total = 0;
		for(i=0;i<player_num;i++){
			if(i!=num){
				total = total + arrow_num[i];
				for(j=0;j<arrow_num[i];j++){
					tmp = new String(tmp+","+arrows[i][j][0]+","+arrows[i][j][1]+","+arrows[i][j][2]);
				}
			}
		}
		dat_out = new String(dat_out+","+Integer.toString(total)+tmp+","+enemy_num);
		// Add Enemies
		for(i=0;i<enemy_num;i++){
			dat_out = new String(dat_out+","+enemy[i][0]+","+enemy[i][1]);		
		}
		
		total = 0;
		tmp = new String();
		// Add Enemy Arrows
		for(i=0;i<enemy_num;i++){ //  Thru enemies
			total = total + enemy_arrow_num[i];
			for(j=0;j<enemy_arrow_num[i];j++){
				tmp = new String(tmp + ","+enemy_arrows[i][j][0]+","+enemy_arrows[i][j][1]);
			}
		}
		dat_out = new String(dat_out+","+Integer.toString(total)+tmp);
		// Add special Weapons
		total = 0;
		tmp = new String();
		for(i=0;i<player_num;i++){
			if(specWep[i][0] != 0 && specWep[i][1] != 0 && i!=num){
				total++;
				tmp = new String(tmp+","+specWep[i][0]+","+specWep[i][1]);
			}
		}
		dat_out = new String(dat_out+","+Integer.toString(total)+tmp);
		// Add other things after
		return(dat_out);
		
		
		
	}
	
	public void AddPlayer(int num){
	// Adds a new player to the game
		int i,j;
		player_num = num;
		players = new int[player_num][3];
		for(i=0;i<player_num;i++) {
			players[i][0] = i;
			players[i][1] = 0;
			players[i][2] = 0;
		}
		
		arrow_num = new int[player_num];
		for(i=0;i<player_num;i++) arrow_num[i] = 0;
		
		specWep = new int[player_num][2];
		arrows = new int[player_num][MAX_PLAYER_WEAPONS][3];
		
		for(i=0;i<player_num;i++){
			for(j=0;j<MAX_PLAYER_WEAPONS;j++){
				arrows[i][j][0] = 0;
				arrows[i][j][1] = 0;
				arrows[i][j][2] = players[i][0];
			}
			specWep[i][0] = 0;
			specWep[i][1] = 0;
		}
		
		
	}
	
	public void RmPlayer(int j){
	// Removes a player from the game upon disconnected
		int i;
		for(i=j;i<player_num-1;i++){
			players[i][0] = i;
			players[i][1] = players[i+1][1];
			players[i][2] = players[i+1][2];
			arrow_num[i] = arrow_num[i+1];
		}
		player_num --;
		
	}

	private void addEnemy(int e_num){
	// Adds a new wave of enemies to the game
		Random r_gen = new Random();
		
		enemy_num = e_num;
		enemy = new int[enemy_num][3];
		for(int i=0;i<enemy_num;i++){
			enemy[i][0] = r_gen.nextInt(1000)+20;
			enemy[i][1] = r_gen.nextInt(750)+20;
		
		}
		enemy_arrows = new int[e_num][30][4];
		enemy_arrow_num = new int[e_num];
		for(int i=0;i<e_num;i++){
			enemy_arrow_num[i] = 0;
		}
		enemy_choose_player();
	
	}
	
	private void removeEnemy(int e_num){
	// Removes an enemy from the game when they have been hit by a weapon
		int i;
		for(i=e_num;i<enemy_num-1;i++){
			enemy[i][0] = enemy[i+1][0];
			enemy[i][1] = enemy[i+1][1];
			enemy[i][2] = enemy[i+1][2];
			enemy_arrow_num[i] = enemy_arrow_num[i+1];
			enemy_arrows[i] = enemy_arrows[i+1];
		}
		enemy_num -= 1;
		enemy[i][0] = 0;
		enemy[i][1] = 0;
		enemy[i][2] = 0;
		enemy_arrow_num[i] = 0;
		
		
	
	}

	private boolean checkCollisions(int X, int Y){
	// Checks if a collision has occured between an enemy and player's weapon
		int hitBoxSizeX = 105;
		int hitBoxSizeY = 80;
		for(int i=0;i<enemy_num;i++){
			if(X<enemy[i][0]+hitBoxSizeX && X>enemy[i][0]){
				if(Y<enemy[i][1]+hitBoxSizeY && Y>enemy[i][1]){
					removeEnemy(i);
					return true;
				}
			}
		}
		return(false);
	}
	
	private void checkPlayerCollisions(int x, int y){
	// Checks weather an enemies weapon has contacted a player
		int wHitBoxX = 100;
		int wHitBoxY = 150;
		
		for(int i = 0; i<enemy_num; i++){
			for(int j = 0; j<enemy_arrow_num[i]; j++){
				if(enemy_arrows[i][j][0] > x && enemy_arrows[i][j][0] < (x+wHitBoxX)){
				if(enemy_arrows[i][j][1] > y && enemy_arrows[i][j][1] < (y+wHitBoxY)){
				  	remove_arrow(i,j);
				}}
			}
		}
		
	
	}
	
	private void remove_arrow(int i, int j){
	// removes the weapons if a collision has been detected.
		int k;
		for(k = j; k < enemy_arrow_num[i]-1;k++){
			enemy_arrows[i][k][0] = enemy_arrows[i][k+1][0];
			enemy_arrows[i][k][1] = enemy_arrows[i][k+1][1];
		}
		enemy_arrows[i][k][0] = 0;
		enemy_arrows[i][k][1] = 0;
		enemy_arrow_num[i] --;
	}

	private void moveEnemies(){
		// Moves the enemies in the direction of the player they are attacking.
 		double dirX = 0;
		double dirY = 0;
 		double length = 99999;
 		int speed = 2;

		for(int i=0;i<enemy_num;i++){
			dirX = players[enemy[i][2]][1] - enemy[i][0];
			dirY = players[enemy[i][2]][2] - enemy[i][1];
			length = Math.sqrt(dirX*dirX + dirY*dirY);
			dirX /= length;
			dirY /= length;
			dirX *= speed;
			dirY *= speed;
			enemy[i][0] += dirX;
			enemy[i][1] += dirY;
		
		}
	
	}
	
	private void enemy_choose_player(){
		// Randomly assigns the enemy a player to attack. 
		int i;
		Random r_gen = new Random();
		for(i=0;i<enemy_num;i++){
			enemy[i][2] = r_gen.nextInt(player_num);
		}
	}
	
	private void AddEnemyArrow(int j){
		// adds an enemy arrow and gives it an initial direction and position
		double dirX = 0;
		double dirY = 0;
		double length = 99999;
 		int speed = 6;
		
		dirX = players[enemy[j][2]][1] - enemy[j][0];
		dirY = players[enemy[j][2]][2] - enemy[j][1];
		length = Math.sqrt(dirX*dirX + dirY*dirY);

		dirX /= length;
		dirY /= length;
		dirX *= speed;
		dirY *= speed;
				
		if(enemy_arrow_num[j] < 29)	enemy_arrow_num[j] ++;
		else enemy_arrow_num[j] = 0;
		
		enemy_arrows[j][enemy_arrow_num[j]][0] = enemy[j][0] + (int)(4*dirX); // Set X
		enemy_arrows[j][enemy_arrow_num[j]][1] = enemy[j][1] + (int)(4*dirY)+20; // Set Y
		enemy_arrows[j][enemy_arrow_num[j]][2] = (int) dirX; // Set dx
		enemy_arrows[j][enemy_arrow_num[j]][3] = (int) dirY; // Set dy
		System.out.println("Enemy Arrow Added");
	}
	
	private void moveEnemyArrows(){
		// Moves the enemy arrow in the direction of the player it was initially
		// launched towards.
		int i,j;
		for(i=0;i<enemy_num;i++){
			for(j=0;j<enemy_arrow_num[i];j++){
				enemy_arrows[i][j][0] = enemy_arrows[i][j][0]+enemy_arrows[i][j][2];
				enemy_arrows[i][j][1] = enemy_arrows[i][j][1]+enemy_arrows[i][j][3];
			}
		}
	}
}