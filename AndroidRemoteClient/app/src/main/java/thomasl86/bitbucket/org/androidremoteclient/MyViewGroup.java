package thomasl86.bitbucket.org.androidremoteclient;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.jar.Attributes;

/**
 * Created by thomas on 03.01.16.
 */
public class MyViewGroup extends ViewGroup {


    /* Members */

    private int mTouchSlop      = 0;
    Point mPtTouchInit          = new Point(0, 0);
    double mMouseFact           = 3;
    Context mContext            = null;
    private MouseEventListener mMouseEventListener;
    boolean mIsInitMove         = false;


    /* Constructors */

    public MyViewGroup(Context context){
        super(context);
        mContext = context;
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }


    /* Listener Interfaces */
    public interface MouseEventListener {
        void onMotion(int[] iCommand);
        void onDown();
    }


    /* Members */

    public void setMouseEventListener(MouseEventListener mouseEventListener){
        this.mMouseEventListener = mouseEventListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch(action){
            case MotionEvent.ACTION_DOWN:
                //int[] iPosDown = {(int) event.getX()-mPtTouchInit.x, (int) event.getY()-mPtTouchInit.y};
                if (mMouseEventListener != null) {
                    mMouseEventListener.onDown();
                }
                mIsInitMove = true;
                return true;
            case MotionEvent.ACTION_UP:

                return false;
            case MotionEvent.ACTION_MOVE:
                if(mIsInitMove){
                    mPtTouchInit = new Point((int)event.getX(), (int)event.getY());
                }
                mIsInitMove = false;
                int iPosX = (int)((event.getX()-mPtTouchInit.x)*mMouseFact);
                int iPosY = (int)((event.getY()-mPtTouchInit.y)*mMouseFact);
                int[] iPos = { iPosX, iPosY };
                if (mMouseEventListener != null) {
                    mMouseEventListener.onMotion(iPos);
                }

                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void error(String stError, int length){
        Toast.makeText(mContext, "ERROR: " + stError, length).show();
    }

    public void info(String stError, int length){
        Toast.makeText(mContext, "INFO: " + stError, length).show();
    }
}
