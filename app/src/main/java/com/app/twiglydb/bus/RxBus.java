package com.app.twiglydb.bus;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by abhishek on 29-06-2016.
 */
// use rxbus only for async calls or service
// if there is only 1 subscriber just use intents or listeners
public class RxBus {

    private static final RxBus INSTANCE = new RxBus();

    public static RxBus getInstance() {
        return INSTANCE;
    }

    private final Subject<Object, Object> bus = new SerializedSubject<>(BehaviorSubject.create()); // for sticky events
    //private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create()); --> pub sub when subscriber exists
    // Pass any event down to event listeners.
    public void post(Object o) {
        bus.onNext(o);
    }

    // Subscribe to this event
    public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
        return bus
                .filter(event -> event.getClass().equals(eventClass))
                .map(obj -> (T) obj)
                .subscribe(onNext);

    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
