package atritechnocrat.com.locationchatapplication.webservice;

import java.util.concurrent.TimeUnit;

import atritechnocrat.com.locationchatapplication.webservice.apis.ChatApis;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DM on 12-01-2017.
 */

public class Api {

    private Api() {
    }

    ;

    private static Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        client.connectTimeoutMillis();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.ROOTPATH)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }


    public static ChatApis chatApis() {
        return getRetrofit().create(ChatApis.class);
    }

}
