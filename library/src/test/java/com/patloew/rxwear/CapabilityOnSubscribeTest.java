package com.patloew.rxwear;

import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
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

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class, BaseRx.class })
public class CapabilityOnSubscribeTest extends BaseOnSubscribeTest {

    @Mock Uri uri;
    @Mock CapabilityInfo capabilityInfo;

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

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

}
