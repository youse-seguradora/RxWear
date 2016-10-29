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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
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
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class, BaseRx.class })
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

    RxWear rxWear;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        rxWear = new RxWear(ctx);

        PowerMockito.mockStatic(Wearable.class);
        Whitebox.setInternalState(Wearable.class, capabilityApi);
        Whitebox.setInternalState(Wearable.class, channelApi);
        Whitebox.setInternalState(Wearable.class, dataApi);
        Whitebox.setInternalState(Wearable.class, messageApi);
        Whitebox.setInternalState(Wearable.class, nodeApi);

        doReturn(status).when(status).getStatus();
        doReturn(ctx).when(ctx).getApplicationContext();
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
        doAnswer(invocation -> {
            final Subscriber<? super T> subscriber = invocation.getArgumentAt(0, BaseObservable.ApiClientConnectionCallbacks.class).subscriber;

            doAnswer(invocation1 -> {
                baseObservable.onGoogleApiClientReady(apiClient, subscriber);
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseObservable).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    // Mock GoogleApiClient connection success behaviour
    private <T> void setupBaseSingleSuccess(final BaseSingle<T> baseSingle) {
        setupBaseSingleSuccess(baseSingle, apiClient);
    }

    // Mock GoogleApiClient connection success behaviour
    private <T> void setupBaseSingleSuccess(final BaseSingle<T> baseSingle, final GoogleApiClient apiClient) {
        doAnswer(invocation -> {
            final SingleSubscriber<? super T> subscriber = invocation.getArgumentAt(0, BaseSingle.ApiClientConnectionCallbacks.class).subscriber;

            doAnswer(invocation1 -> {
                baseSingle.onGoogleApiClientReady(apiClient, subscriber);
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseSingle).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    // Mock GoogleApiClient connection error behaviour
    private <T> void setupBaseObservableError(final BaseObservable<T> baseObservable) {
        doAnswer(invocation -> {
            final Subscriber<? super T> subscriber = invocation.getArgumentAt(0, BaseObservable.ApiClientConnectionCallbacks.class).subscriber;

            doAnswer(invocation1 -> {
                subscriber.onError(new GoogleAPIConnectionException("Error connecting to GoogleApiClient.", connectionResult));
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseObservable).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    // Mock GoogleApiClient connection error behaviour
    private <T> void setupBaseSingleError(final BaseSingle<T> baseSingle) {
        doAnswer(invocation -> {
            final SingleSubscriber<? super T> subscriber = invocation.getArgumentAt(0, BaseSingle.ApiClientConnectionCallbacks.class).subscriber;

            doAnswer(invocation1 -> {
                subscriber.onError(new GoogleAPIConnectionException("Error connecting to GoogleApiClient.", connectionResult));
                return null;
            }).when(apiClient).connect();

            return apiClient;
        }).when(baseSingle).createApiClient(Matchers.any(BaseRx.ApiClientConnectionCallbacks.class));
    }

    @SuppressWarnings("unchecked")
    private void setPendingResultValue(final Result result) {
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, ResultCallback.class).onResult(result);
            return null;
        }).when(pendingResult).setResultCallback(Matchers.any());
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
        GoogleAPIClientSingle single = PowerMockito.spy(new GoogleAPIClientSingle(ctx, new Api[] {}, new Scope[] {}));

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, apiClient);
    }

    @Test
    public void GoogleAPIClientObservable_ConnectionException() {
        TestSubscriber<GoogleApiClient> sub = new TestSubscriber<>();
        final GoogleAPIClientSingle single = PowerMockito.spy(new GoogleAPIClientSingle(ctx, new Api[] {}, new Scope[] {}));

        setupBaseSingleError(single);
        Single.create(single).subscribe(sub);

        assertError(sub, GoogleAPIConnectionException.class);
    }

    /**************
     * CAPABILITY *
     **************/

    // CapabilityAddListenerObservable

    @Test
    public void CapabilityAddListenerObservable_String_Success() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        String capability = "capability";
        CapabilityListenerObservable observable = PowerMockito.spy(new CapabilityListenerObservable(rxWear, capability, null, null, null, null));

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
        CapabilityListenerObservable observable = PowerMockito.spy(new CapabilityListenerObservable(rxWear, capability, null, null, null, null));

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
        CapabilityListenerObservable observable = PowerMockito.spy(new CapabilityListenerObservable(rxWear, null, uri, flag, null, null));

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
        CapabilityListenerObservable observable = PowerMockito.spy(new CapabilityListenerObservable(rxWear, null, uri, flag, null, null));

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
        CapabilityGetAllSingle single = PowerMockito.spy(new CapabilityGetAllSingle(rxWear, nodeFilter, null, null));

        when(result.getAllCapabilities()).thenReturn(resultMap);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.getAllCapabilities(apiClient, nodeFilter)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, resultMap);
    }

    @Test
    public void CapabilityGetAllObservable_StatusException() {
        TestSubscriber<Map<String, CapabilityInfo>> sub = new TestSubscriber<>();
        Map<String, CapabilityInfo> resultMap = new HashMap<>();
        CapabilityApi.GetAllCapabilitiesResult result = Mockito.mock(CapabilityApi.GetAllCapabilitiesResult.class);
        int nodeFilter = 0;
        CapabilityGetAllSingle single = PowerMockito.spy(new CapabilityGetAllSingle(rxWear, nodeFilter, null, null));

        when(result.getAllCapabilities()).thenReturn(resultMap);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.getAllCapabilities(apiClient, nodeFilter)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // CapabilityGetSingle

    @Test
    public void CapabilityGetSingle_Success() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        CapabilityApi.GetCapabilityResult result = Mockito.mock(CapabilityApi.GetCapabilityResult.class);
        int nodeFilter = 0;
        String capability = "capability";
        CapabilityGetSingle single = PowerMockito.spy(new CapabilityGetSingle(rxWear, capability, nodeFilter, null, null));

        when(result.getCapability()).thenReturn(capabilityInfo);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.getCapability(apiClient, capability, nodeFilter)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, capabilityInfo);
    }

    @Test
    public void CapabilityGetSingle_StatusException() {
        TestSubscriber<CapabilityInfo> sub = new TestSubscriber<>();
        CapabilityApi.GetCapabilityResult result = Mockito.mock(CapabilityApi.GetCapabilityResult.class);
        int nodeFilter = 0;
        String capability = "capability";
        CapabilityGetSingle single = PowerMockito.spy(new CapabilityGetSingle(rxWear, capability, nodeFilter, null, null));

        when(result.getCapability()).thenReturn(capabilityInfo);
        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.getCapability(apiClient, capability, nodeFilter)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // CapabilityAddLocalSingle

    @Test
    public void CapabilityAddLocalSingle_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.AddLocalCapabilityResult result = Mockito.mock(CapabilityApi.AddLocalCapabilityResult.class);
        String capability = "capability";
        CapabilityAddLocalSingle single = PowerMockito.spy(new CapabilityAddLocalSingle(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.addLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void CapabilityAddLocalSingle_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.AddLocalCapabilityResult result = Mockito.mock(CapabilityApi.AddLocalCapabilityResult.class);
        String capability = "capability";
        CapabilityAddLocalSingle single = PowerMockito.spy(new CapabilityAddLocalSingle(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.addLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // CapabilityAddLocalSingle

    @Test
    public void CapabilityRemoveLocalSingle_Success() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.RemoveLocalCapabilityResult result = Mockito.mock(CapabilityApi.RemoveLocalCapabilityResult.class);
        String capability = "capability";
        CapabilityRemoveLocalSingle single = PowerMockito.spy(new CapabilityRemoveLocalSingle(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(capabilityApi.removeLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, status);
    }

    @Test
    public void CapabilityRemoveLocalSingle_StatusException() {
        TestSubscriber<Status> sub = new TestSubscriber<>();
        CapabilityApi.RemoveLocalCapabilityResult result = Mockito.mock(CapabilityApi.RemoveLocalCapabilityResult.class);
        String capability = "capability";
        CapabilityRemoveLocalSingle single = PowerMockito.spy(new CapabilityRemoveLocalSingle(rxWear, capability, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(capabilityApi.removeLocalCapability(apiClient, capability)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    /***********
     * CHANNEL *
     ***********/

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
        TestSubscriber<Channel> sub = new TestSubscriber<>();
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
        TestSubscriber<Channel> sub = new TestSubscriber<>();
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
        TestSubscriber<Channel> sub = new TestSubscriber<>();
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
        Channel.GetInputStreamResult result = Mockito.mock(Channel.GetInputStreamResult.class);
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
        Channel.GetInputStreamResult result = Mockito.mock(Channel.GetInputStreamResult.class);
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
        Channel.GetOutputStreamResult result = Mockito.mock(Channel.GetOutputStreamResult.class);
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
        Channel.GetOutputStreamResult result = Mockito.mock(Channel.GetOutputStreamResult.class);
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

    /********
     * DATA *
     ********/

    // DataListenerObservable

    @Test
    public void DataListenerObservable_Success() {
        TestSubscriber<DataEvent> sub = new TestSubscriber<>();
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, null, null, null, null));

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
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, null, null, null, null));

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
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, uri, filterType, null, null));

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
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataDeleteItemsSingle

    @Test
    public void DataDeleteItemsObservable_Success() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, null, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.deleteDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, 1);
    }

    @Test
    public void DataDeleteItemsSingle_StatusException() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, null, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.deleteDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void DataDeleteItemsSingle_FilterType_Success() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        int filterType = 0;
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, filterType, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.deleteDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, 1);
    }

    @Test
    public void DataDeleteItemsSingle_FilterType_StatusException() {
        TestSubscriber<Integer> sub = new TestSubscriber<>();
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        int filterType = 0;
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, filterType, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.deleteDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataPutItemSingle

    @Test
    public void DataPutItemSingle_Success() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        DataApi.DataItemResult result = Mockito.mock(DataApi.DataItemResult.class);
        DataPutItemSingle single = PowerMockito.spy(new DataPutItemSingle(rxWear, putDataRequest, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getDataItem()).thenReturn(dataItem);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.putDataItem(apiClient, putDataRequest)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, dataItem);
    }

    @Test
    public void DataPutItemSingle_StatusException() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        DataApi.DataItemResult result = Mockito.mock(DataApi.DataItemResult.class);
        DataPutItemSingle single = PowerMockito.spy(new DataPutItemSingle(rxWear, putDataRequest, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getDataItem()).thenReturn(dataItem);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.putDataItem(apiClient, putDataRequest)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataGetItemsObservable

    @Test
    public void DataGetItemsObservable_Uri_FilterType_Success() {
        TestSubscriber<DataItem> sub = new TestSubscriber<>();
        int filterType = 0;
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, filterType, null, null));

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
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, filterType, null, null));

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
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

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
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

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
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

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
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, null, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getDataItems(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // DataGetFdForAssetSingle

    @Test
    public void DataGetFdForAssetSingle_DataItemAsset_Success() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        DataItemAsset dataItemAsset = Mockito.mock(DataItemAsset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, dataItemAsset, null, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getFdForAsset(apiClient, dataItemAsset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, result);
    }

    @Test
    public void DataGetFdForAssetSingle_DataItemAsset_StatusException() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        DataItemAsset dataItemAsset = Mockito.mock(DataItemAsset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, dataItemAsset, null, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getFdForAsset(apiClient, dataItemAsset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    @Test
    public void DataGetFdForAssetSingle_Asset_Success() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        Asset asset = Mockito.mock(Asset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, null, asset, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getFdForAsset(apiClient, asset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, result);
    }

    @Test
    public void DataGetFdForAssetSingle_Asset_StatusException() {
        TestSubscriber<DataApi.GetFdForAssetResult> sub = new TestSubscriber<>();
        Asset asset = Mockito.mock(Asset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, null, asset, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getFdForAsset(apiClient, asset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }


    /***********
     * MESSAGE *
     ***********/

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

    /********
     * NODE *
     ********/

    // NodeListenerObservable

    @Test
    public void NodeListenerObservable_Success() {
        TestSubscriber<NodeEvent> sub = new TestSubscriber<>();
        NodeListenerObservable observable = PowerMockito.spy(new NodeListenerObservable(rxWear, null, null));

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
        NodeListenerObservable observable = PowerMockito.spy(new NodeListenerObservable(rxWear, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(nodeApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(NodeApi.NodeListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        Observable.create(observable).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // NodeGetConnectedSingle

    @Test
    public void NodeGetConnectedSingle_Success() {
        TestSubscriber<List<Node>> sub = new TestSubscriber<>();
        NodeApi.GetConnectedNodesResult result = Mockito.mock(NodeApi.GetConnectedNodesResult.class);
        NodeGetConnectedSingle single = PowerMockito.spy(new NodeGetConnectedSingle(rxWear, null, null));

        List<Node> nodeList = new ArrayList<>();

        when(result.getStatus()).thenReturn(status);
        when(result.getNodes()).thenReturn(nodeList);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(nodeApi.getConnectedNodes(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, nodeList);
    }

    @Test
    public void NodeGetConnectedSingle_StatusException() {
        TestSubscriber<List<Node>> sub = new TestSubscriber<>();
        NodeApi.GetConnectedNodesResult result = Mockito.mock(NodeApi.GetConnectedNodesResult.class);
        NodeGetConnectedSingle single = PowerMockito.spy(new NodeGetConnectedSingle(rxWear, null, null));

        List<Node> nodeList = new ArrayList<>();

        when(result.getStatus()).thenReturn(status);
        when(result.getNodes()).thenReturn(nodeList);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(nodeApi.getConnectedNodes(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }

    // NodeGetLocalSingle

    @Test
    public void NodeGetLocalSingle_Success() {
        TestSubscriber<Node> sub = new TestSubscriber<>();
        NodeApi.GetLocalNodeResult result = Mockito.mock(NodeApi.GetLocalNodeResult.class);
        Node node = Mockito.mock(Node.class);
        NodeGetLocalSingle single = PowerMockito.spy(new NodeGetLocalSingle(rxWear, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNode()).thenReturn(node);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(nodeApi.getLocalNode(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, node);
    }

    @Test
    public void NodeGetLocalSingle_StatusException() {
        TestSubscriber<Node> sub = new TestSubscriber<>();
        NodeApi.GetLocalNodeResult result = Mockito.mock(NodeApi.GetLocalNodeResult.class);
        Node node = Mockito.mock(Node.class);
        NodeGetLocalSingle single = PowerMockito.spy(new NodeGetLocalSingle(rxWear, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNode()).thenReturn(node);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(nodeApi.getLocalNode(apiClient)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertError(sub, StatusException.class);
    }
}
