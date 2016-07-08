package com.app.twiglydb.network;

import com.app.twiglydb.models.Order;

import java.util.List;
import rx.Observable;

/**
 * Created by abhishek on 03-07-2016.
 */
public class TwiglyDataManager {

    private static TwiglyDataManager instance = null;
    private TwiglyDataManager() {
        // Exists only to defeat instantiation.
    }

    // Lazy instantiation: instance created only when needed
    public static TwiglyDataManager getInstance() {
        if(instance == null) {
            instance = new TwiglyDataManager();
        }
        return instance;
    }

    private TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();

//TODO: afadgdgsdg
    public Observable<List<Order>> getOrders() {
        return api.getOrders();
    }


}
