import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;


public class Media {

	
	/* Members */
	
	private Robot mRobot = null;
	private float mVolume = 0;
	private static float mVolumeFactor = 0.05f; 
	
	
	/* Constructors */
	
	public Media(){
		mVolume = Audio.getMasterOutputVolume();
	}
	
	
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
	
	public void volume(int volumeCtrl){
		switch(volumeCtrl){
		case Command.VOLUME_UP:
			mVolume = mVolume + mVolumeFactor;
			break;
		case Command.VOLUME_DOWN:
			mVolume = mVolume - mVolumeFactor;
			break;
		}
		if (mVolume < 0) mVolume = 0;
		if (mVolume > 1) mVolume = 1;
		Audio.setMasterOutputVolume(mVolume);
	}
	
	public void setVolumeFactor(float volumeFactor){ mVolumeFactor = volumeFactor; }
	
	public float getVolumeFactor() { return mVolumeFactor; }
}
