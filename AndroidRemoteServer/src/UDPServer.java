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
	
	public void init() throws SocketException {

		//PORT_RCV = port;
		mBuffer = new byte[65536];
		mSocket = new DatagramSocket(PORT_RCV);
		mSocket.setBroadcast(true);
	}
	
	public DatagramPacket receive() throws SocketException, IOException{
		DatagramPacket packetIncoming = new DatagramPacket(mBuffer, mBuffer.length);
		mSocket.receive(packetIncoming);
        return packetIncoming;
	}
	
	public void close() throws SocketException{
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
