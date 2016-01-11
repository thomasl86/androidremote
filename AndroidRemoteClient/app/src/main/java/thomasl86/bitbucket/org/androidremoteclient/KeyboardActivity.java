package thomasl86.bitbucket.org.androidremoteclient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by thomas on 10.01.16.
 */
public class KeyboardActivity extends MainActivity
        implements View.OnClickListener {


    /* Members */

    private KeyboardEventListener mKeyboardEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        /* Register buttons & checkboxes */
        /*CheckBox chboxCtrl = (CheckBox) findViewById(R.id.checkBox_ctrl);
        chboxCtrl.setOnCheckedChangeListener(this);
        CheckBox chboxAlt = (CheckBox) findViewById(R.id.checkBox_alt);
        chboxAlt.setOnCheckedChangeListener(this);
        CheckBox chboxShift = (CheckBox) findViewById(R.id.checkBox_shift);
        chboxShift.setOnCheckedChangeListener(this);*/
        Button buttonUp = (Button) findViewById(R.id.button_up);
        buttonUp.setOnClickListener(this);
        Button buttonDown = (Button) findViewById(R.id.button_down);
        buttonDown.setOnClickListener(this);
        Button buttonLeft = (Button) findViewById(R.id.button_left);
        buttonLeft.setOnClickListener(this);
        Button buttonRight = (Button) findViewById(R.id.button_right);
        buttonRight.setOnClickListener(this);
        /*Button buttonHome = (Button) findViewById(R.id.button_home);
        buttonHome.setOnClickListener(this);
        Button buttonEnd = (Button) findViewById(R.id.button_end);
        buttonEnd.setOnClickListener(this);*/
        Button buttonKeyboard = (Button) findViewById(R.id.button_keyboard);
        buttonKeyboard.setOnClickListener(this);

    }

    public void setKeyboardEventListener(KeyboardEventListener keyboardEventListener){
        mKeyboardEventListener = keyboardEventListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_up:
                super.onKeyDown(Command.KB_UP, null);
                break;
            case R.id.button_down:
                super.onKeyDown(Command.KB_DOWN, null);
                break;
            case R.id.button_left:
                super.onKeyDown(Command.KB_LEFT, null);
                break;
            case R.id.button_right:
                super.onKeyDown(Command.KB_RIGHT, null);
                break;
            case R.id.button_keyboard:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                break;
        }
    }
/*
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.checkBox_ctrl:
                    if (mKeyboardEventListener != null){
                        mKeyboardEventListener.onKeyboardModifierDown(0x2303);
                    }
                    break;
                case R.id.checkBox_alt:
                    if (mKeyboardEventListener != null){
                        mKeyboardEventListener.onKeyboardModifierDown(Command.KB_MODIFIER_ALT);
                    }
                    break;
                case R.id.checkBox_shift:
                    if (mKeyboardEventListener != null){
                        mKeyboardEventListener.onKeyboardModifierDown(0x21E7);
                    }

                    break;
            }
        }
    }*/
}

/* Listener Interfaces */
interface KeyboardEventListener {
    public void onKeyboardDown(int keycode, KeyEvent event);
    public void onKeyboardModifierDown(int keycode);
}
