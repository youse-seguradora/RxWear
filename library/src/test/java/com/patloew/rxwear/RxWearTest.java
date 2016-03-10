package com.patloew.rxwear;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.patloew.rxwear.events.ChannelEvent;
import com.patloew.rxwear.events.NodeEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class })
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
public class RxWearTest {

    @Mock Context ctx;

    @Mock GoogleApiClient apiClient;
    @Mock Status status;
    @Mock ConnectionResult connectionResult;
    @Mock PendingResult pendingResult;

    @Mock CapabilityInfo capabilityInfo;
    @Mock Uri uri;
    @Mock Channel channel;
    @Mock DataItem dataItem;
    @Mock DataItemBuffer dataItemBuffer;

    @Mock CapabilityApi capabilityApi;
    @Mock ChannelApi channelApi;
    @Mock DataApi dataApi;
    @Mock MessageApi messageApi;
    @Mock NodeApi nodeApi;

    @Mock RxWear rxWear;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(Wearable.class);
        Whitebox.setInternalState(Wearable.class, capabilityApi);
        Whitebox.setInternalState(Wearable.class, channelApi);
        Whitebox.setInternalState(Wearable.class, dataApi);
        Whitebox.setInternalState(Wearable.class, messageApi);
        Whitebox.setInternalState(Wearable.class, nodeApi);

        when(ctx.getApplicationContext()).thenReturn(ctx);
    }

    //////////////////
    // UTIL METHODS //
    //////////////////


    // Mock GoogleApiClient connection success behaviour
    private <T> void setupBaseObservableSuccess(final BaseObservable<T> baseObservable) {
        setupBaseObservableSuccess(baseObservable, apiClient);
    }

    // Mock GoogleApiClient connection success behaviour
    private <T> void setupBaseObservableSuccess(final BaseObservable<T> baseObservable, final GoogleApiClient apiClient) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Subscriber<? super T> subscriber = (Subscriber<? super T>) invocation.getArguments()[0];

                doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        baseObservable.onGoogleApiClientReady(apiClient, subscriber);
                        return null;
                    }
                }).when(apiClient).connect();

                return apiClient;
            }
        }).when(baseObservable).createApiClient(Matchers.<Subscriber<? super T>>any());


    }

    // Mock GoogleApiClient connection error behaviour
    private <T> void setupBaseObservableError(final BaseObservable<T> baseObservable) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Subscriber<? super T> subscriber = (Subscriber<? super T>) invocation.getArguments()[0];

                doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        subscriber.onError(new GoogleAPIConnectionException("Error connecting to GoogleApiClient.", connectionResult));
                        return null;
                    }
                }).when(apiClient).connect();

                return apiClient;
            }
        }).when(baseObservable).createApiClient(Matchers.<Subscriber<? super T>>any());
    }

    @SuppressWarnings("unchecked")
    private void setPendingResultValue(final Result result) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((ResultCallback)invocation.getArguments()[0]).onResult(result);
                return null;
            }
        }).when(pendingResult).setResultCallback(Matchers.<ResultCallback>any());
    }

    private static void assertError(TestSubscriber sub, Class<? extends Throwable> errorClass) {
        sub.assertError(errorClass);
        sub.assertNoValues();
        sub.assertUnsubscribed();
    }

    @SuppressWarnings("unchecked")
    private static void assertSingleValue(TestSubscriber sub, Object value) {
        sub.assertCompleted();
        sub.assertUnsubscribed();
        sub.assertValue(value);
    }

    private static void assertNoValue(TestSubscriber sub) {
        sub.assertCompleted();
        sub.assertUnsubscribed();
        sub.assertNoValues();
    }


    //////////////////////
    // OBSERVABLE TESTS //
    //////////////////////


    // GoogleApiClientObservable

    @Test
    public void GoogleAPIClientObservable_Success() {
        TestSubscriber<GoogleApiClient> sub = new TestSubscriber<>();
        GoogleAPIClientObservable observable = spy(new GoogleAPIClientObservable(ctx, new Api[] {}, new Scope[] {}));

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, apiClient);
    }

    @Test
    public void GoogleAPIClientObservable_ConnectionException() {
        TestSubscriber<GoogleApiClient> sub = new TestSubscriber<>();
        final GoogleAPIClientObservable observable = spy(new GoogleAPIClientObservable(ctx, new Api[] {}, new Scope[] {}));

        setupBaseObservableError(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, GoogleAPIConnectionException.class);
    }

    // CheckConnectionCompletable

    @Test
    public void CheckConnectionCompletable_Success() {
        TestSubscriber<GoogleApiClient> sub = new TestSubscriber<>();
        CheckConnectionObservable observable = spy(new CheckConnectionObservable(rxWear));

        setupBaseObservableSuccess(observable);
        Completable.fromObservable(Observable.create(observable)).subscribe(sub);

        sub.assertCompleted();
        sub.assertNoValues();
    }

    @Test
    public void CheckConnectionCompletable_Error() {
        TestSubscriber<GoogleApiClient> sub = new TestSubscriber<>();
        CheckConnectionObservable observable = spy(new CheckConnectionObservable(rxWear));

        setupBaseObservableError(observable);
        Completable.fromObservable(Observable.create(observable)).subscribe(sub);

        sub.assertError(GoogleAPIConnectionException.class);
        sub.assertNoValues();
    }

    /**************
     * CAPABILITY *
     **************/

    // CapabilityAddListenerObservable

    @Test
    public void CapabilityAddListenerObservable_String_Success() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        String capability = "capability";
        CapabilityListenerObservable observable = spy(new CapabilityListenerObservable(rxWear, capability, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.addCapabilityListener(Matchers.any(GoogleApiClient.class), Matchers.any(CapabilityApi.CapabilityListener.class), Matchers.anyString())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void CapabilityAddListenerObservable_String_StatusException() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        String capability = "capability";
        CapabilityListenerObservable observable = spy(new CapabilityListenerObservable(rxWear, capability, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.addCapabilityListener(Matchers.any(GoogleApiClient.class), Matchers.any(CapabilityApi.CapabilityListener.class), Matchers.anyString())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void CapabilityAddListenerObservable_Uri_Success() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        int flag = 0;
        CapabilityListenerObservable observable = spy(new CapabilityListenerObservable(rxWear, null, uri, flag, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(CapabilityApi.CapabilityListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void CapabilityAddListenerObservable_Uri_StatusException() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        int flag = 0;
        CapabilityListenerObservable observable = spy(new CapabilityListenerObservable(rxWear, null, uri, flag, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(CapabilityApi.CapabilityListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // CapabilityGetAllObservable

    @Test
    public void CapabilityGetAllObservable_Success() {
        TestSubscriber<Map<String, CapabilityInfo>> sub = new TestSubscriber<>();
        Map<String, CapabilityInfo> resultMap = new HashMap<>();
        CapabilityApi.GetAllCapabilitiesResult result = Mockito.mock(CapabilityApi.GetAllCapabilitiesResult.class);
        int nodeFilter = 0;
        CapabilityGetAllObservable observable = spy(new CapabilityGetAllObservable(rxWear, nodeFilter, null, null));

        when(result.getAllCapabilities()).thenReturn(resultMap);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.getAllCapabilities(apiClient, nodeFilter)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, resultMap);
    }

    @Test
    public void CapabilityGetAllObservable_StatusException() {
        TestSubscriber<Map<String, CapabilityInfo>> sub = new TestSubscriber<>();
        Map<String, CapabilityInfo> resultMap = new HashMap<>();
        CapabilityApi.GetAllCapabilitiesResult result = Mockito.mock(CapabilityApi.GetAllCapabilitiesResult.class);
        int nodeFilter = 0;
        CapabilityGetAllObservable observable = spy(new CapabilityGetAllObservable(rxWear, nodeFilter, null, null));

        when(result.getAllCapabilities()).thenReturn(resultMap);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.getAllCapabilities(apiClient, nodeFilter)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // CapabilityGetObservable

    @Test
    public void CapabilityGetObservable_Success() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        CapabilityApi.GetCapabilityResult result = Mockito.mock(CapabilityApi.GetCapabilityResult.class);
        int nodeFilter = 0;
        String capability = "capability";
        CapabilityGetObservable observable = spy(new CapabilityGetObservable(rxWear, capability, nodeFilter, null, null));

        when(result.getCapability()).thenReturn(capabilityInfo);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.getCapability(apiClient, capability, nodeFilter)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, capabilityInfo);
    }

    @Test
    public void CapabilityGetObservable_StatusException() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        CapabilityApi.GetCapabilityResult result = Mockito.mock(CapabilityApi.GetCapabilityResult.class);
        int nodeFilter = 0;
        String capability = "capability";
        CapabilityGetObservable observable = spy(new CapabilityGetObservable(rxWear, capability, nodeFilter, null, null));

        when(result.getCapability()).thenReturn(capabilityInfo);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.getCapability(apiClient, capability, nodeFilter)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // CapabilityAddLocalObservable

    @Test
    public void CapabilityAddLocalObservable_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.AddLocalCapabilityResult result = Mockito.mock(CapabilityApi.AddLocalCapabilityResult.class);
        String capability = "capability";
        CapabilityAddLocalObservable observable = spy(new CapabilityAddLocalObservable(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.addLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void CapabilityAddLocalObservable_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.AddLocalCapabilityResult result = Mockito.mock(CapabilityApi.AddLocalCapabilityResult.class);

        String capability = "capability";
        CapabilityAddLocalObservable observable = spy(new CapabilityAddLocalObservable(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.addLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // CapabilityAddLocalObservable

    @Test
    public void CapabilityRemoveLocalObservable_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.RemoveLocalCapabilityResult result = Mockito.mock(CapabilityApi.RemoveLocalCapabilityResult.class);
        String capability = "capability";
        CapabilityRemoveLocalObservable observable = spy(new CapabilityRemoveLocalObservable(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.removeLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void CapabilityRemoveLocalObservable_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.RemoveLocalCapabilityResult result = Mockito.mock(CapabilityApi.RemoveLocalCapabilityResult.class);
        String capability = "capability";
        CapabilityRemoveLocalObservable observable = spy(new CapabilityRemoveLocalObservable(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.removeLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    /***********
     * CHANNEL *
     ***********/

    // ChannelListenerObservable

    @Test
    public void ChannelListenerObservable_Success() {
        TestSubscriber<ChannelEvent> sub = new TestSubscriber<>();
        ChannelListenerObservable observable = spy(new ChannelListenerObservable(rxWear, null, null, null));

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
        ChannelListenerObservable observable = spy(new ChannelListenerObservable(rxWear, null, null, null));

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
        ChannelListenerObservable observable = spy(new ChannelListenerObservable(rxWear, channel, null, null));

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
        ChannelListenerObservable observable = spy(new ChannelListenerObservable(rxWear, channel, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(ChannelApi.ChannelListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelCloseObservable

    @Test
    public void ChannelCloseObservable_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseObservable observable = spy(new ChannelCloseObservable(rxWear, channel, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.close(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelCloseObservable_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseObservable observable = spy(new ChannelCloseObservable(rxWear, channel, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.close(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void ChannelCloseObservable_ErrorCode_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseObservable observable = spy(new ChannelCloseObservable(rxWear, channel, 1, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.close(apiClient, 1)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelCloseObservable_ErrorCode_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelCloseObservable observable = spy(new ChannelCloseObservable(rxWear, channel, 1, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.close(apiClient, 1)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelSendFileObservable

    @Test
    public void ChannelSendFileObservable_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileObservable observable = spy(new ChannelSendFileObservable(rxWear, channel, uri, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.sendFile(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelSendFileObservable_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileObservable observable = spy(new ChannelSendFileObservable(rxWear, channel, uri, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.sendFile(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void ChannelSendFileObservable_OffsetLength_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileObservable observable = spy(new ChannelSendFileObservable(rxWear, channel, uri, 1l, 2l, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.sendFile(apiClient, uri, 1l, 2l)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelSendFileObservable_OffsetLength_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelSendFileObservable observable = spy(new ChannelSendFileObservable(rxWear, channel, uri, 1l, 2l, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.sendFile(apiClient, uri, 1l, 2l)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelOpenObservable

    @Test
    public void ChannelOpenObservable_Success() {
        TestSubscriber<Channel> sub = new TestSubscriber<>();
        ChannelApi.OpenChannelResult result = Mockito.mock(ChannelApi.OpenChannelResult.class);
        String nodeId = "nodeId";
        String path ="path";
        ChannelOpenObservable observable = spy(new ChannelOpenObservable(rxWear, nodeId, path, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getChannel()).thenReturn(channel);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channelApi.openChannel(apiClient, nodeId, path)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, channel);
    }

    @Test
    public void ChannelOpenObservable_Success_NoChannel() {
        TestSubscriber<Channel> sub = new TestSubscriber<>();
        ChannelApi.OpenChannelResult result = Mockito.mock(ChannelApi.OpenChannelResult.class);
        String nodeId = "nodeId";
        String path ="path";
        ChannelOpenObservable observable = spy(new ChannelOpenObservable(rxWear, nodeId, path, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getChannel()).thenReturn(null);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channelApi.openChannel(apiClient, nodeId, path)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertCompleted();
        sub.assertUnsubscribed();
        sub.assertNoValues();
    }

    @Test
    public void ChannelOpenObservable_StatusException() {
        TestSubscriber<Channel> sub = new TestSubscriber<>();
        ChannelApi.OpenChannelResult result = Mockito.mock(ChannelApi.OpenChannelResult.class);
        String nodeId = "nodeId";
        String path ="path";
        ChannelOpenObservable observable = spy(new ChannelOpenObservable(rxWear, nodeId, path, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getChannel()).thenReturn(channel);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(channelApi.openChannel(apiClient, nodeId, path)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelGetInputStreamObservable

    @Test
    public void ChannelGetInputStreamObservable_Success() {
        TestSubscriber<InputStream> sub = new TestSubscriber<>();
        Channel.GetInputStreamResult result = Mockito.mock(Channel.GetInputStreamResult.class);
        InputStream inputStream = Mockito.mock(InputStream.class);
        ChannelGetInputStreamObservable observable = spy(new ChannelGetInputStreamObservable(rxWear, channel, null, null));

        when(result.getInputStream()).thenReturn(inputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channel.getInputStream(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, inputStream);
    }

    @Test
    public void ChannelGetInputStreamObservable_StatusException() {
        TestSubscriber<InputStream> sub = new TestSubscriber<>();
        Channel.GetInputStreamResult result = Mockito.mock(Channel.GetInputStreamResult.class);
        InputStream inputStream = Mockito.mock(InputStream.class);
        ChannelGetInputStreamObservable observable = spy(new ChannelGetInputStreamObservable(rxWear, channel, null, null));

        when(result.getInputStream()).thenReturn(inputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(channel.getInputStream(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelGetOutputStreamObservable

    @Test
    public void ChannelGetOutputStreamObservable_Success() {
        TestSubscriber<OutputStream> sub = new TestSubscriber<>();
        Channel.GetOutputStreamResult result = Mockito.mock(Channel.GetOutputStreamResult.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        ChannelGetOutputStreamObservable observable = spy(new ChannelGetOutputStreamObservable(rxWear, channel, null, null));

        when(result.getOutputStream()).thenReturn(outputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(channel.getOutputStream(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, outputStream);
    }

    @Test
    public void ChannelGetOutputStreamObservable_StatusException() {
        TestSubscriber<OutputStream> sub = new TestSubscriber<>();
        Channel.GetOutputStreamResult result = Mockito.mock(Channel.GetOutputStreamResult.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        ChannelGetOutputStreamObservable observable = spy(new ChannelGetOutputStreamObservable(rxWear, channel, null, null));

        when(result.getOutputStream()).thenReturn(outputStream);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(channel.getOutputStream(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // ChannelReceiveFileObservable

    @Test
    public void ChannelReceiveFileObservable_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelReceiveFileObservable observable = spy(new ChannelReceiveFileObservable(rxWear, channel, uri, false, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(channel.receiveFile(apiClient, uri, false)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void ChannelReceiveFileObservable_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        ChannelReceiveFileObservable observable = spy(new ChannelReceiveFileObservable(rxWear, channel, uri, false, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(channel.receiveFile(apiClient, uri, false)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    /********
     * DATA *
     ********/

    // DataListenerObservable

    @Test
    public void DataListenerObservable_Success() {
        TestSubscriber<DataEvent> sub = new TestSubscriber<>();
        DataListenerObservable observable = spy(new DataListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void DataListenerObservable_StatusException() {
        TestSubscriber<DataEvent> sub = new TestSubscriber<>();
        DataListenerObservable observable = spy(new DataListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void DataListenerObservable_Uri_Success() {
        TestSubscriber<DataEvent> sub = new TestSubscriber<>();
        int filterType = 0;
        DataListenerObservable observable = spy(new DataListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void DataListenerObservable_Uri_StatusException() {
        TestSubscriber<DataEvent> sub = new TestSubscriber<>();
        int filterType = 0;
        DataListenerObservable observable = spy(new DataListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataDeleteItemsObservable

    @Test
    public void DataDeleteItemsObservable_Success() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        DataDeleteItemsObservable observable = spy(new DataDeleteItemsObservable(rxWear, uri, null, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.deleteDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, 1);
    }

    @Test
    public void DataDeleteItemsObservable_StatusException() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        DataDeleteItemsObservable observable = spy(new DataDeleteItemsObservable(rxWear, uri, null, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.deleteDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void DataDeleteItemsObservable_FilterType_Success() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        int filterType = 0;
        DataDeleteItemsObservable observable = spy(new DataDeleteItemsObservable(rxWear, uri, filterType, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.deleteDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, 1);
    }

    @Test
    public void DataDeleteItemsObservable_FilterType_StatusException() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        int filterType = 0;
        DataDeleteItemsObservable observable = spy(new DataDeleteItemsObservable(rxWear, uri, filterType, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.deleteDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataPutItemObservable

    @Test
    public void DataPutItemObservable_Success() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        DataApi.DataItemResult result = Mockito.mock(DataApi.DataItemResult.class);
        DataPutItemObservable observable = spy(new DataPutItemObservable(rxWear, putDataRequest, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getDataItem()).thenReturn(dataItem);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.putDataItem(apiClient, putDataRequest)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, dataItem);
    }

    @Test
    public void DataPutItemObservable_StatusException() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        DataApi.DataItemResult result = Mockito.mock(DataApi.DataItemResult.class);
        DataPutItemObservable observable = spy(new DataPutItemObservable(rxWear, putDataRequest, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getDataItem()).thenReturn(dataItem);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.putDataItem(apiClient, putDataRequest)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataGetItemsObservable

    @Test
    public void DataGetItemsObservable_Uri_FilterType_Success() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        int filterType = 0;
        DataGetItemsObservable observable = spy(new DataGetItemsObservable(rxWear, uri, filterType, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertNoValue(sub);
    }

    @Test
    public void DataGetItemsObservable_Uri_FilterType_StatusException() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        int filterType = 0;
        DataGetItemsObservable observable = spy(new DataGetItemsObservable(rxWear, uri, filterType, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void DataGetItemsObservable_Uri_Success() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        DataGetItemsObservable observable = spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertNoValue(sub);
    }

    @Test
    public void DataGetItemsObservable_Uri_StatusException() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        DataGetItemsObservable observable = spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void DataGetItemsObservable_Success() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        DataGetItemsObservable observable = spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertNoValue(sub);
    }

    @Test
    public void DataGetItemsObservable_StatusException() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        DataGetItemsObservable observable = spy(new DataGetItemsObservable(rxWear, null, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getDataItems(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataGetFdForAssetObservable

    @Test
    public void DataGetFdForAssetObservable_DataItemAsset_Success() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        DataItemAsset dataItemAsset = Mockito.mock(DataItemAsset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetObservable observable = spy(new DataGetFdForAssetObservable(rxWear, dataItemAsset, null, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getFdForAsset(apiClient, dataItemAsset)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, result);
    }

    @Test
    public void DataGetFdForAssetObservable_DataItemAsset_StatusException() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        DataItemAsset dataItemAsset = Mockito.mock(DataItemAsset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetObservable observable = spy(new DataGetFdForAssetObservable(rxWear, dataItemAsset, null, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getFdForAsset(apiClient, dataItemAsset)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void DataGetFdForAssetObservable_Asset_Success() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        Asset asset = Mockito.mock(Asset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetObservable observable = spy(new DataGetFdForAssetObservable(rxWear, null, asset, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getFdForAsset(apiClient, asset)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, result);
    }

    @Test
    public void DataGetFdForAssetObservable_Asset_StatusException() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        Asset asset = Mockito.mock(Asset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetObservable observable = spy(new DataGetFdForAssetObservable(rxWear, null, asset, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getFdForAsset(apiClient, asset)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }


    /***********
     * MESSAGE *
     ***********/

    // MessageListenerObservable

    @Test
    public void MessageListenerObservable_Success() {
        TestSubscriber<MessageEvent> sub = new TestSubscriber<>();
        MessageListenerObservable observable = spy(new MessageListenerObservable(rxWear, null, null, null, null));

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
        MessageListenerObservable observable = spy(new MessageListenerObservable(rxWear, null, null, null, null));

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
        MessageListenerObservable observable = spy(new MessageListenerObservable(rxWear, uri, filterType, null, null));

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
        MessageListenerObservable observable = spy(new MessageListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(messageApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(MessageApi.MessageListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // MessageSendObservable

    @Test
    public void MessageSendObservable_Success() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        MessageApi.SendMessageResult result = Mockito.mock(MessageApi.SendMessageResult.class);
        String nodeId = "nodeId";
        String path = "path";
        byte[] data = new byte[] {};
        MessageSendObservable observable = spy(new MessageSendObservable(rxWear, nodeId, path, data, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getRequestId()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(messageApi.sendMessage(apiClient, nodeId, path, data)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, 1);
    }

    @Test
    public void MessageSendObservable_StatusException() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        MessageApi.SendMessageResult result = Mockito.mock(MessageApi.SendMessageResult.class);
        String nodeId = "nodeId";
        String path = "path";
        byte[] data = new byte[] {};
        MessageSendObservable observable = spy(new MessageSendObservable(rxWear, nodeId, path, data, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getRequestId()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(messageApi.sendMessage(apiClient, nodeId, path, data)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    /********
     * NODE *
     ********/

    // NodeListenerObservable

    @Test
    public void NodeListenerObservable_Success() {
        TestSubscriber<NodeEvent> sub = new TestSubscriber<>();
        NodeListenerObservable observable = spy(new NodeListenerObservable(rxWear, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(nodeApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(NodeApi.NodeListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        sub.assertNoTerminalEvent();
        sub.assertNoValues();
    }

    @Test
    public void NodeListenerObservable_StatusException() {
        TestSubscriber<NodeEvent> sub = new TestSubscriber<>();
        NodeListenerObservable observable = spy(new NodeListenerObservable(rxWear, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(nodeApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(NodeApi.NodeListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // NodeGetConnectedObservable

    @Test
    public void NodeGetConnectedObservable_Success() {
        TestSubscriber<List<Node>> sub = new TestSubscriber<>();
        NodeApi.GetConnectedNodesResult result = Mockito.mock(NodeApi.GetConnectedNodesResult.class);
        NodeGetConnectedObservable observable = spy(new NodeGetConnectedObservable(rxWear, null, null));

        List<Node> nodeList = new ArrayList<>();

        when(result.getStatus()).thenReturn(status);
        when(result.getNodes()).thenReturn(nodeList);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(nodeApi.getConnectedNodes(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, nodeList);
    }

    @Test
    public void NodeGetConnectedObservable_StatusException() {
        TestSubscriber<List<Node>> sub = new TestSubscriber<>();
        NodeApi.GetConnectedNodesResult result = Mockito.mock(NodeApi.GetConnectedNodesResult.class);
        NodeGetConnectedObservable observable = spy(new NodeGetConnectedObservable(rxWear, null, null));

        List<Node> nodeList = new ArrayList<>();

        when(result.getStatus()).thenReturn(status);
        when(result.getNodes()).thenReturn(nodeList);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(nodeApi.getConnectedNodes(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // NodeGetLocalObservable

    @Test
    public void NodeGetLocalObservable_Success() {
        TestSubscriber<Node> sub = new TestSubscriber<>();
        NodeApi.GetLocalNodeResult result = Mockito.mock(NodeApi.GetLocalNodeResult.class);
        Node node = Mockito.mock(Node.class);
        NodeGetLocalObservable observable = spy(new NodeGetLocalObservable(rxWear, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNode()).thenReturn(node);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(nodeApi.getLocalNode(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertSingleValue(sub, node);
    }

    @Test
    public void NodeGetLocalObservable_StatusException() {
        TestSubscriber<Node> sub = new TestSubscriber<>();
        NodeApi.GetLocalNodeResult result = Mockito.mock(NodeApi.GetLocalNodeResult.class);
        Node node = Mockito.mock(Node.class);
        NodeGetLocalObservable observable = spy(new NodeGetLocalObservable(rxWear, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNode()).thenReturn(node);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(nodeApi.getLocalNode(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }
}
