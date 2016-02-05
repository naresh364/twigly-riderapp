package com.app.twiglydb.bus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by naresh on 05/02/16.
 */
public class EventReceiver extends BroadcastReceiver{
    EventCallback eventCallback;
    public EventReceiver(EventCallback eventCallback){
        super();
        this.eventCallback= eventCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getExtras().getString("data");
        eventCallback.callback(message);
    }
}
