package thomasl86.bitbucket.org.androidremoteclient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends ActionBarActivity
        implements View.OnClickListener, MyViewGroup.MouseEventListener {


    /* Members */
    //==============================================================================================

    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    private static WifiCommService mWifiCommService = null;
    private String mAddress                         = null;
    private Point mPtTouchInit                      = new Point(0, 0);
    private double mMouseFact                       = 3;
    private boolean mIsKbOpen                       = false;
    private BluetoothAdapter mBluetoothAdapter      = null;
    private static BtCommService mBtCommService     = null;
    private static BluetoothDevice mBtDevice        = null;

    // Name of the connected device
    private String mConnectedDeviceName             = null;
    private String mConnectedDeviceAddress          = null;

    // The communication mode: Bluetooth or Wifi
    public static final int COMM_MODE_NONE          = 0;
    public static final int COMM_MODE_WIFI          = 1;
    public static final int COMM_MODE_BT            = 2;
    private int mCommMode                           = COMM_MODE_NONE;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE    = 1;
    public static final int MESSAGE_READ            = 2;
    public static final int MESSAGE_WRITE           = 3;
    public static final int MESSAGE_DEVICE_NAME     = 4;
    public static final int MESSAGE_TOAST           = 5;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_COMM_MODE      = 2;
    private static final int REQUEST_HOTKEY         = 3;

    public static final String STR_DEVICE_NAME      = "device_name";
    public static final String STR_TOAST            = "toast";
    public static final String STR_DATA_STRINGS     = "BluetoothCommDataStrings";
    public static final String STR_DATA_INTEGERS    = "BluetoothCommDataIntegers";

    // Objects for serializing and saving data to memory
    private SaveRead<String[]> mSaveReadBtCommStr   = new SaveRead<>();
    private SaveRead<int[]> mSaveReadBtCommInt      = new SaveRead<>();


    /* Methods */
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (D) Log.d(TAG, "onCreate() called.");

        //if(mAddress == null) establishConnectionWifi();

        if(savedInstanceState != null) {
            mConnectedDeviceAddress = savedInstanceState.getString("mConnectedDeviceAddress");
            mConnectedDeviceName = savedInstanceState.getString("mConnectedDeviceName");
        }

        // Register listeners
        findViewById(R.id.button_left).setOnClickListener(this);
        findViewById(R.id.button_right).setOnClickListener(this);
        findViewById(R.id.button_keyboard_main).setOnClickListener(this);
        findViewById(R.id.button_up_main).setOnClickListener(this);
        findViewById(R.id.button_down_main).setOnClickListener(this);
        findViewById(R.id.button_hotkeys).setOnClickListener(this);
        MyViewGroup myViewGroup = (MyViewGroup) findViewById(R.id.viewMousePad);
        myViewGroup.setMouseEventListener(this);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            error("Bluetooth is not available", Toast.LENGTH_LONG);
        }
    }


    @Override
    protected void onStart() {
        if (D) Log.d(TAG, "onStart() called.");
        super.onStart();

        // Load the data serialized in onStop()
        String[] dataStrings = mSaveReadBtCommStr.readFile(this, STR_DATA_STRINGS);
        if (dataStrings != null) {
            mConnectedDeviceAddress = dataStrings[0];
            mConnectedDeviceName = dataStrings[1];
        }
        int[] dataInts = mSaveReadBtCommInt.readFile(this, STR_DATA_INTEGERS);
        if (dataInts != null) {
            mCommMode = dataInts[0];
        }

        establishConnection(mCommMode);
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
            case R.id.action_choose_mode:
                establishConnection(mCommMode);
                return true;
            case R.id.action_connect_bt:
                establishConnectionBt();
                return true;
            case R.id.action_scan_bt:
                // Tell server to cut connection
                sendCommand(false, Command.TYPE_CLIENT_STATE, Command.CLIENT_PAUSED);
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, BtDeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.action_connect_wifi:
                establishConnectionWifi();
                return true;
            case R.id.action_keyboard:
                Intent intent = new Intent(this, KeyboardActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void establishConnection(int commMode) {

        if (D) Log.d(TAG, "establishConnection() called.");

        if(commMode == COMM_MODE_NONE){
            if (D) Log.d(TAG, "commMode == COMM_MODE_NONE");
            Intent connectionIntent = new Intent(this, ConnectionActivity.class);
            startActivityForResult(connectionIntent, REQUEST_COMM_MODE);
        }
        else if(commMode == COMM_MODE_BT){
            if (D) Log.d(TAG, "commMode == COMM_MODE_BT");
            establishConnectionBt();
        }
        else if(commMode == COMM_MODE_WIFI){
            if (D) Log.d(TAG, "commMode == COMM_MODE_WIFI");
            establishConnectionWifi();
        }
    }


    private boolean establishConnectionWifi(){
        //Broadcast a message to the network requesting the server IP
        int[] iCommand = {0};
        byte[] bMessage = MessagePacker.pack(new Command(Command.TYPE_SEND_INFO, iCommand));
        if(!WifiCommService.sendBroadcastMsg(bMessage)) {
            error("Broadcast message could0 NOT be sent.", Toast.LENGTH_SHORT);
        }

        boolean boReceived = false;
        //TODO below is blocking! Write a listener that listens to when the message is received
        mAddress = WifiCommService.receiveHostIp();

        mWifiCommService = new WifiCommService();
        boolean boSuccess = false;
        if (mAddress != null) {
            boSuccess = mWifiCommService.connect(mAddress);
        }

        if(!boSuccess) {
            error("Connection attempt failed. Is the server running?", Toast.LENGTH_SHORT);
        }
        return boSuccess;
    }


    private void establishConnectionBt(){
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBtCommService = new BtCommService(mHandler);

        if(mConnectedDeviceAddress == null) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, BtDeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            if (D) Log.d(TAG, "BtDeviceListActivity started.");
        }
        else {
            // Get the BLuetoothDevice object
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mConnectedDeviceAddress);
            // Attempt to connect to the device
            mBtCommService.connect(device);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_CONNECT_DEVICE:
                if (D) Log.d(TAG, "Case REQUEST_CONNECT_DEVICE entered.");
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    mConnectedDeviceAddress = address;
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBtCommService.connect(device);
                }
                break;
            case REQUEST_COMM_MODE:
                if (resultCode == Activity.RESULT_OK){
                    mCommMode = data.getExtras().getInt(ConnectionActivity.STR_COMM_MODE);

                    switch(mCommMode){
                        case COMM_MODE_BT:
                            establishConnectionBt();
                            break;
                        case COMM_MODE_WIFI:
                            establishConnectionWifi();
                            break;
                    }

                }
                break;
            case REQUEST_HOTKEY:
                if (resultCode == Activity.RESULT_OK){
                    byte type = data.getExtras().getByte(HotkeyActivity.STR_COMM_TYPE);
                    int command = data.getExtras().getInt(HotkeyActivity.STR_COMMAND);
                    sendCommand(true, type, command);
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_left:
                sendCommand(true, Command.TYPE_MOUSE_BUTTON, Command.MOUSE_BUT_DWN_L);
                break;
            case R.id.button_right:
                sendCommand(true, Command.TYPE_MOUSE_BUTTON, Command.MOUSE_BUT_DWN_R);
                break;
            case R.id.button_keyboard_main:
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(
                        InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            case R.id.button_up_main:
                onKeyDown(Command.KB_UP, null);
                break;
            case R.id.button_down_main:
                onKeyDown(Command.KB_DOWN, null);
                break;
            case R.id.button_hotkeys:
                Intent intent = new Intent(this, HotkeyActivity.class);
                startActivityForResult(intent, REQUEST_HOTKEY);
                break;
            case R.id.button_bluetooth:
                establishConnectionBt();
                mCommMode = COMM_MODE_BT;
            case R.id.button_wifi:
                establishConnectionWifi();
                mCommMode = COMM_MODE_WIFI;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
        case KeyEvent.KEYCODE_VOLUME_UP:
            sendCommand(true, Command.TYPE_VOLUME, Command.VOLUME_UP);
            return true;
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            sendCommand(true, Command.TYPE_VOLUME, Command.VOLUME_DOWN);
            return true;
        case KeyEvent.KEYCODE_DEL:
            sendCommand(true, Command.TYPE_KB, '\b');
            return true;
        case Command.KB_UP:
            sendCommand(true, Command.TYPE_KB, Command.KB_UP);
            return true;
        case Command.KB_DOWN:
            sendCommand(true, Command.TYPE_KB, Command.KB_DOWN);
            return true;
        case Command.KB_LEFT:
            sendCommand(true, Command.TYPE_KB, Command.KB_LEFT);
            return true;
        case Command.KB_RIGHT:
            sendCommand(true, Command.TYPE_KB, Command.KB_RIGHT);
            return true;
            case Command.KB_HOME:
                sendCommand(true, Command.TYPE_KB, Command.KB_HOME);
                return true;
            case Command.KB_END:
                sendCommand(true, Command.TYPE_KB, Command.KB_END);
                return true;
        case KeyEvent.KEYCODE_BACK:
            return super.onKeyDown(keyCode, event);
        default:
            sendCommand(true, Command.TYPE_KB, event.getUnicodeChar());
            return true;
        }

    }


    @Override
    public void onMotion(int[] iCommand, int pointerCount) {
        sendCommand(false, Command.TYPE_MOUSE_MOVE, iCommand);
    }


    @Override
    public void onDown(int pointerCount) {
        int[] iCommand = {0, 0};
        byte[] bMessage = null;
        if(pointerCount == 1) {
            sendCommand(true, Command.TYPE_MOUSE_MOVE_INIT, iCommand);
        }
        else if(pointerCount == 2){
            sendCommand(true, Command.TYPE_MOUSE_SCROLL_INIT, iCommand);
        }
    }


    @Override
    protected void onPause() {
        if (D) Log.d(TAG, "onPause() called.");
        super.onPause();
    }


    @Override
    protected void onStop() {
        if (D) Log.d(TAG, "onStop() called.");
        super.onStop();
        sendCommand(false, Command.TYPE_CLIENT_STATE, Command.CLIENT_PAUSED);
        if (mCommMode == COMM_MODE_BT)
            mBtCommService.stop();

        String[] stringArray = {mConnectedDeviceAddress, mConnectedDeviceName};
        mSaveReadBtCommStr.saveFile(this, stringArray, STR_DATA_STRINGS);
        int[] intArray = {mCommMode};
        mSaveReadBtCommInt.saveFile(this, intArray, STR_DATA_INTEGERS);
    }


    @Override
    protected void onDestroy() {
        if (D) Log.d(TAG, "onDestroy() called.");

        int[] intArray = {COMM_MODE_NONE};
        mSaveReadBtCommInt.saveFile(this, intArray, STR_DATA_INTEGERS);

        super.onDestroy();
    }


    public void sendCommand(boolean errorMsg, byte type, int... iCommand){
        boolean isConnected = false;
        boolean isListening = false;
        int state = 0;
        byte[] bMessage =
                MessagePacker.pack(new Command(type, iCommand));
        switch (mCommMode){
            case COMM_MODE_WIFI:
                isConnected = mWifiCommService.isConnected();
                if (isConnected) {
                    mWifiCommService.write(bMessage);
                }
                break;
            case COMM_MODE_BT:
                state = mBtCommService.getState();
                isConnected = (state == BtCommService.STATE_CONNECTED);
                if(isConnected){
                    mBtCommService.write(bMessage);
                }
                break;
        }

        if (!isConnected) {
            if (errorMsg) {
                error("Not connected.", Toast.LENGTH_LONG);
            }
        }
    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BtCommService.STATE_CONNECTED:
                            //mTitle.setText(R.string.title_connected_to);
                            //mTitle.append(mConnectedDeviceName);
                            //Toast.makeText(getApplicationContext(), "Connected to "
                            //        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                            break;
                        case BtCommService.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            //Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
                            break;
                        case BtCommService.STATE_LISTEN:
                        case BtCommService.STATE_NONE:
                            //mTitle.setText(R.string.title_not_connected);
                            //Toast.makeText(MainActivity.this, "Not connected.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(STR_DEVICE_NAME);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(STR_TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


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
