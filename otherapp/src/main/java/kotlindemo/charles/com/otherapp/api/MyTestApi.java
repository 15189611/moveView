package kotlindemo.charles.com.otherapp.api;


import java.util.List;

import okhttp3.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by 17111980 on 2018/5/18.
 */

public interface MyTestApi {
    @GET("/heiheihei")
    Call add(int a, int b);

    @POST("add")
    Observable<List<String>> addUser(@Body String user);

}
