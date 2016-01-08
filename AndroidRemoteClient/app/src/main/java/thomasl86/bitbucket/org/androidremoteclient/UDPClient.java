package thomasl86.bitbucket.org.androidremoteclient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by thomas on 30.12.15.
 */
public class UDPClient {


    // Members
    InetAddress mHost       = null;
    DatagramSocket mSock    = null;
    final static int PORT   = 7777;
    //TODO this is not a good way of checking whether we are connected
    boolean mIsConnected    = false;

    public boolean connect(String stIpAddress)
    {
        try
        {
            mSock = new DatagramSocket();
            mHost = InetAddress.getByName(stIpAddress);
            mIsConnected = true;
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean sendMsg(String stMessage)
    {
        byte[] bMessage = stMessage.getBytes();
        final DatagramPacket dp = new DatagramPacket(bMessage , bMessage.length , mHost , PORT);
        MsgThread msgThread = new MsgThread(dp);
        msgThread.start();

        return msgThread.success();
    }

    public boolean sendMsg(byte[] bMessage)
    {
        final DatagramPacket dp = new DatagramPacket(bMessage , bMessage.length , mHost , PORT);
        MsgThread msgThread = new MsgThread(dp);
        msgThread.start();

        return msgThread.success();
    }

    public boolean isConnected() { return mIsConnected; }


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
                mSock.send(mDp);
                mSuccess = true;
            }
            catch (Exception e)
            {
            }
        }

        public boolean success() { return mSuccess; };
    }
}

