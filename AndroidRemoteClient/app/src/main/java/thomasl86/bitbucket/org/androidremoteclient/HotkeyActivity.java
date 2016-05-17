package thomasl86.bitbucket.org.androidremoteclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by thomas on 06.05.16.
 */
public class HotkeyActivity extends Activity
        implements View.OnClickListener, DialogInterface.OnClickListener
{


    /* Members */

    public static final String STR_COMM_TYPE    = "command_type";
    public static final String STR_COMMAND      = "command";


    /* Methods */

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
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.button_new_pr_win:
                intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
                intent.putExtra(STR_COMMAND, Command.HK_NEW_PR_WIN);
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.button_new_win:
                intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
                intent.putExtra(STR_COMMAND, Command.HK_NEW_WIN);
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.button_new_tab:
                intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
                intent.putExtra(STR_COMMAND, Command.HK_NEW_TAB);
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.button_close_tab:
                intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
                intent.putExtra(STR_COMMAND, Command.HK_CLOSE_TAB);
                setResult(Activity.RESULT_OK, intent);
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
                intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
                intent.putExtra(STR_COMMAND, Command.HK_SEARCHBAR);
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.button_mute:
                intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
                intent.putExtra(STR_COMMAND, Command.HK_MUTE);
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.button_close_win:
                intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
                intent.putExtra(STR_COMMAND, Command.HK_CLOSE_WIN);
                setResult(Activity.RESULT_OK, intent);
                break;
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent();
        if (which == DialogInterface.BUTTON_POSITIVE) {
            intent.putExtra(STR_COMM_TYPE, Command.TYPE_HOTKEY);
            intent.putExtra(STR_COMMAND, Command.HK_SHUTDOWN);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }
}
