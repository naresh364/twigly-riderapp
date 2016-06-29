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

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                                    @Override
                                    public Response intercept(Chain chain) throws IOException {
                                        Request request = chain.request();
                                        String uuid = DeliveryBoy.getInstance().getDev_id();
                                        if (uuid != null) {
                                            request = request
                                                    .newBuilder()
                                                    .addHeader("EUID", uuid)
                                                    .build();
                                        }
                                        Response response = chain.proceed(request);
                                        return response;
                                    }
                                }
                ).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TwiglyRestAPI.TWIGLYAPI_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okClient)
                .build();
        return retrofit.create(TwiglyRestAPI.class);
    }
}


