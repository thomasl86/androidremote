import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPServer {
	
	
	/* Members */
	
	public static final int PORT_RCV 	= 7777;
    public static final int PORT_SND   	= 7770;
	private byte[] mBuffer 				= null;
	private DatagramSocket mSocket		= null;

	
	/* Methods */
	
	public boolean init(){
		boolean boSuccess = false;

		//PORT_RCV = port;
		mBuffer = new byte[65536];
		try {
			mSocket = new DatagramSocket(PORT_RCV);
			mSocket.setBroadcast(true);
			boSuccess = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}		
		
		return boSuccess;
	}
	
	public DatagramPacket receive(){
		DatagramPacket packetIncoming = new DatagramPacket(mBuffer, mBuffer.length);
		try {
			mSocket.receive(packetIncoming);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        return packetIncoming;
	}
	
	public void close(){
		mSocket.close();
	}

    public boolean sendMsg(int command, String stIpAddress)
    {
        byte[] bMessage = MessagePacker.pack(Command.TYPE_SEND_INFO, stIpAddress);
        DatagramPacket dp = null;
		try {
			dp = new DatagramPacket(bMessage , bMessage.length , InetAddress.getByName(stIpAddress) , PORT_SND);
            mSocket.send(dp);
	        //MsgThread msgThread = new MsgThread(dp);
	        //msgThread.start();
	        //return msgThread.success();
            return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }


    class MsgThread extends Thread implements Runnable
    {

        private DatagramPacket mDp = null;
        private boolean mSuccess = false;

        public MsgThread (DatagramPacket dp)
        {
            mDp = dp;
        }

        @Override
        public void run() {
            try
            {
                mSocket.send(mDp);
                mSuccess = true;
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }

        public boolean success() { return mSuccess; };
    }
}
