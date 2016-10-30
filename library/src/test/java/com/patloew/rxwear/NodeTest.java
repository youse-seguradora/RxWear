package com.patloew.rxwear;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Single;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, Single.class })
public class NodeTest extends BaseTest {


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
    public void Node_Listen() throws Exception {
        ArgumentCaptor<NodeListenerObservable> captor = ArgumentCaptor.forClass(NodeListenerObservable.class);

        rxWear.node().listen();
        rxWear.node().listen(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        NodeListenerObservable single = captor.getAllValues().get(0);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertTimeoutSet(single);
    }

    // Get Connected Nodes

    @Test
    public void Node_GetConnectedNodes() throws Exception {
        ArgumentCaptor<NodeGetConnectedSingle> captor = ArgumentCaptor.forClass(NodeGetConnectedSingle.class);

        rxWear.node().getConnectedNodes();
        rxWear.node().getConnectedNodes(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(atLeast(2));
        Single.create(captor.capture());

        NodeGetConnectedSingle single = captor.getAllValues().get(0);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(2);
        assertTimeoutSet(single);
    }

    // Get Local Node

    @Test
    public void Node_GetLocalNode() throws Exception {
        ArgumentCaptor<NodeGetLocalSingle> captor = ArgumentCaptor.forClass(NodeGetLocalSingle.class);

        rxWear.node().getLocalNode();
        rxWear.node().getLocalNode(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        NodeGetLocalSingle single = captor.getAllValues().get(0);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertTimeoutSet(single);
    }

}
