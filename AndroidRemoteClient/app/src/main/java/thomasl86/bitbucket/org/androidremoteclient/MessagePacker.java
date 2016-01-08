package thomasl86.bitbucket.org.androidremoteclient;

import java.nio.ByteBuffer;
import java.util.Arrays;

/*
 * Data encoding:
 * * 1st byte:
 * 		- Mouse motion command: 0
 * 		- Mouse button press: 1,
 * 		- Mouse wheel: 2,
 * 		- Keyboard button 3
 * * bytes 2-5 (integer): Mouse command or key pressed
 *
 *  Example:
 *  First byte: 1010 0000 -> mouse button pressed
 *
 *  * If the message is a mouse motion command:
 *  	- Each coordinate is encoded in 4 bytes (integer)
 *  	  with x-coordinate first and then y-coordinate.
 */


/**
 * Created by thomas on 01.01.16.
 */
public class MessagePacker {


	/* Members */




	/* Constructors */




	/* Methods */

    public static Command unpack(byte[] bMessage){
        // Split the message into header and command
        byte bType = bMessage[0];
        // Decode the message depending on the type
        int[] iCommand = null;
        if (bType == Command.TYPE_MOUSE_MOVE){
            iCommand = new int[2];
            byte[] bCommand = Arrays.copyOfRange(bMessage, 1, 9);
            byte[] bPosX = Arrays.copyOfRange(bCommand, 0, 4);
            byte[] bPosY = Arrays.copyOfRange(bCommand, 4, bCommand.length);
            iCommand[0] = ByteBuffer.wrap(bPosX).getInt();
            iCommand[1] = ByteBuffer.wrap(bPosY).getInt();
        }
        else{
            iCommand = new int[1];
            byte[] bCommand = Arrays.copyOfRange(bMessage, 1, 5);
            iCommand[0] = ByteBuffer.wrap(bCommand).getInt();
        }

        return new Command(bType, iCommand);
    }

    public static byte[] pack(Command command){
        byte[] bMessage = new byte[9];
        bMessage[0] = command.mType;
        ByteBuffer byteBuf = ByteBuffer.allocate(bMessage.length-1);
        for (int i=0; i<command.mCommand.length; i++){
            byteBuf.putInt(command.mCommand[i]);
        }
        byte[] bCommand = byteBuf.array();
        for (int i=1; i<bMessage.length; i++){
            bMessage[i] = bCommand[i-1];
        }

        return bMessage;
    }


}

