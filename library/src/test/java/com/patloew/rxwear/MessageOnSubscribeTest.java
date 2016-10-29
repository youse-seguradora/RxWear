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

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

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
        TestSubscriber<MessageEvent> sub = new TestSubscriber<>();
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void MessageListenerObservable_StatusException() {
        TestSubscriber<MessageEvent> sub = new TestSubscriber<>();
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void MessageListenerObservable_Uri_Success() {
        TestSubscriber<MessageEvent> sub = new TestSubscriber<>();
        int filterType = 0;
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void MessageListenerObservable_Uri_StatusException() {
        TestSubscriber<MessageEvent> sub = new TestSubscriber<>();
        int filterType = 0;
        MessageListenerObservable observable = PowerMockito.spy(new MessageListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // MessageSendSingle

    @Test
    public void MessageSendSingle_Success() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
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
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, 1);
    }

    @Test
    public void MessageSendSingle_StatusException() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
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
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

}
