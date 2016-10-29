package com.patloew.rxwear;

import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.Wearable;
import com.patloew.rxwear.events.ChannelEvent;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class, BaseRx.class })
public class ChannelOnSubscribeTest extends BaseOnSubscribeTest {

    @Mock Uri uri;
    @Mock Channel channel;

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

    // ChannelListenerObservable

    @Test
    public void ChannelListenerObservable_Success() {
        TestSubscriber<ChannelEvent> sub = new TestSubscriber<>();
        ChannelListenerObservable observable = PowerMockito.spy(new ChannelListenerObservable(rxWear, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channelApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(ChannelApi.ChannelListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void ChannelListenerObservable_StatusException() {
        TestSubscriber<ChannelEvent> sub = new TestSubscriber<>();
        ChannelListenerObservable observable = PowerMockito.spy(new ChannelListenerObservable(rxWear, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channelApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(ChannelApi.ChannelListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void ChannelListenerObservable_Channel_Success() {
        TestSubscriber<ChannelEvent> sub = new TestSubscriber<>();
        ChannelListenerObservable observable = PowerMockito.spy(new ChannelListenerObservable(rxWear, channel, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(ChannelApi.ChannelListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void ChannelListenerObservable_Channel_StatusException() {
        TestSubscriber<ChannelEvent> sub = new TestSubscriber<>();
        ChannelListenerObservable observable = PowerMockito.spy(new ChannelListenerObservable(rxWear, channel, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(ChannelApi.ChannelListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelCloseSingle

    @Test
    public void ChannelCloseSingle_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseSingle single = PowerMockito.spy(new ChannelCloseSingle(rxWear, channel, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.close(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelCloseSingle_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseSingle single = PowerMockito.spy(new ChannelCloseSingle(rxWear, channel, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.close(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void ChannelCloseSingle_ErrorCode_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseSingle single = PowerMockito.spy(new ChannelCloseSingle(rxWear, channel, 1, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.close(apiClient, 1)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelCloseSingle_ErrorCode_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseSingle single = PowerMockito.spy(new ChannelCloseSingle(rxWear, channel, 1, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.close(apiClient, 1)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelSendFileSingle

    @Test
    public void ChannelSendFileSingle_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileSingle single = PowerMockito.spy(new ChannelSendFileSingle(rxWear, channel, uri, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.sendFile(apiClient, uri)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelSendFileSingle_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileSingle single = PowerMockito.spy(new ChannelSendFileSingle(rxWear, channel, uri, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.sendFile(apiClient, uri)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void ChannelSendFileSingle_OffsetLength_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileSingle single = PowerMockito.spy(new ChannelSendFileSingle(rxWear, channel, uri, 1l, 2l, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.sendFile(apiClient, uri, 1l, 2l)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelSendFileSingle_OffsetLength_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileSingle single = PowerMockito.spy(new ChannelSendFileSingle(rxWear, channel, uri, 1l, 2l, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.sendFile(apiClient, uri, 1l, 2l)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelOpenSingle

    @Test
    public void ChannelOpenSingle_Success() {
        TestSubscriber<com.google.android.gms.wearable.Channel> sub = new TestSubscriber<>();
        ChannelApi.OpenChannelResult result = Mockito.mock(ChannelApi.OpenChannelResult.class);
        String nodeId = "nodeId";
        String path ="path";
        ChannelOpenSingle single = PowerMockito.spy(new ChannelOpenSingle(rxWear, nodeId, path, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getChannel()).thenReturn(channel);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channelApi.openChannel(apiClient, nodeId, path)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, channel);
    }

    @Test
    public void ChannelOpenSingle_NullChannel_IOException() {
        TestSubscriber<com.google.android.gms.wearable.Channel> sub = new TestSubscriber<>();
        ChannelApi.OpenChannelResult result = Mockito.mock(ChannelApi.OpenChannelResult.class);
        String nodeId = "nodeId";
        String path ="path";
        ChannelOpenSingle single = PowerMockito.spy(new ChannelOpenSingle(rxWear, nodeId, path, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getChannel()).thenReturn(null);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channelApi.openChannel(apiClient, nodeId, path)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, IOException.class);
    }

    @Test
    public void ChannelOpenSingle_StatusException() {
        TestSubscriber<com.google.android.gms.wearable.Channel> sub = new TestSubscriber<>();
        ChannelApi.OpenChannelResult result = Mockito.mock(ChannelApi.OpenChannelResult.class);
        String nodeId = "nodeId";
        String path ="path";
        ChannelOpenSingle single = PowerMockito.spy(new ChannelOpenSingle(rxWear, nodeId, path, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getChannel()).thenReturn(channel);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(channelApi.openChannel(apiClient, nodeId, path)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelGetInputStreamObservable

    @Test
    public void ChannelGetInputStreamSingle_Success() {
        TestSubscriber<InputStream> sub = new TestSubscriber<>();
        com.google.android.gms.wearable.Channel.GetInputStreamResult result = Mockito.mock(com.google.android.gms.wearable.Channel.GetInputStreamResult.class);
        InputStream inputStream = Mockito.mock(InputStream.class);
        ChannelGetInputStreamSingle single = PowerMockito.spy(new ChannelGetInputStreamSingle(rxWear, channel, null, null));

        when(result.getInputStream()).thenReturn(inputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channel.getInputStream(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, inputStream);
    }

    @Test
    public void ChannelGetInputStreamSingle_StatusException() {
        TestSubscriber<InputStream> sub = new TestSubscriber<>();
        com.google.android.gms.wearable.Channel.GetInputStreamResult result = Mockito.mock(com.google.android.gms.wearable.Channel.GetInputStreamResult.class);
        InputStream inputStream = Mockito.mock(InputStream.class);
        ChannelGetInputStreamSingle single = PowerMockito.spy(new ChannelGetInputStreamSingle(rxWear, channel, null, null));

        when(result.getInputStream()).thenReturn(inputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(channel.getInputStream(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelGetOutputStreamSingle

    @Test
    public void ChannelGetOutputStreamSingle_Success() {
        TestSubscriber<OutputStream> sub = new TestSubscriber<>();
        com.google.android.gms.wearable.Channel.GetOutputStreamResult result = Mockito.mock(com.google.android.gms.wearable.Channel.GetOutputStreamResult.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        ChannelGetOutputStreamSingle single = PowerMockito.spy(new ChannelGetOutputStreamSingle(rxWear, channel, null, null));

        when(result.getOutputStream()).thenReturn(outputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channel.getOutputStream(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, outputStream);
    }

    @Test
    public void ChannelGetOutputStreamSingle_StatusException() {
        TestSubscriber<OutputStream> sub = new TestSubscriber<>();
        com.google.android.gms.wearable.Channel.GetOutputStreamResult result = Mockito.mock(com.google.android.gms.wearable.Channel.GetOutputStreamResult.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        ChannelGetOutputStreamSingle single = PowerMockito.spy(new ChannelGetOutputStreamSingle(rxWear, channel, null, null));

        when(result.getOutputStream()).thenReturn(outputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(channel.getOutputStream(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelReceiveFileSingle

    @Test
    public void ChannelReceiveFileSingle_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelReceiveFileSingle single = PowerMockito.spy(new ChannelReceiveFileSingle(rxWear, channel, uri, false, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.receiveFile(apiClient, uri, false)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelReceiveFileSingle_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelReceiveFileSingle single = PowerMockito.spy(new ChannelReceiveFileSingle(rxWear, channel, uri, false, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.receiveFile(apiClient, uri, false)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }
}
