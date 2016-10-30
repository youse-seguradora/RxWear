package com.patloew.rxwear;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Single;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, Single.class })
public class CapabilityTest extends BaseTest {

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
    public void Capability_Listen_Capability() throws Exception {
        ArgumentCaptor<CapabilityListenerObservable> captor = ArgumentCaptor.forClass(CapabilityListenerObservable.class);

        String capability = "capability";
        rxWear.capability().listen(capability);
        rxWear.capability().listen(capability, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        CapabilityListenerObservable single = captor.getAllValues().get(0);
        assertEquals(capability, single.capability);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(capability, single.capability);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Capability_Listen_UriFilterType() throws Exception {
        ArgumentCaptor<CapabilityListenerObservable> captor = ArgumentCaptor.forClass(CapabilityListenerObservable.class);

        Uri uri = Mockito.mock(Uri.class);
        int filterType = 123;
        rxWear.capability().listen(uri, filterType);
        rxWear.capability().listen(uri, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        CapabilityListenerObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNull(single.capability);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNull(single.capability);
        assertTimeoutSet(single);
    }

    // Get All

    @Test
    public void Capability_GetAll() throws Exception {
        ArgumentCaptor<CapabilityGetAllSingle> captor = ArgumentCaptor.forClass(CapabilityGetAllSingle.class);

        int nodeFilter = 123;
        rxWear.capability().getAll(nodeFilter);
        rxWear.capability().getAll(nodeFilter, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(atLeast(2));
        Single.create(captor.capture());

        CapabilityGetAllSingle single = captor.getAllValues().get(0);
        assertEquals(nodeFilter, single.nodeFilter);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(2);
        assertEquals(nodeFilter, single.nodeFilter);
        assertTimeoutSet(single);
    }

    // Get

    @Test
    public void Capability_Get() throws Exception {
        ArgumentCaptor<CapabilityGetSingle> captor = ArgumentCaptor.forClass(CapabilityGetSingle.class);

        String capability = "capability";
        int nodeFilter = 123;
        rxWear.capability().get(capability, nodeFilter);
        rxWear.capability().get(capability, nodeFilter, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        CapabilityGetSingle single = captor.getAllValues().get(0);
        assertEquals(capability, single.capability);
        assertEquals(nodeFilter, single.nodeFilter);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(capability, single.capability);
        assertEquals(nodeFilter, single.nodeFilter);
        assertTimeoutSet(single);
    }

    // Add Local

    @Test
    public void Capability_AddLocal() throws Exception {
        ArgumentCaptor<CapabilityAddLocalSingle> captor = ArgumentCaptor.forClass(CapabilityAddLocalSingle.class);

        String capability = "capability";
        rxWear.capability().addLocal(capability);
        rxWear.capability().addLocal(capability, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        CapabilityAddLocalSingle single = captor.getAllValues().get(0);
        assertEquals(capability, single.capability);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(capability, single.capability);
        assertTimeoutSet(single);
    }

    // Remove Local

    @Test
    public void Capability_RemoveLocal() throws Exception {
        ArgumentCaptor<CapabilityRemoveLocalSingle> captor = ArgumentCaptor.forClass(CapabilityRemoveLocalSingle.class);

        String capability = "capability";
        rxWear.capability().removeLocal(capability);
        rxWear.capability().removeLocal(capability, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        CapabilityRemoveLocalSingle single = captor.getAllValues().get(0);
        assertEquals(capability, single.capability);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(capability, single.capability);
        assertTimeoutSet(single);
    }

}
