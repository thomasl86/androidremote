

public class Command {
	
	
	/* Members */

	public static final byte TYPE_CONNECTED 		= (byte) (0x01111111);
    public static final byte TYPE_MOUSE_BUTTON 		= 0;
    public static final byte TYPE_MOUSE_MOVE_INIT 	= 1;
    public static final byte TYPE_MOUSE_SCROLL_INIT = 2;
    public static final byte TYPE_MOUSE_MOVE 		= 3;
    public static final byte TYPE_MOUSE_SCROLL 		= 4;
    public static final byte TYPE_KB            	= 5;
    public static final byte TYPE_VOLUME        	= 6;
    public static final byte TYPE_SEND_INFO 		= 7;
    public static final byte TYPE_HOTKEY            = 8;
    
	public static final int MOUSE_BUT_DWN_L 		= 10;
	public static final int MOUSE_BUT_DWN_R 		= 11;
	public static final int MOUSE_BUT_DWN_M 		= 12;
    public static final int VOLUME_UP           	= 20;
    public static final int VOLUME_DOWN         	= 21;
    public static final int KB_MODIFIER_ALT     	= 30;
    public static final int KB_MODIFIER_CTRL    	= 31;
    public static final int KB_MODIFIER_SHIFT   	= 32;
    public static final int KB_SUPER				= 0xff;
    public static final int KB_HOME             	= 'r';
    public static final int KB_END              	= 0x0A;
    public static final int KB_UP               	= 0x2191;
    public static final int KB_DOWN             	= 0x2193;
    public static final int KB_LEFT             	= 0x2190;
    public static final int KB_RIGHT            	= 0x2192;
    public static final int KB_F1					= 1000;
    public static final int KB_F2					= 1001;
    public static final int KB_F3					= 1002;
    public static final int KB_F4					= 1003;
    public static final int KB_F5					= 1004;
    public static final int KB_F6					= 1005;
    public static final int KB_F7					= 1006;
    public static final int KB_F8					= 1007;
    public static final int KB_F9					= 1008;
    public static final int HK_SHUTDOWN             = 40;
    public static final int HK_MUTE                 = 41;
    public static final int HK_NEW_PR_WIN           = 42;
    public static final int HK_NEW_WIN              = 43;
    public static final int HK_NEW_TAB              = 44;
    public static final int HK_CLOSE_TAB            = 45;
    public static final int HK_SEARCHBAR            = 46;
    public static final int HK_CLOSE_WIN            = 47;
	
	public byte mType = -1;
	public int[] mCommand = null;
	
	
	/* Constructors */
	
	public Command(){}
	
	public Command(byte type, int[] command){
		mType = type;
		mCommand = command;
	}
	
	
	/* Methods */
	
}
