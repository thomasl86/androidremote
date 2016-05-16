package thomasl86.bitbucket.org.androidremoteclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by thomas on 16.05.16.
 */
public class HotkeyViewGroup extends ViewGroup
        implements View.OnClickListener, DialogInterface.OnClickListener {


    /* Members */

    Context mContext = null;
    HotkeyListener mHotkeyListener;


    /* Constructors */

    public HotkeyViewGroup(Context context){
        super(context);
        mContext = context;

        findViewById(R.id.button_new_pr_win).setOnClickListener(this);
        findViewById(R.id.button_new_win).setOnClickListener(this);
        findViewById(R.id.button_new_tab).setOnClickListener(this);
        findViewById(R.id.button_close_tab).setOnClickListener(this);
        findViewById(R.id.button_shutdown).setOnClickListener(this);
        findViewById(R.id.button_searchbar).setOnClickListener(this);
        findViewById(R.id.button_mute).setOnClickListener(this);
        findViewById(R.id.button_close_win).setOnClickListener(this);
    }


    /* Listener interfaces */

    public interface HotkeyListener {
        public void onHotkey(byte type, int... command);
    }

    public void setHotkeyListener(HotkeyListener hotkeyListener){
        mHotkeyListener = hotkeyListener;
    }


    /* Methods */


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_new_pr_win:
                mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_NEW_PR_WIN);
                break;
            case R.id.button_new_win:
                mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_NEW_WIN);
                break;
            case R.id.button_new_tab:
                mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_NEW_TAB);
                break;
            case R.id.button_close_tab:
                mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_CLOSE_TAB);
                break;
            case R.id.button_shutdown:
                boolean doShutdown;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setPositiveButton("OK", this);
                builder.setNegativeButton("Cancel", this);
                builder.setMessage("Are you sure?")
                        .setTitle("Server PC shutdown");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.button_searchbar:
                mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_SEARCHBAR);
                break;
            case R.id.button_mute:
                mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_MUTE);
                break;
            case R.id.button_close_win:
                mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_CLOSE_WIN);
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE)
            mHotkeyListener.onHotkey(Command.TYPE_HOTKEY, Command.HK_SHUTDOWN);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
