import java.awt.AWTException;
import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;


public class ServerThread extends Thread implements Runnable {

	
	/* Members */

	private UDPServer 	mServer 		= null;
	private Keyboard 	mKeyboard 		= null;
	private Hotkey 		mHotkey			= null;
	private boolean 	mBoStop 		= false;
	private String 		mStIpAddress 	= null;
	private int 		mVerbosity 		= 0;
	
	
	/* Constructors */
	
	
	
	
	/* Methods */
	
	public void close() throws SocketException{ 
		mBoStop = true;
		mServer.close();
	}
	
	/**
	 * Initializes the server thread 
	 * @return Returns 0 if success, -1 if keyboard failed, 
	 * -2 if socket exception, -3 if security exception.
	 */
	public short init(String stIpAddress){
		
		this.mStIpAddress = stIpAddress;

		short shSuccess = 0;
		mServer = new UDPServer();

		try {
			mServer.init();
			mKeyboard = new Keyboard();
			mHotkey = new Hotkey();
		} catch (AWTException e) {
			shSuccess = -1;
		}catch (SocketException e){
			shSuccess = -2;
		}catch (SecurityException e){
			shSuccess = -3;
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
			DatagramPacket packet = null;
			try{
				packet = mServer.receive();
			}
			catch(SocketException e){
				mBoStop = true;
			}
			catch(IOException e){
				mBoStop = true;
			}
			if (packet != null){
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
					if (!mKeyboard.type((char)command.mCommand[0], null)){
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
					Utils.waitMs(100);
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
				case Command.TYPE_HOTKEY:
					Printing.info("Hotkey pressed.", 1);
					try {
						if(command.mCommand[0] == Command.HK_SHUTDOWN){
							mBoStop = true;
							close();
						}
						mHotkey.setHotkey(command.mCommand);
					} catch (IOException e) {
						Printing.error("IOException while trying apply hotkey.");
					}
				}
			}
		}
		Printing.info("Server thread has been shut down.", 0);
	}
}
