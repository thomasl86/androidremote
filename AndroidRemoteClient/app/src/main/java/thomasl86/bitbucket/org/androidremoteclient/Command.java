package thomasl86.bitbucket.org.androidremoteclient;

/**
 * Created by thomas on 01.01.16.
 */
public class Command {


	/* Members */

    public static final byte TYPE_CONNECTED 	    = (byte) (0x01111111);
    public static final byte TYPE_MOUSE_BUTTON 		= 0;
    public static final byte TYPE_MOUSE_MOVE_INIT 	= 1;
    public static final byte TYPE_MOUSE_MOVE 		= 2;
    public static final byte TYPE_MOUSE_WHEEL 		= 3;
    public static final byte TYPE_KB            	= 4;
    public static final byte TYPE_VOLUME        	= 5;

    public static final int MOUSE_BUT_DWN_L     = 10;
    public static final int MOUSE_BUT_DWN_R     = 11;
    public static final int MOUSE_BUT_DWN_M     = 12;
    public static final int VOLUME_UP           = 20;
    public static final int VOLUME_DOWN         = 21;

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
