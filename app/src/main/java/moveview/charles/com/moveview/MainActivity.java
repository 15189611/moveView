package moveview.charles.com.moveview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    GifImageView gifImageView;

    private int statusBarHeight;
    private int mScreenWidth;
    private int mScreenHeight;

    private int lastX;
    private int lastY;


    private int mLastX;
    private int mLastY;
    private float mTouchX;
    private float mTouchY;
    private int left;
    private int right;
    private int top;
    private int bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gifImageView = (GifImageView) findViewById(R.id.gifImage);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels - (getStatusBarHeight());

        gifImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取到手指处的横坐标和纵坐标
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
/*                        lastX = x;
                        lastY = y;*/
                        // 相对于屏幕的坐标点
                        mLastX = (int) event.getRawX();
                        mLastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算移动的距离
                        int offX = (int) (event.getRawX() - mLastX);
                        int offY = (int) (event.getRawY() - mLastY);
                   /*     int offX = x - lastX;
                        int offY = y - lastY;*/

                        left = v.getLeft() + offX;
                        right = v.getRight() + offX;
                        top = v.getTop() + offY;
                        bottom = v.getBottom() + offY;
                        // 下面判断移动是否超出屏幕
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }

                        if (right > mScreenWidth) {
                            right = mScreenWidth;
                            left = right - v.getWidth();
                        }

                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > mScreenHeight) {
                            bottom = mScreenHeight;
                            top = bottom - v.getHeight();
                        }
                        //调用layout方法来重新放置它的位置
                        v.layout(left, top, right, bottom);
                        mLastX = (int) event.getRawX();
                        mLastY = (int) event.getRawY();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 获取状态栏的高度
     */
    private int getStatusBarHeight() {

        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            int height = Integer.parseInt(c.getField("status_bar_height")
                    .get(o).toString());
            return getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
