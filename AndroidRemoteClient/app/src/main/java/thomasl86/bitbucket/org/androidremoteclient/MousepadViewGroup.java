package thomasl86.bitbucket.org.androidremoteclient;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by thomas on 03.01.16.
 */
public class MousepadViewGroup extends ViewGroup {


    /* Members */

    private int mTouchSlop      = 0;
    private Point mPtTouchInit          = new Point(0, 0);
    private double mMouseFact           = 3;
    private double mScrollFact          = 10;
    private Context mContext            = null;
    private boolean mIsInitMove         = false;
    private float mPosXPre              = 0;
    private float mPosYPre              = 0;
    private long mTimeMsPre             = 0;
    private MouseEventListener mMouseEventListener;


    /* Constructors */

    public MousepadViewGroup(Context context){
        super(context);
        mContext = context;
    }

    public MousepadViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MousepadViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }


    /* Listener Interfaces */
    public interface MouseEventListener {
        void onMotion(int[] iCommand, int pointerCount);
        void onDown(int pointerCount);
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

        int pointerCount = event.getPointerCount();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mMouseEventListener != null) {
                    mMouseEventListener.onDown(pointerCount);
                }
                mIsInitMove = true;
                return true;
            case MotionEvent.ACTION_UP:

                return false;
            case MotionEvent.ACTION_MOVE:
                if (mIsInitMove) {
                    mPtTouchInit = new Point((int) event.getX(), (int) event.getY());
                    mPosXPre = mPtTouchInit.x;
                    mPosYPre = mPtTouchInit.y;
                }
                mIsInitMove = false;
                int iPosX = 0;
                int iPosY = 0;
                if (pointerCount == 2){
                    double dt = ((double)(System.currentTimeMillis() - mTimeMsPre))/1000;
                    mTimeMsPre = System.currentTimeMillis();
                    iPosX = (int) ((event.getX() - mPosXPre)/dt * mScrollFact);
                    iPosY = (int) ((event.getY() - mPosYPre)/dt * mScrollFact);
                    mPosXPre = event.getX();
                    mPosYPre = event.getY();
                }
                else if (pointerCount == 1){
                    iPosX = (int) ((event.getX() - mPtTouchInit.x) * mMouseFact);
                    iPosY = (int) ((event.getY() - mPtTouchInit.y) * mMouseFact);
                }
                int[] iPos = {iPosX, iPosY};
                if (mMouseEventListener != null) {
                    mMouseEventListener.onMotion(iPos, pointerCount);
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
