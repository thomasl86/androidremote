import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class AndroidRemoteServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
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
				info("Finger down detected.");
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
				System.out.println("Mouse pos: (" + iPosX + "," + iPosY +")");
				break;
			case Command.TYPE_MOUSE_WHEEL:
				mouse.wheel(command.mCommand[0]);
			case Command.TYPE_CONNECTED:
				info("Connected to phone. Listening on port " + port + "...");
				break;
			case Command.TYPE_VOLUME:
				media.volume(command.mCommand[0]);
				break;
			}
		}
		server.close();
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
