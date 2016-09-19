import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;


public class Media {

	
	/* Members */
	 
	private int osId;
	private Robot mRobot = null;
	private float mVolume = 0;
	private static float mVolumeFactor = 0.03f;
	
	
	/* Constructors */
	
	public Media(){
		osId = OsDetection.getOS();
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
	
	public void volume(int volumeCtrl) throws IOException {
		if (osId == OsDetection.OS_WIN) {
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
		else if (osId == OsDetection.OS_NIX){
			String strCommand;
			switch(volumeCtrl){
			case Command.VOLUME_UP:
				strCommand = "amixer -D pulse sset Master "+ Double.valueOf(mVolumeFactor*100) + "%+";
				Runtime.getRuntime().exec(strCommand);
				Printing.info(strCommand, 1);
				strCommand = "notify-send \"Volume up " + Double.valueOf(mVolumeFactor*100) + "%\" -t 1000";
				Runtime.getRuntime().exec(strCommand);
				Printing.info(strCommand, 1);
				break;
			case Command.VOLUME_DOWN:
				strCommand = "amixer -D pulse sset Master "+ Double.valueOf(mVolumeFactor*100) + "%-";
				Runtime.getRuntime().exec(strCommand);
				Printing.info(strCommand, 1);
				strCommand = "notify-send \"Volume down " + Double.valueOf(mVolumeFactor*100) + "%\" -t 1000";
				Runtime.getRuntime().exec(strCommand);
				Printing.info(strCommand, 1);
				break;
			}
		}
	}
	
	public void setVolumeFactor(float volumeFactor){ mVolumeFactor = volumeFactor; }
	
	public float getVolumeFactor() { return mVolumeFactor; }
}
