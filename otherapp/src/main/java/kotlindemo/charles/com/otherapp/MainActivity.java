package kotlindemo.charles.com.otherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                .baseUrl("")
                .build();
    }
}
