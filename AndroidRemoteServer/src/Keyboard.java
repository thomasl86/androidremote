import java.awt.AWTException;
import java.awt.Robot;
import static java.awt.event.KeyEvent.*;


public class Keyboard {

	
	/* Members */
	
    private Robot robot;
    

    /* Constructors */
    
    public Keyboard() throws AWTException {
        this.robot = new Robot();
    }

    public Keyboard(Robot robot) {
        this.robot = robot;
    }

    
    /* Methods */
    
    /**
     * 
     * @param character
     * @param modifiers
    public void type(int character, int... modifiers){
    	int lenMod = modifiers.length;
    	int[] sequence = new int[lenMod + 1];
    	for(int i = 0; i<lenMod; i++){
    		if (modifiers[i] == Command.KB_MODIFIER_CTRL)
    			sequence[i] = VK_CONTROL;
    		else if (modifiers[i] == Command.KB_MODIFIER_SHIFT)
    			sequence[i] = VK_SHIFT;
    		else if (modifiers[i] == Command.KB_MODIFIER_ALT)
    			sequence[i] = VK_ALT;
    	}
    	sequence[lenMod] = character;
    	doType(sequence);
    }*/
    
    public void type(CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            type(character);
        }
    }

    public boolean type(char character, int... modifiers) {
    	
    	boolean boSuccess = true;
    	int characterVk = 0;
    	int[] sequence = null;
    	
        switch (character) {
        
        case 'a': characterVk = VK_A; break;
        case 'b': characterVk = VK_B; break;
        case 'c': characterVk = VK_C; break;
        case 'd': characterVk = VK_D; break;
        case 'e': characterVk = VK_E; break;
        case 'f': characterVk = VK_F; break;
        case 'g': characterVk = VK_G; break;
        case 'h': characterVk = VK_H; break;
        case 'i': characterVk = VK_I; break;
        case 'j': characterVk = VK_J; break;
        case 'k': characterVk = VK_K; break;
        case 'l': characterVk = VK_L; break;
        case 'm': characterVk = VK_M; break;
        case 'n': characterVk = VK_N; break;
        case 'o': characterVk = VK_O; break;
        case 'p': characterVk = VK_P; break;
        case 'q': characterVk = VK_Q; break;
        case 'r': characterVk = VK_R; break;
        case 's': characterVk = VK_S; break;
        case 't': characterVk = VK_T; break;
        case 'u': characterVk = VK_U; break;
        case 'v': characterVk = VK_V; break;
        case 'w': characterVk = VK_W; break;
        case 'x': characterVk = VK_X; break;
        case 'y': characterVk = VK_Y; break;
        case 'z': characterVk = VK_Z; break;
        case '`': characterVk = VK_BACK_QUOTE; break;
        case '0': characterVk = VK_0; break;
        case '1': characterVk = VK_1; break;
        case '2': characterVk = VK_2; break;
        case '3': characterVk = VK_3; break;
        case '4': characterVk = VK_4; break;
        case '5': characterVk = VK_5; break;
        case '6': characterVk = VK_6; break;
        case '7': characterVk = VK_7; break;
        case '8': characterVk = VK_8; break;
        case '9': characterVk = VK_9; break;
        case '-': characterVk = VK_MINUS; break;
        case '=': characterVk = VK_EQUALS; break;
        case '!': characterVk = VK_EXCLAMATION_MARK; break;
        case '@': characterVk = VK_AT; break;
        case '#': characterVk = VK_NUMBER_SIGN; break;
        case '$': characterVk = VK_DOLLAR; break;
        case '^': characterVk = VK_CIRCUMFLEX; break;
        case '&': characterVk = VK_AMPERSAND; break;
        case '*': characterVk = VK_ASTERISK; break;
        case '(': characterVk = VK_LEFT_PARENTHESIS; break;
        case ')': characterVk = VK_RIGHT_PARENTHESIS; break;
        case '_': characterVk = VK_UNDERSCORE; break;
        case '+': characterVk = VK_PLUS; break;
        case '\t': characterVk = VK_TAB; break;
        case '\n': characterVk = VK_ENTER; break;
        case '[': characterVk = VK_OPEN_BRACKET; break;
        case ']': characterVk = VK_CLOSE_BRACKET; break;
        case '\\': characterVk = VK_BACK_SLASH; break;
        case ';': characterVk = VK_SEMICOLON; break;
        case ':': characterVk = VK_COLON; break;
        case '\'': characterVk = VK_QUOTE; break;
        case '"': characterVk = VK_QUOTEDBL; break;
        case ',': characterVk = VK_COMMA; break;
        case '.': characterVk = VK_PERIOD; break;
        case '/': characterVk = VK_SLASH; break;
        case ' ': characterVk = VK_SPACE; break;
        case '\b' : characterVk = VK_BACK_SPACE; break;
        case '\r' : characterVk = VK_HOME; break;
        case '~': doType(VK_SHIFT, VK_BACK_QUOTE); return boSuccess;
        case '%': doType(VK_SHIFT, VK_5); return boSuccess;
        case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); return boSuccess;
        case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); return boSuccess;
        case '|': doType(VK_SHIFT, VK_BACK_SLASH); return boSuccess;
        case '<': doType(VK_SHIFT, VK_COMMA); return boSuccess;
        case '>': doType(VK_SHIFT, VK_PERIOD); return boSuccess;
        case '?': doType(VK_SHIFT, VK_SLASH); return boSuccess;
        case 'A': doType(VK_SHIFT, VK_A); return boSuccess;
        case 'B': doType(VK_SHIFT, VK_B); return boSuccess;
        case 'C': doType(VK_SHIFT, VK_C); return boSuccess;
        case 'D': doType(VK_SHIFT, VK_D); return boSuccess;
        case 'E': doType(VK_SHIFT, VK_E); return boSuccess;
        case 'F': doType(VK_SHIFT, VK_F); return boSuccess;
        case 'G': doType(VK_SHIFT, VK_G); return boSuccess;
        case 'H': doType(VK_SHIFT, VK_H); return boSuccess;
        case 'I': doType(VK_SHIFT, VK_I); return boSuccess;
        case 'J': doType(VK_SHIFT, VK_J); return boSuccess;
        case 'K': doType(VK_SHIFT, VK_K); return boSuccess;
        case 'L': doType(VK_SHIFT, VK_L); return boSuccess;
        case 'M': doType(VK_SHIFT, VK_M); return boSuccess;
        case 'N': doType(VK_SHIFT, VK_N); return boSuccess;
        case 'O': doType(VK_SHIFT, VK_O); return boSuccess;
        case 'P': doType(VK_SHIFT, VK_P); return boSuccess;
        case 'Q': doType(VK_SHIFT, VK_Q); return boSuccess;
        case 'R': doType(VK_SHIFT, VK_R); return boSuccess;
        case 'S': doType(VK_SHIFT, VK_S); return boSuccess;
        case 'T': doType(VK_SHIFT, VK_T); return boSuccess;
        case 'U': doType(VK_SHIFT, VK_U); return boSuccess;
        case 'V': doType(VK_SHIFT, VK_V); return boSuccess;
        case 'W': doType(VK_SHIFT, VK_W); return boSuccess;
        case 'X': doType(VK_SHIFT, VK_X); return boSuccess;
        case 'Y': doType(VK_SHIFT, VK_Y); return boSuccess;
        case 'Z': doType(VK_SHIFT, VK_Z); return boSuccess;
        case Command.KB_UP : characterVk = VK_UP; break;
        case Command.KB_DOWN : characterVk = VK_DOWN; break;
        case Command.KB_LEFT : characterVk = VK_LEFT; break;
        case Command.KB_RIGHT : characterVk = VK_RIGHT; break;
        case Command.KB_SUPER : characterVk = VK_WINDOWS; break;
        case Command.KB_F1 : characterVk = VK_F1; break;
        case Command.KB_F2 : characterVk = VK_F2; break;
        case Command.KB_F3 : characterVk = VK_F3; break;
        case Command.KB_F4 : characterVk = VK_F4; break;
        case Command.KB_F5 : characterVk = VK_F5; break;
        case Command.KB_F6 : characterVk = VK_F6; break;
        case Command.KB_F7 : characterVk = VK_F7; break;
        case Command.KB_F8 : characterVk = VK_F8; break;
        case Command.KB_F9 : characterVk = VK_F9; break;
        default:
            //throw new IllegalArgumentException("Cannot type character " + character);
        	boSuccess = false;
        }

        if (modifiers != null){
    		sequence = new int[modifiers.length + 1];
	    	int lenMod = modifiers.length;
	    	for(int i = 0; i<lenMod; i++){
	    		if (modifiers[i] == Command.KB_MODIFIER_CTRL)
	    			sequence[i] = VK_CONTROL;
	    		else if (modifiers[i] == Command.KB_MODIFIER_SHIFT)
	    			sequence[i] = VK_SHIFT;
	    		else if (modifiers[i] == Command.KB_MODIFIER_ALT)
	    			sequence[i] = VK_ALT;
	    	}
	    	sequence[lenMod] = characterVk;
        } else {
    		sequence = new int[1];
    		sequence[0] = characterVk;
        }
        
        if (boSuccess) doType(sequence);
    	
        return boSuccess;
    }

    private void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }

        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        robot.keyRelease(keyCodes[offset]);
    }

}
