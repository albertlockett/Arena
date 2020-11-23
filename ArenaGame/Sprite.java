import java.awt.Image;


public class Sprite implements Commons
{
	protected Image image;
	protected int x;	// X coordinate
	protected int dx;	// X velocity
	protected int y;	// y coordinate
	protected int dy;	// y velocity
	protected String direction;
	protected boolean alive; //Dead or Alive 
	
	public Sprite()// Default constructor
	{
		alive = true;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public boolean isAlive()
	{
		return this.alive;
	}
	
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
	
	public Image getImage()
	{
		return this.image;
	}
	
	public void setImage(Image image)
	{
		this.image = image;
	}
	
	public void move() //  Moves the character a bit
	{	
			this.x += dx; // Move width
			
			// Check width Out of Bounds - if yes = stop
			if(this.x > BOARD_WIDTH - 35) this.x = BOARD_WIDTH - 35;
			if(this.x < 0) this.x = 0;
			
			this.y += dy; // Move height
			
			// Check height Out of bounds - if yes = stop
			if(this.y > BOARD_HEIGHT - 100) this.y = BOARD_HEIGHT - 100;
			if(this.y < 0) this.y = 0;
	}

	public int getWidth()
	{
		return image.getWidth(null);
	}
	
	public int getHeight()
	{
		return image.getHeight(null);
	}
	

}
