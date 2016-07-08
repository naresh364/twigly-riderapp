package com.app.twiglydb.network;

import com.app.twiglydb.models.DeliveryBoy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * Created by abhishek on 28-06-2016.
 */
public class TwiglyRestAPIBuilder {
    public static TwiglyRestAPI buildRetroService() {

        OkHttpClient okClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();
            String uuid = DeliveryBoy.getInstance().getDev_id();
            if (uuid != null) {
                request = request
                        .newBuilder()
                        .addHeader("EUID", uuid)
                        .build();
            }
            return chain.proceed(request);
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TwiglyRestAPI.TWIGLYAPI_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okClient)
                .build();
        return retrofit.create(TwiglyRestAPI.class);
    }
}


