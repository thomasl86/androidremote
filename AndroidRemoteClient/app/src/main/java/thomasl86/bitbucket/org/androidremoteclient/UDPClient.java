package thomasl86.bitbucket.org.androidremoteclient;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by thomas on 30.12.15.
 */
public class UDPClient {


    // Members
    private InetAddress mHost               = null;
    private static DatagramSocket mSock     = null;
    private final static int PORT_SND       = 7777;
    private final static int PORT_RCV       = 7770;
    //TODO this is not a good way of checking whether we are connected
    boolean mIsConnected                    = false;

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

    public static String receiveHostIp(){
        String hostIpAddress = null;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(PORT_RCV);
            MsgReceiveThread msgReceiveThread = new MsgReceiveThread(socket);
            msgReceiveThread.start();
            while(msgReceiveThread.isAlive());
            DatagramPacket packetIncoming = msgReceiveThread.mDp;
            hostIpAddress = packetIncoming.getAddress().getHostAddress();
            //byte[] bData = packetIncoming.getData();
            //hostIpAddress = Arrays.copyOfRange(bData,1,bData.length).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
        return hostIpAddress;
    }

    public boolean sendMsg(String stMessage)
    {
        byte[] bMessage = stMessage.getBytes();
        final DatagramPacket dp = new DatagramPacket(bMessage , bMessage.length , mHost , PORT_SND);
        MsgSendThread msgSendThread = new MsgSendThread(dp, mSock);
        msgSendThread.start();

        return msgSendThread.success();
    }

    public boolean sendMsg(byte[] bMessage)
    {
        final DatagramPacket dp = new DatagramPacket(bMessage , bMessage.length , mHost , PORT_SND);
        MsgSendThread msgSendThread = new MsgSendThread(dp, mSock);
        msgSendThread.start();

        return msgSendThread.success();
    }

    public static boolean sendBroadcastMsg(byte[] bMessage){
        boolean boSuccess = false;
        DatagramSocket socket = null;
        try {
            final DatagramPacket dp = new DatagramPacket(bMessage, bMessage.length, InetAddress.getByName("255.255.255.255"), PORT_SND);
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            MsgSendThread msgSendThread = new MsgSendThread(dp, socket);
            msgSendThread.start();
            while(msgSendThread.isAlive());
            boSuccess = msgSendThread.success();
        }
        catch(Exception e){
            e.printStackTrace();
            boSuccess = false;
        }
        try {
            socket.setBroadcast(false);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        socket.close();
        return boSuccess;
    }

    public boolean isConnected() { return mIsConnected; }


    static class MsgSendThread extends Thread implements Runnable
    {

        private DatagramPacket mDp = null;
        private DatagramSocket mSocket = null;
        private boolean mSuccess = false;

        public MsgSendThread(DatagramPacket dp, DatagramSocket socket)
        {
            mDp = dp;
            mSocket = socket;
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


    static class MsgReceiveThread extends Thread implements Runnable
    {

        protected DatagramPacket mDp = null;
        private DatagramSocket mSocket = null;
        private boolean mSuccess = false;

        public MsgReceiveThread(DatagramSocket socket){
            mSocket = socket;
        }

        @Override
        public void run() {
            try
            {
                byte[] buffer = new byte[65536];
                mDp = new DatagramPacket(buffer, buffer.length);
                boolean abort = false;
                while(!abort) {
                    mSocket.receive(mDp);
                    if( mDp.getData()[0] == Command.TYPE_SEND_INFO)
                        abort = true;
                }
                mSuccess = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("MsgReceiveThread", "Receiving failed");
            }
        }

        public boolean success() { return mSuccess; };
    }
}

