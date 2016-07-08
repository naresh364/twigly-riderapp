package com.app.twiglydb.bus;

/**
 * Created by abhishek on 02-07-2016.
 */
public class RxBusProvider {
    private static final RxBus bus = new RxBus();

    public static RxBus instance(){
        return bus;
    }

}
