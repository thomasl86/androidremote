
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.*;

public class AndroidRemoteServer implements KeyListener {
	
	/* Members */
	
	boolean mBoExcKeyPressed = false;
	ServerThread mServerThread = new ServerThread();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean boRunServer = false;
		
		OptionParser parser = new OptionParser();
		parser.acceptsAll( asList("v", "verbose"), "Be more chatty." );
		parser.acceptsAll( asList("?", "h", "help"), "Show help and exit." );
		parser.accepts( "ip", "Get the wifi ip address and exit." );
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
				boRunServer = false;
			}
			// Run the server in verbose mode
			if (optionSet.has("v") || optionSet.has("verbose")){
				Printing.setVerbosity(true);
				boRunServer = true;
			}
			// Print the wifi ip address and exit.
			if (optionSet.has("ip")){
				System.out.println(getWifiIpAddress());
				boRunServer = false;
			}
		}
		else{
			boRunServer = true;
		}
		
		if (boRunServer){
			//TODO add code to start the server
			ServerThread serverThread = new ServerThread();
			String stIpAddress = getWifiIpAddress();
			short reply = serverThread.init(stIpAddress);
			if (reply == 0){
				serverThread.start();
				Printing.info("Server socket created. Listening on port " + UDPServer.PORT_RCV + "...", 0);
			}
			else if (reply == -1) {
				Printing.error("Server socket creation failed. Keyboard class failed to initialize.");
			}
			else if (reply == -2) {
				Printing.error("Server socket creation failed. SocketException.");
			}
		}
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

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getID() == KeyEvent.VK_SPACE){
			mBoExcKeyPressed = true;
			mServerThread.close();
			Printing.info("Space key pressed.", 1);
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
