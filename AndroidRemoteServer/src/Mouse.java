import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;


public class Mouse {
	
	
	/* Members */
	
	public static final int BUT_DWN_L 	= Command.MOUSE_BUT_DWN_L;
	public static final int BUT_DWN_R 	= Command.MOUSE_BUT_DWN_R;
	public static final int BUT_DWN_M 	= Command.MOUSE_BUT_DWN_M;
	public static final int CMD_MOVE 	= Command.TYPE_MOUSE_MOVE;
	public static final int CMD_BUTTON 	= Command.TYPE_MOUSE_BUTTON;
	public static final int CMD_WHEEL 	= Command.TYPE_MOUSE_WHEEL;
	private Robot mRobot 		= null;
	private Point mPtLocation 	= new Point(0,0); 
	
	
	/* Constructors */
	
	
	
	/* Methods */
	
	/**
	 * Initializes the mouse.
	 * @return Returns the success of the init.
	 */
	public boolean init(){
		boolean boSuccess = false;
		
		// Create an object of Robot, 
		// enables native system input events
		try {
			mRobot = new Robot();
			boSuccess = true;
		} catch (AWTException e) {
			e.printStackTrace();
		} 
		
		return boSuccess;
	}
	
	public void moveInit(){
		mPtLocation = getLocation();
	}
	
	/**
	 * Moves the mouse to the given relative position.
	 * @param ptRelative Relative amount of distance by which the mouse is moved.
	 */
	public Point move(Point ptRelative){
		Point ptPos = new Point(mPtLocation.x + ptRelative.x, mPtLocation.y + ptRelative.y);
		mRobot.mouseMove(ptPos.x, ptPos.y);
		//mRobot.mouseMove(ptRelative.x, ptRelative.y);
		return ptPos;
	}
	
	/**
	 * Presses a mouse button.
	 * @param button The button to press. Use the final members BUT_DWN_L, BUT_DWN_R and BUT_DWN_M. 
	 */
	public void press(int button){
		int inputEvent = 0;
		switch (button) {
		case BUT_DWN_L:
			inputEvent = InputEvent.BUTTON1_DOWN_MASK;
			break;
		case BUT_DWN_M:
			inputEvent = InputEvent.BUTTON2_DOWN_MASK;
			break;
		case BUT_DWN_R:
			inputEvent = InputEvent.BUTTON3_DOWN_MASK;
			break;
		}
		mRobot.mousePress(inputEvent);
		mRobot.mouseRelease(inputEvent);
	}
	
	/**
	 * Scrolls the mouse wheel.
	 * @param wheelAmt Amount by which the mouse wheel should be scrolled. 
	 * Positive and negative values are allowed. 
	 * Negative values indicate movement up/away from the user, positive down/towards the user.
	 */
	public void wheel(int wheelAmt){ mRobot.mouseWheel(wheelAmt); }
	
	/**
	 * 
	 * @return Returns the current location of the mouse on the screen.
	 */
	public Point getLocation(){ 
		return MouseInfo.getPointerInfo().getLocation();
	}
}
