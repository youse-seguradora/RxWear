package com.patloew.rxwear;

import android.app.PendingIntent;
import android.support.annotation.CallSuper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import io.reactivex.ObservableEmitter;
import io.reactivex.SingleEmitter;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

public abstract class BaseOnSubscribeTest extends BaseTest {

    @Mock GoogleApiClient apiClient;
    @Mock Status status;
    @Mock ConnectionResult connectionResult;
    @Mock PendingResult pendingResult;
    @Mock PendingIntent pendingIntent;

    @Mock DataApi dataApi;
    @Mock CapabilityApi capabilityApi;
    @Mock MessageApi messageApi;
    @Mock NodeApi nodeApi;
    @Mock ChannelApi channelApi;

    @CallSuper
    public void setup() throws Exception {
        PowerMockito.mockStatic(Wearable.class);
        Whitebox.setInternalState(Wearable.class, dataApi);
        Whitebox.setInternalState(Wearable.class, capabilityApi);
        Whitebox.setInternalState(Wearable.class, messageApi);
        Whitebox.setInternalState(Wearable.class, nodeApi);
        Whitebox.setInternalState(Wearable.class, channelApi);

        doReturn(status).when(status).getStatus();

        super.setup();
    }

    // Mock GoogleApiClient connection success behaviour
    protected <T> void setupBaseObservableSuccess(final BaseObservable<T> baseObservable) {
        setupBaseObservableSuccess(baseObservable, apiClient);
    }

    // Mock GoogleApiClient connection success behaviour
    protected <T> void setupBaseObservableSuccess(final BaseObservable<T> baseObservable, final GoogleApiClient apiClient) {
        doAnswer(invocation -> {
            final ObservableEmitter<T> subscriber = ((BaseObservable.ApiClientConnectionCallbacks)invocation.getArguments()[0]).emitter;

            doAnswer(invocation1 -> {
                baseObservable.onGoogleApiClientReady(apiClient, subscriber);
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseObservable).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    // Mock GoogleApiClient connection success behaviour
    protected <T> void setupBaseSingleSuccess(final BaseSingle<T> baseSingle) {
        setupBaseSingleSuccess(baseSingle, apiClient);
    }

    // Mock GoogleApiClient connection success behaviour
    protected <T> void setupBaseSingleSuccess(final BaseSingle<T> baseSingle, final GoogleApiClient apiClient) {
        doAnswer(invocation -> {
            final SingleEmitter<T> subscriber = ((BaseSingle.ApiClientConnectionCallbacks)invocation.getArguments()[0]).emitter;

            doAnswer(invocation1 -> {
                baseSingle.onGoogleApiClientReady(apiClient, subscriber);
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseSingle).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    // Mock GoogleApiClient connection error behaviour
    protected <T> void setupBaseObservableError(final BaseObservable<T> baseObservable) {
        doAnswer(invocation -> {
            final ObservableEmitter<T> subscriber = ((BaseObservable.ApiClientConnectionCallbacks)invocation.getArguments()[0]).emitter;

            doAnswer(invocation1 -> {
                subscriber.onError(new GoogleAPIConnectionException("Error connecting to GoogleApiClient.", connectionResult));
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseObservable).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    // Mock GoogleApiClient connection error behaviour
    protected <T> void setupBaseSingleError(final BaseSingle<T> baseSingle) {
        doAnswer(invocation -> {
            final SingleEmitter<T> subscriber = ((BaseSingle.ApiClientConnectionCallbacks)invocation.getArguments()[0]).emitter;

            doAnswer(invocation1 -> {
                subscriber.onError(new GoogleAPIConnectionException("Error connecting to GoogleApiClient.", connectionResult));
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseSingle).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    @SuppressWarnings("unchecked")
    protected void setPendingResultValue(final Result result) {
        doAnswer(invocation -> {
            ((ResultCallback)invocation.getArguments()[0]).onResult(result);
            return null;
        }).when(pendingResult).setResultCallback(Matchers.<ResultCallback>any());
    }

    protected static void assertError(TestObserver sub, Class<? extends Throwable> errorClass) {
        sub.assertError(errorClass);
        sub.assertNoValues();
    }

    @SuppressWarnings("unchecked")
    protected static void assertSingleValue(TestObserver sub, Object value) {
        sub.assertComplete();
        sub.assertValue(value);
    }

    protected static void assertNoValue(TestObserver sub) {
        sub.assertComplete();
        sub.assertNoValues();
    }
}
