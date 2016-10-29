package com.patloew.rxwear;

import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.patloew.rxwear.events.NodeEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class, BaseRx.class })
public class NodeOnSubscribeTest extends BaseOnSubscribeTest {

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

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
        TestSubscriber<List<com.google.android.gms.wearable.Node>> sub = new TestSubscriber<>();
        NodeApi.GetConnectedNodesResult result = Mockito.mock(NodeApi.GetConnectedNodesResult.class);
        NodeGetConnectedSingle single = PowerMockito.spy(new NodeGetConnectedSingle(rxWear, null, null));

        List<com.google.android.gms.wearable.Node> nodeList = new ArrayList<>();

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
        TestSubscriber<List<com.google.android.gms.wearable.Node>> sub = new TestSubscriber<>();
        NodeApi.GetConnectedNodesResult result = Mockito.mock(NodeApi.GetConnectedNodesResult.class);
        NodeGetConnectedSingle single = PowerMockito.spy(new NodeGetConnectedSingle(rxWear, null, null));

        List<com.google.android.gms.wearable.Node> nodeList = new ArrayList<>();

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
        TestSubscriber<com.google.android.gms.wearable.Node> sub = new TestSubscriber<>();
        NodeApi.GetLocalNodeResult result = Mockito.mock(NodeApi.GetLocalNodeResult.class);
        com.google.android.gms.wearable.Node node = Mockito.mock(com.google.android.gms.wearable.Node.class);
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
        TestSubscriber<com.google.android.gms.wearable.Node> sub = new TestSubscriber<>();
        NodeApi.GetLocalNodeResult result = Mockito.mock(NodeApi.GetLocalNodeResult.class);
        com.google.android.gms.wearable.Node node = Mockito.mock(com.google.android.gms.wearable.Node.class);
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
