package com.app.twiglydb.network;

import com.app.twiglydb.BuildConfig;
import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.LocationParamas;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.models.Summary;
import com.app.twiglydb.models.Version;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by abhishek on 28-06-2016.
 */
public interface TwiglyRestAPI {

    //String TWIGLYAPI_ENDPOINT = "http://dev2.twigly.in/";
    String TWIGLYAPI_ENDPOINT = (BuildConfig.DEBUG)?"http://dev2.twigly.in/":"https://www.twigly.in/";
    //String TWIGLYAPI_ENDPOINT = "https://www.twigly.in/";


    @GET("/employees/signup")
    Observable<DeliveryBoy> signup(@Query("contact") String mob, @Query("deviceId") String devId);

    @GET("/db/orders")
    Observable<List<Order>> getOrders();

    @GET("/db/dailyorders")
    Observable<List<Order>> getDailyOrders();

    @GET("/db/summary")
    Observable<Summary> getSummary(@Query("dateTo") String from, @Query("dateFrom") String to);

    @FormUrlEncoded
    @POST("/db/markdone")
    Observable<ServerResponse> markDone(@Field("mode") String mode,
                                        @Field("displayId") String orderId,
                                        @Field("lat") double lat,
                                        @Field("lng") double lng,
                                        @Field("acc") double acc,
                                        @Field("bat") double bat,
                                        @Field("time") double time,
                                        @Field("pending_collected") boolean pendingCollected);

    @FormUrlEncoded
    @POST("/db/updatedeviceinfo")
    Observable<ServerResponse> updateDeviceInfo(@Field("lat") double lat,
                                          @Field("lng") double lng,
                                          @Field("acc") double accuracy,
                                          @Field("speed") double speed,
                                          @Field("time") long time,
                                          @Field("battery") double battery);

    @GET("/db/reached")
    Observable<ServerResponse> reachedDestination(@Query("displayId") String orderId);

    @GET("/db/called")
    Observable<ServerResponse> dbCalled(@Query("mobile") String mobileNumber);

    @GET("/db/updategcm")
    Observable<ServerResponse> updateGCM(@Query("gcmid") String gcmId);

    @GET("/db/payment/status")
    Observable<ServerResponse> getPaymentStatus(@Query("orderId") String orderId, @Query("pendings") boolean withPendings);

    class ServerResponse{
        @SerializedName("serverResponse")
        @Expose
        public String code;
        @SerializedName("message")
        @Expose
        public String message;
    }

    @GET("/assets/static/dbapp/dbapp_data")
    Observable<Version> getVersionInfo();

    @GET("/db/location/params")
    Observable<LocationParamas> getLocationParams();
}
