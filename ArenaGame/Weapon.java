import javax.swing.ImageIcon;


public class Weapon extends Sprite
{
	private ImageIcon weapon;
	private boolean job;
	
	public Weapon(int x, int y, String job)
	{
		if(job.equals("warrior"))
		{
			weapon = new ImageIcon(this.getClass().getResource("sword.png"));
		}
		if(job.equals("mage"))
		{
			weapon = new ImageIcon(this.getClass().getResource("magic.png"));
		}
		if(job.equals("enemy"))
		{
			weapon = new ImageIcon(this.getClass().getResource("enemyShot.png"));
		}
		if(job.equals("mageSpecial"))
		{
			weapon = new ImageIcon(this.getClass().getResource("mageSpecial.png"));
		}
		
		setImage(weapon.getImage());
		
		this.alive = false;
		this.x = x;
		this.y = y;
	}
	
	public void setDX(int dx)
	{
		this.dx = dx;
	}
	
	public void setDY(int dy)
	{
		this.dy = dy;
	}
	
	public void move() //  Moves the weapon
	{	
			this.x += dx; // Move width
			
			// Check width Out of Bounds 
			if(this.x > BOARD_WIDTH) this.setAlive(false);
			if(this.x < 0) this.setAlive(false);
			
			this.y += dy; // Move height
			
			// Check height Out of bounds
			if(this.y > BOARD_HEIGHT) this.setAlive(false);
			if(this.y < 0) this.setAlive(false);
	}
	
	public void setJob(boolean job){
		this.job = job;
		if(job){
			weapon = new ImageIcon(this.getClass().getResource("magic.png"));
			setImage(weapon.getImage());
		} else {
			weapon = new ImageIcon(this.getClass().getResource("sword.png"));
			setImage(weapon.getImage());
		}
	}
	
}
