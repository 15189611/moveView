package kotlindemo.charles.com.otherapp;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by 17111980 on 2018/7/9.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
