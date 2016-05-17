import java.awt.AWTException;
import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;


public class ServerThread extends Thread implements Runnable {


	/* Members */

	private Keyboard 		mKeyboard 		 	= null;
	private Hotkey 			mHotkey			 	= null;
	private boolean 		mBoStop 		 	= false;
	private int 			mVerbosity 		 	= 0;
	private int 			mCommMode	 		= -1;
	private final int 		COMM_MODE_WIFI 		= 1;
	private final int 		COMM_MODE_BT  		= 2;
	// UDP
	private WifiCommService mWifiCommService 	= null;
	private String 			mStIpAddress 	 	= null;
	//Bluetooth
	private BtCommService	mBtCommService		= null;



	/* Constructors */




	/* Methods */

	public void close() throws SocketException{
		mBoStop = true;
		if(mCommMode == COMM_MODE_WIFI)
			mWifiCommService.close();
	}


	/**
	 * Initializes the server thread with communication mode Wifi.
	 * @return Returns 0 if success, -1 if keyboard failed,
	 * -2 if socket exception, -3 if security exception.
	 */
	public short initUdp(String stIpAddress){

		this.mStIpAddress = stIpAddress;

		mCommMode = COMM_MODE_WIFI;

		short shSuccess = 0;
		mWifiCommService = new WifiCommService();

		try {
			mWifiCommService.init();
		}catch (SocketException e){
			shSuccess = -2;
			return shSuccess;
		}catch (SecurityException e){
			shSuccess = -3;
			return shSuccess;
		}

		shSuccess = init();

		return shSuccess;
	}

	/**
	 * Initializes the server thread with communication mode Bluetooth.
	 * @return Returns 0 if success, -1 if keyboard failed,
	 * -4 if IOException or BluetoothStateException.
	 */
	public short initBt(){

		short shSuccess = 0;

		mCommMode = COMM_MODE_BT;
		mBtCommService = new BtCommService();

        // setup the server to listen for connection
        try {
        	mBtCommService.init();
        } catch (Exception e) {
    		e.printStackTrace();
            return -4;
        }

		shSuccess = init();

		return shSuccess;
	}


	private short init(){

		short shSuccess = 0;

		try {
			mKeyboard = new Keyboard();
			mHotkey = new Hotkey();
		} catch (AWTException e) {
			e.printStackTrace();
			shSuccess = -1;
			return shSuccess;
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

			Command command = null;

			/* Code specific for UDP communication */
			if (mCommMode == COMM_MODE_WIFI){
				DatagramPacket packet = null;
				try{
					packet = mWifiCommService.receive();
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
					command = MessagePacker.unpack(data);

					if (command.mType == Command.TYPE_SEND_INFO){
						Printing.info("Server IP address: " + mStIpAddress, 1);
						Printing.info("Client IP address: " + stClientIpAddress, 1);
						int[] iCommand = {0};
						// Wait 100 ms before replying
						Utils.waitMs(100);
						boolean boSuccess = mWifiCommService.sendMsg(Command.TYPE_SEND_INFO, stClientIpAddress);
						if (boSuccess){
							Printing.info("Server info sent.", 1);
						}
						else
							Printing.error("Server info sending failed.");
					}
				}
			}

			/* Code specific for Bluetooth communication */
			else if (mCommMode == COMM_MODE_BT){
				byte[] bMessage = null;
				
				try {
					bMessage = mBtCommService.receive();
				} catch (IOException e) {
					Printing.error("Receiving failed.");
					e.printStackTrace();
				}
				
				command = MessagePacker.unpack(bMessage);
				
				if (command.mType == Command.TYPE_CLIENT_STATE){
					if (command.mCommand[0] == Command.CLIENT_PAUSED){
						Printing.info("Connection lost.", 0);
						try {
							mBtCommService.close();
							mBtCommService.init();
						} catch (IOException e) {
							Printing.error("Re-connecting failed.");
							e.printStackTrace();
						}
					}
				}
			}
			if (command != null){
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
					Printing.info("Connected to phone. Listening on port " + WifiCommService.PORT_RCV + "...",1);
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
