package thomasl86.bitbucket.org.androidremoteclient;

/**
 * Created by thomas on 01.01.16.
 */
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

    public static final int MOUSE_BUT_DWN_L 		= 10;
    public static final int MOUSE_BUT_DWN_R 		= 11;
    public static final int MOUSE_BUT_DWN_M 		= 12;
    public static final int VOLUME_UP           	= 20;
    public static final int VOLUME_DOWN         	= 21;
    public static final int KB_MODIFIER_ALT     	= 30;
    public static final int KB_MODIFIER_CTRL    	= 31;
    public static final int KB_MODIFIER_SHIFT   	= 32;
    public static final int KB_HOME             	= 'r';
    public static final int KB_END              	= 0x0A;
    public static final int KB_UP               	= 0x2191;
    public static final int KB_DOWN             	= 0x2193;
    public static final int KB_LEFT             	= 0x2190;
    public static final int KB_RIGHT            	= 0x2192;

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
