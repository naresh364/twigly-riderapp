package com.app.twiglydb.network;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by naresh on 13/01/16.
 */
public class ServerCalls {

    private static ServerCalls instance = null;

    public TwiglyService service;
    //public static final String host = "http://192.168.1.7:9000/";
    //public static final String host = "http://dev2.twigly.in/";
    public static final String host = "http://www.twigly.in/";

    private ServerCalls() {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                                    @Override
                                    public Response intercept(Chain chain) throws IOException {
                                        Request request = chain.request();
                                        //String uuid = "uuid:75bc74bc-2f99-4017-ab52-5b3b1413fef6";//DeliveryBoy.getInstance().getDev_id();
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
                .baseUrl(host)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        service = retrofit.create(TwiglyService.class);
    }

    public static ServerCalls getInstance(){
        if (instance == null) {
            instance = new ServerCalls();
        }
        return instance;
    }

    public interface TwiglyService {
        @GET("/employees/signup")
        Call<DeliveryBoy> signup(@Query("contact") String mob, @Query("deviceId") String devId);

        @GET("/db/orders")
        Call<List<Order>> getOrders();

        @FormUrlEncoded
        @POST("/db/markdone")
        Call<ServerResponse> markDone(@Field("mode") String mode,
                                      @Field("displayId") String orderId,
                                      @Field("lat") double lat,
                                      @Field("lng") double lng,
                                      @Field("acc") double acc);

        @FormUrlEncoded
        @POST("/db/updatedeviceinfo")
        Call<ServerResponse> updateDeviceInfo(@Field("lat") double lat,
                                               @Field("lng") double lng,
                                               @Field("acc") double accuracy,
                                               @Field("battery") double battery);

        @GET("/db/reached")
        Call<ServerResponse> reachedDestination(@Query("displayId") String orderId);

        @GET("/db/updategcm")
        Call<ServerResponse> updateGCM(@Query("gcmid") String gcmId);

    }

    public class ServerResponse{
        @SerializedName("serverResponse")
        @Expose
        public String code;
        @SerializedName("message")
        @Expose
        public String message;
    }

    public interface ServerCallEndCallback {
        public void callback();
    }
}
