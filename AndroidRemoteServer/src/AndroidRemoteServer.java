
import java.awt.List;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.*;

public class AndroidRemoteServer {
	
	/* Members */
	
	private static boolean mBoExcKeyPressed = false;
	private static ServerThread mServerThread = new ServerThread();

    // The communication mode: Bluetooth or Wifi
    public static final int COMM_MODE_NONE          = 0;
    public static final int COMM_MODE_WIFI          = 1;
    public static final int COMM_MODE_BT            = 2;
    private static int mCommMode                    = COMM_MODE_NONE;
    private static boolean mStartGui				= false;
    private static boolean mStartServer				= false;	
    
    private static final String STR_COMM_MODE_WIFI	= "w";
    private static final String STR_COMM_MODE_BT	= "b";
	
	/* Methods */

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		OptionParser parser = new OptionParser();
		parser.acceptsAll( asList("v", "verbose"), "Be more chatty." );
		parser.acceptsAll( asList("?", "h", "help"), "Show help and exit." );
		parser.accepts( "ip", "Get the wifi ip address and exit." );
		parser.accepts("gui", "Start the server with GUI");
		parser.acceptsAll(
				asList("c","comm-mode"), 
				"Communication mode. "
				+ "\nOptions: '"+STR_COMM_MODE_BT+"' for Bluetooth, "
				+ "'"+STR_COMM_MODE_WIFI+"' for WiFi"
						).withRequiredArg();
		OptionSet optionSet = parser.parse( args );
		if (optionSet.hasOptions()){
			if (optionSet.has("gui")){
				mStartGui = true;
				mStartServer = true;
			}
			// Run the server in verbose mode
			if (optionSet.has("v") || optionSet.has("verbose")){
				Printing.setVerbosity(true);
			}
			/* Options that start the server */
			if (optionSet.has("comm-mode")){
				String stCommMode = (String) optionSet.valueOf("comm-mode");
				if(stCommMode.equals(STR_COMM_MODE_BT)){
					mCommMode = COMM_MODE_BT;
					mStartServer = true;
				}
				else if(stCommMode.equals(STR_COMM_MODE_WIFI)){
					mCommMode = COMM_MODE_WIFI;
					mStartServer = true;
				}
				else{
					Printing.error("Wrong option for 'comm-mode'. Exiting.");
				}
			}
			/* Options that do not necessarily start the server */
			// Print the help and exit.
			if (optionSet.has("?") || optionSet.has("h") || optionSet.has("help")){
				mStartServer = false;
				try {
					parser.printHelpOn(System.out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// Print the wifi ip address and exit.
			if (optionSet.has("ip")){
				mStartServer = false;
				System.out.println(getWifiIpAddress());
			}
		}
		/* No options means that server is started with Wifi
		 * as default communication.
		 */
		else{
			mCommMode = COMM_MODE_WIFI;
			mStartServer = true;
		}

		if(mStartServer){
			if(mStartGui) {
				new ServerGUI();
			}
			else{
				startServerThread(mCommMode);
			}
		}
	}
	
	protected static boolean startServerThread(int commMode){
		
		boolean boSuccess = false;
		if (mServerThread == null){
			mServerThread = new ServerThread();
		}
		if (!mServerThread.isAlive()){
			if(commMode == COMM_MODE_WIFI){
				String stIpAddress = getWifiIpAddress();
				short reply = mServerThread.initUdp(stIpAddress);
				if (reply == 0){
					mServerThread.start();
					Printing.info("Server socket created. Listening on port " + WifiCommService.PORT_RCV + "...", 0);
					boSuccess = true;
				}
				else if (reply == -1) {
					Printing.error("Server socket creation failed. Keyboard class failed to initialize.");
				}
				else if (reply == -2) {
					Printing.error("Server socket creation failed. SocketException.");
				}
				else if (reply == -3) {
					Printing.error("Server socket creation failed. SecurityException.");
				}
			}
			else if(commMode == COMM_MODE_BT){
				short reply = mServerThread.initBt();
				if (reply == 0){
					mServerThread.start();
					boSuccess = true;
				}
				else if (reply == -1) {
					Printing.error("Server socket creation failed. Keyboard class failed to initialize.");
				}
				else if(reply == -4){
					Printing.error("Could not set up Bluetooth connection.");
				}
			}
			else{
				Printing.error("No communication mode chosen. Exiting.");
			}
		}
		
		return boSuccess;
	}
	
	protected static void stopServerThread(){
		if(mServerThread != null){
			if (mServerThread.isAlive()){
				try {
					mServerThread.close();
					
				} catch (SocketException e) {
					Printing.error("SocketException while closing port.");
				}
			}
		}
		// Waiting until the server thread has shut down
		while(mServerThread.isAlive());
		Printing.info("Server socket closed.", 0);
	}

	public static String getWifiIpAddress(){
		Enumeration<NetworkInterface> networkInterfaces = null;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Find the wifi interface by going through all available interfaces
		if (networkInterfaces != null){
			while (networkInterfaces.hasMoreElements())
			{
			    NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
			    if (networkInterface.getName().startsWith("wlan")){
			    	Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			    	while(inetAddresses.hasMoreElements()){
			    		InetAddress inetAddress = inetAddresses.nextElement();
			    		String stInetAddress = IPAddress.asString(inetAddress.getAddress());
			    		if (stInetAddress != null){
				    		return stInetAddress;
			    		}
			    	}
			    }
			    
			}
		}
		return null;
	}
}
