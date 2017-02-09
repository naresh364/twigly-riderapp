package com.app.twiglydb.network;

import android.util.Log;

import com.app.twiglydb.models.DeliveryBoy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
            Response response = chain.proceed(request);
            int tryCount = 0;
            while (!response.isSuccessful() && tryCount < 3) {
                Log.d("intercept", "Request is not successful - " + tryCount);
                tryCount++;
                response = chain.proceed(request);
            }
            return response;
        }).build();

        //Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TwiglyRestAPI.TWIGLYAPI_ENDPOINT)
                //.addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okClient)
                .build();
        return retrofit.create(TwiglyRestAPI.class);
    }
}


