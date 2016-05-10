import java.awt.AWTException;
import java.io.IOException;
import static java.awt.event.KeyEvent.*;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;


public class Hotkey {
	
	
	/* Members */
	
	private int osId;
	private Keyboard keyboard;
	
	
	/* Constructors */
	
	public Hotkey() throws AWTException{
		osId = OsDetection.getOS();
		keyboard = new Keyboard(); 
	}
	
	
	/* Methods */
	
	public boolean setHotkey(int ... command) throws IOException{
		
		boolean boSuccess = true;
		
		switch(command[0]){
		case Command.HK_NEW_PR_WIN:
			Runtime.getRuntime().exec("firefox --private-window");
			break;
		case Command.HK_NEW_WIN:
			Runtime.getRuntime().exec("firefox");
			break;
		case Command.HK_NEW_TAB:
			keyboard.type('t', Command.KB_MODIFIER_CTRL);
			break;
		case Command.HK_CLOSE_TAB:
			keyboard.type('w', Command.KB_MODIFIER_CTRL);
			break;
		case Command.HK_SHUTDOWN:
			if (osId == OsDetection.OS_NIX){
				Runtime.getRuntime().exec("shutdown -h now");
			}
			break;
		case Command.HK_SEARCHBAR:
			if (osId == OsDetection.OS_NIX){
				Runtime.getRuntime().exec("xdotool key super");
			} else {
				keyboard.type((char)Command.KB_SUPER, null);
			}
			break;
		case Command.HK_MUTE:
			if (osId == OsDetection.OS_NIX){
				Runtime.getRuntime().exec("amixer -q -D pulse sset Master toggle");
			}
			break;
		case Command.HK_CLOSE_WIN:
			keyboard.type((char)Command.KB_F4, Command.KB_MODIFIER_ALT);
			break;
		default:
			boSuccess = false;
		}
		return boSuccess;
	}
}
