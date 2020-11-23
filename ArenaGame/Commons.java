import java.awt.Toolkit;


public interface Commons 
{
	Toolkit tk = Toolkit.getDefaultToolkit();
	
	public static final int PLAYER_X = 500;
	public static final int PLAYER_Y = 500;
	
	public static final int BOARD_WIDTH = (int) tk.getScreenSize().getWidth();
	public static final int BOARD_HEIGHT = (int) tk.getScreenSize().getHeight();
	public static final int DELAY = 17;
	public static final int MAX_NUM_WEAPONS = 10;

	public static final int NUM_ENEMIES = 5;
	
	public static final int HP_BAR_WIDTH = 100;
	public static final int HP_BAR_HEIGHT = 10;
	public static final int MP_BAR_WIDTH = 100;
	public static final int MP_BAR_HEIGHT = 10;
	public static final int XP_BAR_WIDTH = 100;
	public static final int XP_BAR_HEIGHT = 10;
	
	public static final int NUM_WAVES = 5; // Might remove this and make infinite waves
	
	public static final int MAX_PLAYER_WEAPONS = 100;
	public static final int MAX_ENEMY_WEAPONS = 100;
	
	public static final int MAX_NETWORK_PLAYERS = 4;
}
