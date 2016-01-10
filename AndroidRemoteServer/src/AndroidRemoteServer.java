
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.*;

public class AndroidRemoteServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean boVerbose = false;
		boolean boRunServer = false;
		
		//BuiltinHelpFormatter helpFormatter = new BuiltinHelpFormatter(40, 5);
		//helpFormatter.form
		OptionParser parser = new OptionParser();
		parser.acceptsAll( asList("v", "verbose"), "be more chatty" );
		parser.acceptsAll( asList("?", "h", "help"), "show help" );
		 OptionSet optionSet = parser.parse( args );
		if (optionSet.hasOptions()){
			if (optionSet.has("?") || optionSet.has("h") || optionSet.has("help")){
				try {
					parser.printHelpOn(System.out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (optionSet.has("v") || optionSet.has("verbose")){
				boVerbose = true;
				boRunServer = true;
			}
		}
		else{
			boRunServer = true;
		}
		
		Keyboard keyboard = null;
		try {
			keyboard = new Keyboard();
		} catch (AWTException e) {
			e.printStackTrace();
			boRunServer = false;
		}
		
		if (boRunServer){
			UDPServer server = new UDPServer();
			int port = 7777;
			if (server.init(port)){
				info("Server socket created. Listening on port " + port + "...");
			}
			
			//TODO add handshake or something? 
			
			Mouse mouse = new Mouse();
			mouse.init();
			Media media = new Media();
			
			boolean boIsInitCoord = false; 
			Point ptInitPos = new Point(0,0);
			
			while(true){
				byte[] data = server.receive();
				if (data == null){
					error("Receiving failed. Shutting down server.");
					break;
				}
				Command command = MessagePacker.unpack(data);
				switch(command.mType){
				case Command.TYPE_MOUSE_BUTTON:
					mouse.press(command.mCommand[0]);
					break;
				case Command.TYPE_MOUSE_MOVE_INIT:
					mouse.moveInit();
					boIsInitCoord = true;
					if (boVerbose){
						info("Finger down detected.");
					}
					break;
				case Command.TYPE_MOUSE_MOVE:
					int iPosX = command.mCommand[0];
					int iPosY = command.mCommand[1];
					if (boIsInitCoord){
						ptInitPos = new Point(iPosX,iPosY);
					}
					boIsInitCoord = false;
					iPosX = iPosX - ptInitPos.x;
					iPosY = iPosY - ptInitPos.y;
					mouse.move(new Point(iPosX, iPosY));
					if (boVerbose){
						System.out.println("Mouse pos: (" + iPosX + "," + iPosY +")");
					}
					break;
				case Command.TYPE_MOUSE_WHEEL:
					mouse.wheel(command.mCommand[0]);
				case Command.TYPE_CONNECTED:
					info("Connected to phone. Listening on port " + port + "...");
					break;
				case Command.TYPE_VOLUME:
					media.volume(command.mCommand[0]);
					break;
				case Command.TYPE_KB:
					if (!keyboard.type((char)command.mCommand[0])){
						error("Key not found.");
					}
					else if (boVerbose){
						info("Key '" 
								+ Character.toString((char)command.mCommand[0]) 
								+ "' has been pressed.");
					}
					break;
				}
			}
			server.close();
		}
	}
    
   // Simple function to echo data to terminal
   public static void echo(String msg)
   {
       System.out.println(msg);
   }

   // Simple function to echo data to terminal
   public static void error(String msg)
   {
       System.out.println("ERROR: " + msg);
   }

   // Simple function to echo data to terminal
   public static void info(String msg)
   {
       System.out.println("INFO: " + msg);
   }
}
