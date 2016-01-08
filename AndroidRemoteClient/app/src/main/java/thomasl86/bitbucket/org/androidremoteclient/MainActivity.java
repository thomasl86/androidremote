package thomasl86.bitbucket.org.androidremoteclient;

import android.app.Notification;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, MyViewGroup.MouseEventListener {

    public UDPClient mUDPClient    = null;
    String mAddress         = "192.168.1.70";
    Point mPtTouchInit      = new Point(0, 0);
    double mMouseFact        = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUDPClient = new UDPClient();

        // Register listeners
        Button buttonLeft = (Button) findViewById(R.id.button_left);
        buttonLeft.setOnClickListener(this);
        Button buttonRight = (Button) findViewById(R.id.button_right);
        buttonRight.setOnClickListener(this);
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
                boolean bSuccess = mUDPClient.connect(mAddress);
                if (bSuccess) {
                    int[] iCommand = {0};
                    byte[] bMessage = MessagePacker.pack(new Command(Command.TYPE_CONNECTED, iCommand));
                    mUDPClient.sendMsg(bMessage);
                }
                else{
                    error("Connection attempt to IP " + mAddress + " failed.", Toast.LENGTH_LONG);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_left:
                if (mUDPClient.isConnected()) {
                    int[] iCommand = {Command.MOUSE_BUT_DWN_L};
                    byte[] bMessage =
                            MessagePacker.pack(new Command(Command.TYPE_MOUSE_BUTTON, iCommand));
                    mUDPClient.sendMsg(bMessage);
                }
                else
                {
                    error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
                }
                break;
            case R.id.button_right:
                if (mUDPClient.isConnected()) {
                    int[] iCommand = {Command.MOUSE_BUT_DWN_R};
                    byte[] bMessage =
                            MessagePacker.pack(new Command(Command.TYPE_MOUSE_BUTTON, iCommand));
                    mUDPClient.sendMsg(bMessage);
                }
                else
                {
                    error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
        case KeyEvent.KEYCODE_VOLUME_UP:
            if (mUDPClient.isConnected()) {
                int[] iCommand = {Command.VOLUME_UP};
                byte[] bMessage =
                        MessagePacker.pack(new Command(Command.TYPE_VOLUME, iCommand));
                mUDPClient.sendMsg(bMessage);
            }
            else
            {
                error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
            }
            return true;
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            if (mUDPClient.isConnected()) {
                int[] iCommand = {Command.VOLUME_DOWN};
                byte[] bMessage =
                        MessagePacker.pack(new Command(Command.TYPE_VOLUME, iCommand));
                mUDPClient.sendMsg(bMessage);
            }
            else
            {
                error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMotion(int[] iCommand) {
        if (mUDPClient.isConnected()) {
            byte[] bMessage =
                    MessagePacker.pack(new Command(Command.TYPE_MOUSE_MOVE, iCommand));
            mUDPClient.sendMsg(bMessage);
        } else {
            error("Not connected to IP " + mAddress + ".", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onDown() {
        if (mUDPClient.isConnected()) {
            int[] iCommand = {0, 0};
            byte[] bMessage =
                    MessagePacker.pack(new Command(Command.TYPE_MOUSE_MOVE_INIT, iCommand));
            mUDPClient.sendMsg(bMessage);
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

    public void info(String stError, int length){
        Toast.makeText(this, "INFO: " + stError, length).show();
    }
}
