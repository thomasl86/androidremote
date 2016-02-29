import java.awt.AWTException;
import java.awt.Point;
import java.net.DatagramPacket;
import java.net.SocketException;


public class ServerThread extends Thread implements Runnable {

	
	/* Members */

	private UDPServer mServer = null;
	private Keyboard mKeyboard = null;
	private boolean mBoStop = false;
	private String mStIpAddress = null;
	
	
	/* Constructors */
	
	
	
	
	/* Methods */
	
	public void close(){ 
		this.mBoStop = true;
		mServer.close();
	}
	
	/**
	 * Initializes the server thread 
	 * @return Returns 0 if success, -1 if keyboard failed, -2 if server failed.
	 */
	public short init(String stIpAddress){
		
		this.mStIpAddress = stIpAddress;

		short shSuccess = 0;
		mServer = new UDPServer();

		try {
			mServer.init();
			mKeyboard = new Keyboard();
		} catch (AWTException e) {
			shSuccess = -1;
		}catch (SocketException e){
			shSuccess = -2;
		}catch (SecurityException e){
			shSuccess = -2;
		}
		
		return shSuccess;
	}
	
	@Override
	public void run() {
		
		Mouse mouse = new Mouse();
		mouse.init();
		Media media = new Media();
		
		boolean boIsInitCoord = false; 
		Point ptInitPos = new Point(0,0);
		
		mBoStop = false;
		
		while(!mBoStop){
			DatagramPacket packet = mServer.receive();
			byte[] data = packet.getData();
			String stClientIpAddress = packet.getAddress().getHostAddress();
			if (data == null){
				Printing.error("Receiving failed. Shutting down server.");
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
				Printing.info("Finger down detected.",1);
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
				Printing.info("Mouse pos: (" + iPosX + "," + iPosY +")",1);
				break;
			case Command.TYPE_MOUSE_SCROLL_INIT:
				Printing.info("Two fingers down detected.",1);
				break;
			case Command.TYPE_MOUSE_SCROLL:
				mouse.scroll(command.mCommand[0]);
				Printing.info("Mouse scrolling ("+command.mCommand[0]+".", 1);
				break;
			case Command.TYPE_CONNECTED:
				Printing.info("Connected to phone. Listening on port " + UDPServer.PORT_RCV + "...",1);
				break;
			case Command.TYPE_VOLUME:
				media.volume(command.mCommand[0]);
				break;
			case Command.TYPE_KB:
				if (!mKeyboard.type((char)command.mCommand[0])){
					Printing.error("Key not found.");
				}
				else{
					Printing.info("Key '" 
							+ Character.toString((char)command.mCommand[0]) 
							+ "' has been pressed.",1);
				}
				break;
			case Command.TYPE_SEND_INFO:
				Printing.info("Server IP address: " + mStIpAddress, 1);
				Printing.info("Client IP address: " + stClientIpAddress, 1);
				int[] iCommand = {0};
				// Wait 100 ms before replying
				long delay = 100;
				long time = System.currentTimeMillis();
				long dt = 0;
				while (dt < delay){
					dt = System.currentTimeMillis() - time;
				}
				boolean boSuccess = mServer.sendMsg(Command.TYPE_SEND_INFO, stClientIpAddress);
				if (boSuccess){
					Printing.info("Server info sent.", 1);
				}
				else
					Printing.error("Server info sending failed.");
				break;
			}
		}
	}

}
