
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.*;

public class AndroidRemoteServer implements KeyListener {
	
	/* Members */
	private static boolean boEscKeyPress = false;

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
		
		Keyboard keyboard = null;
		try {
			keyboard = new Keyboard();
		} catch (AWTException e) {
			e.printStackTrace();
			boRunServer = false;
		}
		
		if (boRunServer){
			UDPServer server = new UDPServer();
			if (server.init()){
				Printing.info("Server socket created. Listening on port " + UDPServer.PORT_RCV + "...", 0);
			}
			
			//TODO add handshake or something? 
			
			Mouse mouse = new Mouse();
			mouse.init();
			Media media = new Media();
			
			boolean boIsInitCoord = false; 
			Point ptInitPos = new Point(0,0);
			
			while(!boEscKeyPress){
				DatagramPacket packet = server.receive();
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
					if (!keyboard.type((char)command.mCommand[0])){
						Printing.error("Key not found.");
					}
					else{
						Printing.info("Key '" 
								+ Character.toString((char)command.mCommand[0]) 
								+ "' has been pressed.",1);
					}
					break;
				case Command.TYPE_SEND_INFO:
					String stServerIpAddress = getWifiIpAddress();
					Printing.info("Server IP address: " + stServerIpAddress, 1);
					Printing.info("Client IP address: " + stClientIpAddress, 1);
					int[] iCommand = {0};
					// Wait 100 ms before replying
					long delay = 100;
					long time = System.currentTimeMillis();
					long dt = 0;
					while (dt < delay){
						dt = System.currentTimeMillis() - time;
					}
					boolean boSuccess = server.sendMsg(Command.TYPE_SEND_INFO, stClientIpAddress);
					if (boSuccess){
						Printing.info("Server info sent.", 1);
					}
					else
						Printing.error("Server info sending failed.");
					break;
				}
			}
			server.close();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getID() == KeyEvent.VK_ESCAPE){
			boEscKeyPress = true;
		}
		
	}
}
