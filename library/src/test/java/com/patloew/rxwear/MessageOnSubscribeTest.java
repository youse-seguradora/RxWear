package com.patloew.rxwear;

import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class, BaseRx.class })
public class MessageOnSubscribeTest extends BaseOnSubscribeTest {

    @Mock Uri uri;

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

    // MessageListenerObservable

    @Test
    public void MessageListenerObservable_Success() {
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        TestObserver<MessageEvent> sub = Observable.create(observable).test();

        sub.assertNotTerminated();
        sub.assertNoValues();
    }

    @Test
    public void MessageListenerObservable_StatusException() {
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertError(Observable.create(observable).test(), StatusException.class);
    }

    @Test
    public void MessageListenerObservable_Uri_Success() {
        int filterType = 0;
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        TestObserver<MessageEvent> sub = Observable.create(observable).test();

        sub.assertNotTerminated();
        sub.assertNoValues();
    }

    @Test
    public void MessageListenerObservable_Uri_StatusException() {
        int filterType = 0;
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertError(Observable.create(observable).test(), StatusException.class);
    }

    // MessageSendSingle

    @Test
    public void MessageSendSingle_Success() {
        MessageApi.SendMessageResult result = Mockito.mock(MessageApi.SendMessageResult.class);
        String nodeId = "nodeId";
        String path = "path";
        byte[] data = new byte[] {};
        MessageSendSingle single = PowerMockito.spy(new MessageSendSingle(rxWear, nodeId, path, data, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getRequestId()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(messageApi.sendMessage(apiClient, nodeId, path, data)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertSingleValue(Single.create(single).test(), 1);
    }

    @Test
    public void MessageSendSingle_StatusException() {
        MessageApi.SendMessageResult result = Mockito.mock(MessageApi.SendMessageResult.class);
        String nodeId = "nodeId";
        String path = "path";
        byte[] data = new byte[] {};
        MessageSendSingle single = PowerMockito.spy(new MessageSendSingle(rxWear, nodeId, path, data, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getRequestId()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(messageApi.sendMessage(apiClient, nodeId, path, data)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertError(Single.create(single).test(), StatusException.class);
    }

}
