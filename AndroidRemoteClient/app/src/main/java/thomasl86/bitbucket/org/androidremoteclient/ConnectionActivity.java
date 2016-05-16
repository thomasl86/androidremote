package thomasl86.bitbucket.org.androidremoteclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Created by thomas on 13.05.16.
 */
public class ConnectionActivity extends Activity implements View.OnClickListener {


    /* Members */

    public static final String STR_COMM_MODE = "communication mode";


    /* Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_connection);

        findViewById(R.id.button_bluetooth).setOnClickListener(this);
        findViewById(R.id.button_wifi).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.button_bluetooth:
                intent.putExtra(STR_COMM_MODE, MainActivity.COMM_MODE_BT);
                break;
            case R.id.button_wifi:
                intent.putExtra(STR_COMM_MODE, MainActivity.COMM_MODE_WIFI);
                break;
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
