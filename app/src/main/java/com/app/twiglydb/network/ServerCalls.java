package com.app.twiglydb.network;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.models.OrderResponse;
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
                .baseUrl("http://192.168.1.12:9000/")
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(TwiglyService.class);
    }

    public static ServerCalls getInstanse(){
        if (instance == null) {
            instance = new ServerCalls();
        }
        return instance;
    }

    public interface TwiglyService {
        @GET("/employees/signup")
        Call<DeliveryBoy> signup(@Query("contact") String mob, @Query("deviceId") String devId);

        @GET("/employees/db/orders")
        Call<List<Order>> getOrders();

        @FormUrlEncoded
        @POST("/employees/db/markDone")
        Call<ServerResponse> markDone(@Field("mode") String mode,
                                   @Field("orderId") String orderId,
                                   @Field("lat") double lat,
                                   @Field("lng") double lng);

        @FormUrlEncoded
        @POST("/employees/db/updLoc")
        Call<ServerResponse> updateLocation(@Field("lat") double lat,
                                    @Field("lng") double lng);

        @GET("/employees/db/reached")
        Call<ServerResponse> reachedDestination(@Query("orderId") String orderId);

        @GET("/db/updategcm")
        Call<ServerResponse> updateGCM(@Query("gcmid") String gcmId);

//        @GET("/order")
//        Call<OrderResponse> getRecentOrders(@Query("orderId") String orderId,
//                                            @Query("start") int start,
//                                            @Query("numOrders") int numOrders);


    }

    public class ServerResponse{
        @SerializedName("serverResponse")
        @Expose
        public String code;
        @SerializedName("message")
        @Expose
        public String message;
    }
}
