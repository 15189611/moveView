package kotlindemo.charles.com.otherapp;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ActivityUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import kotlindemo.charles.com.otherapp.api.MyTestApi;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private MyBroadcastReceiver myBroadcastReceiver;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initReceiver();
        setContentView(R.layout.activity_main);
        animationView = findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.city);
        animationView.playAnimation();

        //在最近任务中 显示空白，不泄漏当前页面的信息
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                throw new NullPointerException("action is null");
            }

            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (reason != null) {
                    if (reason.equals("homekey")) {
                        Log.e("Charles2", "home键");
                    } else if (reason.equals("recentapps")) {
                        Log.e("Charles2", "recentapps");
                        recent();
                    } else if (reason.equals("assist")) {
                        Log.e("Charles2", "常按home键");
                    }
                }
            }
        }
    }

    private void recent() {
        Bitmap bitmap = getonCut();
        if (bitmap == null) {
            Log.e("Charles2", "bitmap is null");
            return;
        }

        Activity topActivity = ActivityUtils.getTopActivity();
        Log.e("Charles2", "Activity==" + topActivity);
        ImageView imageView = new ImageView(topActivity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setImageResource(R.mipmap.ic_launcher);
        ViewGroup rootView = (ViewGroup) topActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        imageView.setVisibility(View.VISIBLE);
        rootView.addView(imageView, layoutParams);
    }

    private Activity getTopActivity() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            if (activities == null) return null;
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getonCut() {
        View decorView = getWindow().getDecorView();
        decorView.buildDrawingCache();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        int stateBarHeight = rect.top;
        Log.e("Charles2", "stateBarHeight is " + stateBarHeight);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        Log.e("Charles2", "width is " + width);
        Log.e("Charles2", "height is " + height);

        decorView.setDrawingCacheEnabled(true);
        decorView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.UNSPECIFIED);
        decorView.layout(0, 0, decorView.getWidth(), decorView.getHeight());
        decorView.buildDrawingCache();
        Bitmap bitmap = decorView.getDrawingCache();
        decorView.destroyDrawingCache();
        return bitmap;
    }

    public void mClick(View view) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new MyNetWorkIntercept())
                .build();

        Request requestBuild = new Request.Builder()
                .url("https://blog.piasy.com/2016/07/11/Understand-OkHttp/")
                .method("GET", null)
                .build();

        //   Response response = httpClient.newCall(requestBuild).execute(); //同步
        httpClient.newCall(requestBuild).enqueue(new Callback() {  //异步
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("Charles2", "==" + response.message());
            }
        });


        //2  Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.baidu.com")
                .callFactory(httpClient)  //设置自己的okHttp对象
                .addConverterFactory(GsonConverterFactory.create())  //Gson解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //返回对象设置
                .build();


        MyTestApi myTestApi = retrofit.create(MyTestApi.class);

        Call call = myTestApi.add(3, 5);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }
}
