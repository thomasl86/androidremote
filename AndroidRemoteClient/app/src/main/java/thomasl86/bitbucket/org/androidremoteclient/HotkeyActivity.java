package thomasl86.bitbucket.org.androidremoteclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by thomas on 06.05.16.
 */
public class HotkeyActivity extends MainActivity
        implements View.OnClickListener, DialogInterface.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);

        findViewById(R.id.button_new_pr_win).setOnClickListener(this);
        findViewById(R.id.button_new_win).setOnClickListener(this);
        findViewById(R.id.button_new_tab).setOnClickListener(this);
        findViewById(R.id.button_close_tab).setOnClickListener(this);
        findViewById(R.id.button_shutdown).setOnClickListener(this);
        findViewById(R.id.button_searchbar).setOnClickListener(this);
        findViewById(R.id.button_mute).setOnClickListener(this);
        findViewById(R.id.button_close_win).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_new_pr_win:
                super.sendCommand(Command.TYPE_HOTKEY, Command.HK_NEW_PR_WIN);
                break;
            case R.id.button_new_win:
                super.sendCommand(Command.TYPE_HOTKEY, Command.HK_NEW_WIN);
                break;
            case R.id.button_new_tab:
                super.sendCommand(Command.TYPE_HOTKEY, Command.HK_NEW_TAB);
                break;
            case R.id.button_close_tab:
                super.sendCommand(Command.TYPE_HOTKEY, Command.HK_CLOSE_TAB);
                break;
            case R.id.button_shutdown:
                boolean doShutdown;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("OK", this);
                builder.setNegativeButton("Cancel", this);
                builder.setMessage("Are you sure?")
                        .setTitle("Server PC shutdown");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.button_searchbar:
                super.sendCommand(Command.TYPE_HOTKEY, Command.HK_SEARCHBAR);
                break;
            case R.id.button_mute:
                super.sendCommand(Command.TYPE_HOTKEY, Command.HK_MUTE);
                break;
            case R.id.button_close_win:
                super.sendCommand(Command.TYPE_HOTKEY, Command.HK_CLOSE_WIN);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE)
            super.sendCommand(Command.TYPE_HOTKEY, Command.HK_SHUTDOWN);
    }
}
