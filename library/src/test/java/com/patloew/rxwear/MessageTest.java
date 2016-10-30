package com.patloew.rxwear;

import android.net.Uri;

import com.google.android.gms.wearable.PutDataRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Single;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, Single.class })
public class MessageTest extends BaseTest {

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
    public void Message_Listen() throws Exception {
        ArgumentCaptor<MessageListenerObservable> captor = ArgumentCaptor.forClass(MessageListenerObservable.class);

        rxWear.message().listen();
        rxWear.message().listen(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        MessageListenerObservable single = captor.getAllValues().get(0);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Message_Listen_UriFilterType() throws Exception {
        ArgumentCaptor<MessageListenerObservable> captor = ArgumentCaptor.forClass(MessageListenerObservable.class);

        int filterType = 123;
        rxWear.message().listen(uri, filterType);
        rxWear.message().listen(uri, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        MessageListenerObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Message_Listen_PathFilterType() throws Exception {
        ArgumentCaptor<MessageListenerObservable> captor = ArgumentCaptor.forClass(MessageListenerObservable.class);

        String path = "path";
        int filterType = 123;

        Uri.Builder uriBuilder = Mockito.mock(Uri.Builder.class);
        Message message = spy(rxWear.message());
        doReturn(uriBuilder).when(message).getUriBuilder();
        doReturn(uriBuilder).when(uriBuilder).scheme(PutDataRequest.WEAR_URI_SCHEME);
        doReturn(uriBuilder).when(uriBuilder).path(path);
        doReturn(uri).when(uriBuilder).build();

        message.listen(path, filterType);
        message.listen(path, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        MessageListenerObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertTimeoutSet(single);
    }

    // Send

    @Test
    public void Message_Send() throws Exception {
        ArgumentCaptor<MessageSendSingle> captor = ArgumentCaptor.forClass(MessageSendSingle.class);

        String nodeId = "nodeId";
        String path = "path";
        byte[] data = new byte[] {};
        rxWear.message().send(nodeId, path, data);
        rxWear.message().send(nodeId, path, data, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        MessageSendSingle single = captor.getAllValues().get(0);
        assertEquals(nodeId, single.nodeId);
        assertEquals(path, single.path);
        assertEquals(data, single.data);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(nodeId, single.nodeId);
        assertEquals(path, single.path);
        assertEquals(data, single.data);
        assertTimeoutSet(single);
    }

}
