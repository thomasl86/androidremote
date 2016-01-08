import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class UDPServer {
	
	
	/* Members */
	
	private int mPort 				= 0;
	private byte[] mBuffer 			= null;
	private DatagramSocket mSocket	= null;

	
	/* Methods */
	
	public boolean init(int port){
		boolean boSuccess = false;

		mPort = port;
		mBuffer = new byte[65536];
		try {
			mSocket = new DatagramSocket(mPort);
			boSuccess = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}		
		
		return boSuccess;
	}
	
	public byte[] receive(){
		DatagramPacket packetIncoming = new DatagramPacket(mBuffer, mBuffer.length);
		try {
			mSocket.receive(packetIncoming);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        return packetIncoming.getData();
	}
	
	public void close(){
		mSocket.close();
	}
}
