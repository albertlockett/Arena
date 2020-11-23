import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;


public class Player extends Sprite
{
	private int HP;
	private int maxHP;
	private int MP;
	private int maxMP;
	private int strength;
	private int defense;
	private int speed;
	private int exp;
	private int level;
	private int numLives;
	private boolean job;
	
	private int specialDamage;
	
	// NOTE: The strings have "Up" in their names because we were originally going to change
	// the player's direction depending on which direction he was shooting. Never got around
	// to drawing the other sprites.
	private String warriorUp = "warriorUp.png";
	private String mageUp = "mageUp.png";
	
	public Player(String job)
	{
		if(job.equals("warrior"))
		{
			ImageIcon ii = new ImageIcon(this.getClass().getResource(warriorUp));
			setImage(ii.getImage());
			
			this.maxHP = 100;
			this.HP = this.maxHP;
			
			this.maxMP = 20;
			this.MP = this.maxMP;
			
			this.strength = 10;
			this.defense = 8;
			this.speed = 4;
			
			this.job = false;
		}
		
		if(job.equals("mage"))
		{
			ImageIcon ii = new ImageIcon(this.getClass().getResource(mageUp));
			setImage(ii.getImage());
			
			this.maxHP = 70;
			this.HP = maxHP;
			
			this.maxMP = 100;
			this.MP = maxMP;
			
			this.strength = 5;
			this.defense = 4;
			this.speed = 4;
			
			this.specialDamage = 1;
			
			this.job = true;
		}	
		
		this.level = 1;
		this.exp = 0;
		this.numLives = 5;
		
		x = 500;
		y = 500;
	}
	
	
	// Some setters and getters are currently unused, but are left in for future expansion of the game.
	// eg. getDefense() should be used in calculating the damage the player takes.
	public int getHP()
	{
		return this.HP;
	}
	
	public int getMaxHP()
	{
		return this.maxHP;
	}
	
	public void setHP(int HP)
	{
		this.HP = HP;
	}
	
	public int getMP()
	{
		return this.MP;
	}
	
	public int getMaxMP()
	{
		return this.maxMP;
	}
	
	public void setMP(int MP)
	{
		this.MP = MP;
	}
	
	public int getXP()
	{
		return exp;
	}
	
	public int getMaxXP()
	{
		return 100;
	}
	
	public int getSpecialDamage()
	{
		return this.specialDamage;
	}
	
	public void takeDamage(int damage)
	{
		this.HP -= damage;
	}
	
	public void loseMP(int mp)
	{
		this.MP -= mp;
	}
	
	public int getStrength()
	{
		return this.strength;
	}
	
	public int getDefense()
	{
		return this.defense;
	}
	
	public int getSpeed()
	{
		return this.speed;
	}
	
	public int getEXP()
	{
		return this.exp;
	}
	
	public void gainEXP(int exp)
	{
		this.exp += exp;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public void gainLevel()
	{
		this.level++;
		this.exp = 0;
		
		// In future expansion of the game, maxHP, maxMP, strength, and defense would be arrays, containing
		// appropriate curves for each job. the level would be used as an index into each array. The leveling
		// curves themselves would take a considerable amount of time to create, and also tie in with a difficulty
		// curve that itself would be quite time consuming, so for the sake of the project the following code is
		// acceptable.
		this.maxHP += 10;
		this.HP = this.maxHP;
		this.maxMP += 10;
		this.MP = this.maxMP;
		
		this.strength += 2;
		this.defense += 2;
	}
	
	public int getLives()
	{
		return this.numLives;
	}
	
	public void removeLife()
	{
		this.numLives--;
	}
	
	public void addLife()
	{
		this.numLives++;
	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W)
		{
			this.dy = -speed;
		}
		if(key == KeyEvent.VK_A)
		{
			this.dx = -speed;
		}
		if(key == KeyEvent.VK_S)
		{
			this.dy = speed;
		}
		if(key == KeyEvent.VK_D)
		{
			this.dx = speed;
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W)
		{
			this.dy = -0;
		}
		if(key == KeyEvent.VK_A)
		{
			this.dx = -0;
		}
		if(key == KeyEvent.VK_S)
		{
			this.dy = 0;
		}
		if(key == KeyEvent.VK_D)
		{
			this.dx = 0;
		}
	}
	
	public void setJob(boolean job){
		this.job = job;
		if(job == false){
			ImageIcon ii = new ImageIcon(this.getClass().getResource(warriorUp));
			setImage(ii.getImage());
		} else {
			ImageIcon ii = new ImageIcon(this.getClass().getResource(mageUp));
			setImage(ii.getImage());
		}
	}
	
	public boolean getJob(){
		return job;
	}
	
}
