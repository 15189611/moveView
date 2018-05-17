package kotlindemo.charles.com.otherapp;

import android.util.Log;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 17111980 on 2018/4/17.
 */

public class MyNetWorkIntercept implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Headers headers = request.headers();
        String s = headers.toString();
        Log.e("Charles2", "s==" + s);
        HttpUrl url = request.url();
        Log.e("Charles2", "url==" + url.toString());

        String s1 = url.encodedPath();
        Log.e("Charles2", "加密==" + s1);


        Request.Builder lastRequest = request.newBuilder();
        lastRequest.url("https://www.mssui.com/getcache?id=1&from=0.0.0&to=0.0.1");
        Response proceed = chain.proceed(lastRequest.build());

        return proceed;
    }

}
