package com.patloew.rxwear;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Single;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, Single.class })
public class ChannelTest extends BaseTest {

    @Mock com.google.android.gms.wearable.Channel channel;
    @Mock Uri uri;

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.spy(Single.class);
        PowerMockito.mockStatic(Observable.class);
        super.setup();
    }
    
    // Listen

    @Test
    public void Channel_Listen() throws Exception {
        ArgumentCaptor<ChannelListenerObservable> captor = ArgumentCaptor.forClass(ChannelListenerObservable.class);

        rxWear.channel().listen();
        rxWear.channel().listen(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        ChannelListenerObservable single = captor.getAllValues().get(0);
        assertNull(single.channel);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertNull(single.channel);
        assertTimeoutSet(single);
    }

    @Test
    public void Channel_Listen_Channel() throws Exception {
        ArgumentCaptor<ChannelListenerObservable> captor = ArgumentCaptor.forClass(ChannelListenerObservable.class);

        rxWear.channel().listen(channel);
        rxWear.channel().listen(channel, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        ChannelListenerObservable single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertTimeoutSet(single);
    }

    // Close

    @Test
    public void Channel_Close() throws Exception {
        ArgumentCaptor<ChannelCloseSingle> captor = ArgumentCaptor.forClass(ChannelCloseSingle.class);

        rxWear.channel().close(channel);
        rxWear.channel().close(channel, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelCloseSingle single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertNull(single.errorCode);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertNull(single.errorCode);
        assertTimeoutSet(single);
    }

    @Test
    public void Channel_Close_ErrorCode() throws Exception {
        ArgumentCaptor<ChannelCloseSingle> captor = ArgumentCaptor.forClass(ChannelCloseSingle.class);

        int errorCode = 123;
        rxWear.channel().close(channel, errorCode);
        rxWear.channel().close(channel, errorCode, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelCloseSingle single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertEquals(errorCode, (int) single.errorCode);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertEquals(errorCode, (int) single.errorCode);
        assertTimeoutSet(single);
    }

    // Send File

    @Test
    public void Channel_SendFile() throws Exception {
        ArgumentCaptor<ChannelSendFileSingle> captor = ArgumentCaptor.forClass(ChannelSendFileSingle.class);

        rxWear.channel().sendFile(channel, uri);
        rxWear.channel().sendFile(channel, uri, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelSendFileSingle single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertEquals(uri, single.uri);
        assertNull(single.length);
        assertNull(single.startOffset);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertEquals(uri, single.uri);
        assertNull(single.length);
        assertNull(single.startOffset);
        assertTimeoutSet(single);
    }

    @Test
    public void Channel_SendFile_LengthStartOffset() throws Exception {
        ArgumentCaptor<ChannelSendFileSingle> captor = ArgumentCaptor.forClass(ChannelSendFileSingle.class);

        long startOffset = 234L;
        long length = 123L;
        rxWear.channel().sendFile(channel, uri, startOffset, length);
        rxWear.channel().sendFile(channel, uri, startOffset, length, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelSendFileSingle single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertEquals(uri, single.uri);
        assertEquals(startOffset, (long) single.startOffset);
        assertEquals(length, (long) single.length);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertEquals(uri, single.uri);
        assertEquals(startOffset, (long) single.startOffset);
        assertEquals(length, (long) single.length);
        assertTimeoutSet(single);
    }

    // Send File

    @Test
    public void Channel_ReceiveFile() throws Exception {
        ArgumentCaptor<ChannelReceiveFileSingle> captor = ArgumentCaptor.forClass(ChannelReceiveFileSingle.class);

        boolean append = true;
        rxWear.channel().receiveFile(channel, uri, append);
        rxWear.channel().receiveFile(channel, uri, append, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelReceiveFileSingle single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertEquals(uri, single.uri);
        assertEquals(append, single.append);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertEquals(uri, single.uri);
        assertEquals(append, single.append);
        assertTimeoutSet(single);
    }

    // GetInputStream

    @Test
    public void Channel_GetInputStream() throws Exception {
        ArgumentCaptor<ChannelGetInputStreamSingle> captor = ArgumentCaptor.forClass(ChannelGetInputStreamSingle.class);

        rxWear.channel().getInputStream(channel);
        rxWear.channel().getInputStream(channel, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelGetInputStreamSingle single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertTimeoutSet(single);
    }

    // GetOutputStream

    @Test
    public void Channel_GetOutputStream() throws Exception {
        ArgumentCaptor<ChannelGetOutputStreamSingle> captor = ArgumentCaptor.forClass(ChannelGetOutputStreamSingle.class);

        rxWear.channel().getOutputStream(channel);
        rxWear.channel().getOutputStream(channel, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelGetOutputStreamSingle single = captor.getAllValues().get(0);
        assertEquals(channel, single.channel);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(channel, single.channel);
        assertTimeoutSet(single);
    }

    // Open

    @Test
    public void Channel_Open() throws Exception {
        ArgumentCaptor<ChannelOpenSingle> captor = ArgumentCaptor.forClass(ChannelOpenSingle.class);

        String nodeId = "nodeId";
        String path = "path";
        rxWear.channel().open(nodeId, path);
        rxWear.channel().open(nodeId, path, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        ChannelOpenSingle single = captor.getAllValues().get(0);
        assertEquals(nodeId, single.nodeId);
        assertEquals(path, single.path);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(nodeId, single.nodeId);
        assertEquals(path, single.path);
        assertTimeoutSet(single);
    }
}
