package thomasl86.bitbucket.org.androidremoteclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends ActionBarActivity
        implements View.OnClickListener, MyViewGroup.MouseEventListener {

    protected static UDPClient mUDPClient    = null;
    String mAddress         = null;
    Point mPtTouchInit      = new Point(0, 0);
    double mMouseFact       = 3;
    boolean mIsKbOpen       = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mAddress == null) establishConnection();

        // Register listeners
        findViewById(R.id.button_left).setOnClickListener(this);
        findViewById(R.id.button_right).setOnClickListener(this);
        findViewById(R.id.button_keyboard_main).setOnClickListener(this);
        findViewById(R.id.button_up_main).setOnClickListener(this);
        findViewById(R.id.button_down_main).setOnClickListener(this);
        findViewById(R.id.button_hotkeys).setOnClickListener(this);
        MyViewGroup myViewGroup = (MyViewGroup) findViewById(R.id.viewMousePad);
        myViewGroup.setMouseEventListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_connect:
                establishConnection();
                return true;
            case R.id.action_keyboard:
                Intent intent = new Intent(this, KeyboardActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean establishConnection(){
        //Broadcast a message to the network requesting the server IP
        int[] iCommand = {0};
        byte[] bMessage = MessagePacker.pack(new Command(Command.TYPE_SEND_INFO, iCommand));
        if(UDPClient.sendBroadcastMsg(bMessage)) {
            info("Broadcast message was sent.", Toast.LENGTH_SHORT);
        }
        else{
            info("Broadcast message was NOT sent.", Toast.LENGTH_SHORT);
        }

        boolean boReceived = false;
        //TODO below is blocking! Write a listener that listens to when the message is received
        mAddress = UDPClient.receiveHostIp();

        mUDPClient = new UDPClient();
        boolean boSuccess = false;
        if (mAddress != null) {
            boSuccess = mUDPClient.connect(mAddress);
        }
        else{
            error("Could not find the server.",Toast.LENGTH_SHORT);
        }

        if(!boSuccess){
            error("Connection attempt to IP " + mAddress + " failed. Is the server running?", Toast.LENGTH_SHORT);
        } else {
            info("Connection to IP " + mAddress + " established.", Toast.LENGTH_SHORT);
        }
        return boSuccess;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_left:
                sendCommand(Command.TYPE_MOUSE_BUTTON, Command.MOUSE_BUT_DWN_L);
                break;
            case R.id.button_right:
                sendCommand(Command.TYPE_MOUSE_BUTTON, Command.MOUSE_BUT_DWN_R);
                break;
            case R.id.button_keyboard_main:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            case R.id.button_up_main:
                onKeyDown(Command.KB_UP, null);
                break;
            case R.id.button_down_main:
                onKeyDown(Command.KB_DOWN, null);
                break;
            case R.id.button_hotkeys:
                Intent intent = new Intent(this, HotkeyActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void sendCommand(byte type, int... iCommand){
        if (mUDPClient.isConnected()) {
            byte[] bMessage =
                    MessagePacker.pack(new Command(type, iCommand));
            mUDPClient.sendMsg(bMessage);
        } else {
            error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
        case KeyEvent.KEYCODE_VOLUME_UP:
            sendCommand(Command.TYPE_VOLUME, Command.VOLUME_UP);
            return true;
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            sendCommand(Command.TYPE_VOLUME, Command.VOLUME_DOWN);
            return true;
        case KeyEvent.KEYCODE_DEL:
            sendCommand(Command.TYPE_KB, '\b');
            return true;
        case Command.KB_UP:
            sendCommand(Command.TYPE_KB, Command.KB_UP);
            return true;
        case Command.KB_DOWN:
            sendCommand(Command.TYPE_KB, Command.KB_DOWN);
            return true;
        case Command.KB_LEFT:
            sendCommand(Command.TYPE_KB, Command.KB_LEFT);
            return true;
        case Command.KB_RIGHT:
            sendCommand(Command.TYPE_KB, Command.KB_RIGHT);
            return true;
            case Command.KB_HOME:
                sendCommand(Command.TYPE_KB, Command.KB_HOME);
                return true;
            case Command.KB_END:
                sendCommand(Command.TYPE_KB, Command.KB_END);
                return true;
        case KeyEvent.KEYCODE_BACK:
            return super.onKeyDown(keyCode, event);
        default:
            sendCommand(Command.TYPE_KB, event.getUnicodeChar());
            return true;
        }

    }

    public void onShortcut(int keycode, int... modifiers) {

    }


    @Override
    public void onMotion(int[] iCommand, int pointerCount) {
        if (mUDPClient.isConnected()) {
            byte[] bMessage = null;
            if (pointerCount == 1){
                bMessage =
                        MessagePacker.pack(new Command(Command.TYPE_MOUSE_MOVE, iCommand));
            }
            /*
            else if(pointerCount == 2){
                bMessage =
                        MessagePacker.pack(new Command(Command.TYPE_MOUSE_SCROLL, iCommand));
            }
            */
            if(bMessage != null) {
                mUDPClient.sendMsg(bMessage);
            }
        }
    }

    @Override
    public void onDown(int pointerCount) {
        if (mUDPClient.isConnected()) {
            int[] iCommand = {0, 0};
            byte[] bMessage = null;
            if(pointerCount == 1) {
                bMessage =
                        MessagePacker.pack(new Command(Command.TYPE_MOUSE_MOVE_INIT, iCommand));
            }
            else if(pointerCount == 2){
                bMessage =
                        MessagePacker.pack(new Command(Command.TYPE_MOUSE_SCROLL_INIT, iCommand));
            }
            if(bMessage != null) {
                mUDPClient.sendMsg(bMessage);
            }
        } else {
            error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
        }
    }

    /*
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int index = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(index);

            switch(action){
            case MotionEvent.ACTION_DOWN:
                mPtTouchInit = new Point((int)event.getX(), (int)event.getY());
                if (mUDPClient.isConnected()) {
                    int[] iCommand = {(int) event.getX()-mPtTouchInit.x, (int) event.getY()-mPtTouchInit.y};
                    byte[] bMessage =
                            MessagePacker.pack(new Command(Command.TYPE_MOUSE_MOVE_INIT, iCommand));
                    mUDPClient.sendMsg(bMessage);
                } else {
                    error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
                }
                return true;
            case MotionEvent.ACTION_UP:

                return false;
            case MotionEvent.ACTION_MOVE:
                if (mUDPClient.isConnected()) {
                    int iPosX = (int)((event.getX()-mPtTouchInit.x)*mMouseFact);
                    int iPosY = (int)((event.getY()-mPtTouchInit.y)*mMouseFact);
                    int[] iCommand = { iPosX, iPosY};
                    byte[] bMessage =
                            MessagePacker.pack(new Command(Command.TYPE_MOUSE_MOVE, iCommand));
                    mUDPClient.sendMsg(bMessage);
                } else {
                    error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
                }
                return true;
            }
            return super.onTouchEvent(event);
        }
    /*
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
                case R.id.imageViewMotion:
                    if (mUDPClient.isConnected()) {
                        int[] iCommand = {(int) event.getX(), (int) event.getY()};
                        byte[] bMessage =
                                MessagePacker.pack(new Command(Command.TYPE_MOUSE_MOVE_INIT, iCommand));
                        mUDPClient.sendMsg(bMessage);
                    } else {
                        error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
                    }
                    break;
            }
            return false;
        }
    */
    public void error(String stError, int length){
        Toast.makeText(this, "ERROR: " + stError, length).show();
    }

    public void info(String stInfo, int length){
        Toast.makeText(this, "INFO: " + stInfo, length).show();
    }


    private static String getWifiIpAddress(){
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
                        if (stInetAddress != null) return stInetAddress;
                    }
                }

            }
        }
        return null;
    }
}
