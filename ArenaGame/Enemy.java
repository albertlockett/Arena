import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

// See "Player.java" for comments

public class Enemy extends Sprite
{
	private int HP;
	private int maxHP;
	private int MP;
	private int maxMP;
	private int strength;
	private int defense;
	private int speed;
	private int exp;
	
	private String enemyUp = "ghost.png";
	
	public Enemy(int x, int y)
	{
		ImageIcon ii = new ImageIcon(this.getClass().getResource(enemyUp));
		setImage(ii.getImage());
		
		this.maxHP = 40;
		this.HP = maxHP;
		
		this.strength = 10;
		this.speed = 4;
		this.exp = 10;
		
		this.x = x;
		this.y = y;
	}
	
	public int getHP()
	{
		return this.HP;
	}
	
	public void setHP(int HP)
	{
		this.HP = HP;
	}
	
	public int getMP()
	{
		return this.MP;
	}
	
	public void setMP(int MP)
	{
		this.MP = MP;
	}
	
	public int getMaxHP()
	{
		return this.maxHP;
	}
	
	
	public int getMaxMP()
	{
		return this.maxMP;
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

	public void setDX(int dx)
	{
		this.dx = dx;
	}
	
	public void setDY(int dy)
	{
		this.dy = dy;
	}
	
	public void takeDamage(int damage)
	{
		this.HP -= damage;
	}
}
