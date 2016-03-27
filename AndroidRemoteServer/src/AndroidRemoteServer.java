
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
	
	static boolean mBoExcKeyPressed = false;
	static ServerThread mServerThread = new ServerThread();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		OptionParser parser = new OptionParser();
		parser.acceptsAll( asList("v", "verbose"), "Be more chatty." );
		parser.acceptsAll( asList("?", "h", "help"), "Show help and exit." );
		parser.accepts( "ip", "Get the wifi ip address and exit." );
		parser.accepts("nogui", "Start the server without GUI (in console)");
		OptionSet optionSet = parser.parse( args );
		if (optionSet.hasOptions()){
			// Print the help and exit.
			if (optionSet.has("?") || optionSet.has("h") || optionSet.has("help")){
				try {
					parser.printHelpOn(System.out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Run the server in verbose mode
			if (optionSet.has("v") || optionSet.has("verbose")){
				Printing.setVerbosity(true);
				startServerThread();
			}
			// Print the wifi ip address and exit.
			if (optionSet.has("ip")){
				System.out.println(getWifiIpAddress());
			}
			if (optionSet.has("nogui")){
				startServerThread();
			}
		}
		else{
			new ServerGUI();
		}
	}
	
	protected static boolean startServerThread(){
		
		boolean boSuccess = false;
		if (mServerThread == null){
			mServerThread = new ServerThread();
		}
		if (!mServerThread.isAlive()){
			String stIpAddress = getWifiIpAddress();
			short reply = mServerThread.init(stIpAddress);
			if (reply == 0){
				mServerThread.start();
				Printing.info("Server socket created. Listening on port " + UDPServer.PORT_RCV + "...", 0);
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
		
		return boSuccess;
	}
	
	protected static void stopServerThread(){
		if(mServerThread != null){
			if (mServerThread.isAlive()){
				mServerThread.close();
			}
		}
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
