package com.patloew.rxwear;

import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Wearable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class })
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
public class BaseObservableTest extends BaseOnSubscribeTest {

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

    @Test
    public void BaseObservable_ApiClient_Connected() {
        final Object object = new Object();
        BaseObservable<Object> observable = spy(new BaseObservable<Object>(rxWear, null, null) {
            @Override
            protected void onGoogleApiClientReady(GoogleApiClient apiClient, ObservableEmitter<Object> emitter) {
                emitter.onNext(object);
                emitter.onComplete();
            }
        });

        doAnswer(invocation -> {
            BaseRx.ApiClientConnectionCallbacks callbacks = invocation.getArgumentAt(0, BaseRx.ApiClientConnectionCallbacks.class);
            callbacks.setClient(apiClient);
            callbacks.onConnected(null);
            return apiClient;
        }).when(observable).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));

        TestObserver<Object> sub = Observable.create(observable).test();

        sub.assertValue(object);
        sub.assertComplete();
    }

    @Test
    public void BaseObservable_ApiClient_ConnectionSuspended() {
        final Object object = new Object();

        BaseObservable<Object> observable = spy(new BaseObservable<Object>(rxWear, null, null) {
            @Override
            protected void onGoogleApiClientReady(GoogleApiClient apiClient, ObservableEmitter<? super Object> emitter) {
                emitter.onNext(object);
                emitter.onComplete();
            }
        });

        doAnswer(invocation -> {
            BaseRx.ApiClientConnectionCallbacks callbacks = invocation.getArgumentAt(0, BaseRx.ApiClientConnectionCallbacks.class);
            callbacks.setClient(apiClient);
            callbacks.onConnectionSuspended(0);
            return apiClient;
        }).when(observable).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));

        TestObserver<Object> sub = Observable.create(observable).test();

        sub.assertNoValues();
        sub.assertError(GoogleAPIConnectionSuspendedException.class);
    }
}
