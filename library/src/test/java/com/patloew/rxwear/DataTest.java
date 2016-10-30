package com.patloew.rxwear;

import android.net.Uri;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.PutDataMapRequest;
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
public class DataTest extends BaseTest {

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
    public void Data_Listen() throws Exception {
        ArgumentCaptor<DataListenerObservable> captor = ArgumentCaptor.forClass(DataListenerObservable.class);

        rxWear.data().listen();
        rxWear.data().listen(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataListenerObservable single = captor.getAllValues().get(0);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Listen_UriFilterType() throws Exception {
        ArgumentCaptor<DataListenerObservable> captor = ArgumentCaptor.forClass(DataListenerObservable.class);

        int filterType = 123;
        rxWear.data().listen(uri, filterType);
        rxWear.data().listen(uri, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataListenerObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Listen_PathFilterType() throws Exception {
        ArgumentCaptor<DataListenerObservable> captor = ArgumentCaptor.forClass(DataListenerObservable.class);

        String path = "path";
        int filterType = 123;

        Uri.Builder uriBuilder = Mockito.mock(Uri.Builder.class);
        Data data = spy(rxWear.data());
        doReturn(uriBuilder).when(data).getUriBuilder();
        doReturn(uriBuilder).when(uriBuilder).scheme(PutDataRequest.WEAR_URI_SCHEME);
        doReturn(uriBuilder).when(uriBuilder).path(path);
        doReturn(uri).when(uriBuilder).build();

        data.listen(path, filterType);
        data.listen(path, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataListenerObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertTimeoutSet(single);
    }

    // Delete

    @Test
    public void Data_Delete_Uri() throws Exception {
        ArgumentCaptor<DataDeleteItemsSingle> captor = ArgumentCaptor.forClass(DataDeleteItemsSingle.class);

        rxWear.data().delete(uri);
        rxWear.data().delete(uri, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        DataDeleteItemsSingle single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertNull(single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertNull(single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Delete_UriFilterType() throws Exception {
        ArgumentCaptor<DataDeleteItemsSingle> captor = ArgumentCaptor.forClass(DataDeleteItemsSingle.class);

        int filterType = 123;
        rxWear.data().delete(uri, filterType);
        rxWear.data().delete(uri, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        DataDeleteItemsSingle single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertTimeoutSet(single);
    }

    // Delete

    @Test
    public void Data_Put_PutDataRequest() throws Exception {
        ArgumentCaptor<DataPutItemSingle> captor = ArgumentCaptor.forClass(DataPutItemSingle.class);

        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        rxWear.data().put(putDataRequest);
        rxWear.data().put(putDataRequest, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        DataPutItemSingle single = captor.getAllValues().get(0);
        assertEquals(putDataRequest, single.putDataRequest);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(putDataRequest, single.putDataRequest);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Put_PutDataMapRequest() throws Exception {
        ArgumentCaptor<DataPutItemSingle> captor = ArgumentCaptor.forClass(DataPutItemSingle.class);

        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        PutDataMapRequest putDataMapRequest = Mockito.mock(PutDataMapRequest.class);
        doReturn(putDataRequest).when(putDataMapRequest).asPutDataRequest();

        rxWear.data().put(putDataMapRequest);
        rxWear.data().put(putDataMapRequest, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        DataPutItemSingle single = captor.getAllValues().get(0);
        assertEquals(putDataRequest, single.putDataRequest);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(putDataRequest, single.putDataRequest);
        assertTimeoutSet(single);
    }

    // Get

    @Test
    public void Data_Get() throws Exception {
        ArgumentCaptor<DataGetItemsObservable> captor = ArgumentCaptor.forClass(DataGetItemsObservable.class);

        rxWear.data().get();
        rxWear.data().get(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataGetItemsObservable single = captor.getAllValues().get(0);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertNull(single.uri);
        assertNull(single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Get_Uri() throws Exception {
        ArgumentCaptor<DataGetItemsObservable> captor = ArgumentCaptor.forClass(DataGetItemsObservable.class);

        rxWear.data().get(uri);
        rxWear.data().get(uri, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataGetItemsObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertNull(single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertNull(single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Get_UriFilterType() throws Exception {
        ArgumentCaptor<DataGetItemsObservable> captor = ArgumentCaptor.forClass(DataGetItemsObservable.class);

        int filterType = 123;
        rxWear.data().get(uri, filterType);
        rxWear.data().get(uri, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataGetItemsObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Get_Path() throws Exception {
        ArgumentCaptor<DataGetItemsObservable> captor = ArgumentCaptor.forClass(DataGetItemsObservable.class);

        String path = "path";

        Uri.Builder uriBuilder = Mockito.mock(Uri.Builder.class);
        Data data = spy(rxWear.data());
        doReturn(uriBuilder).when(data).getUriBuilder();
        doReturn(uriBuilder).when(uriBuilder).scheme(PutDataRequest.WEAR_URI_SCHEME);
        doReturn(uriBuilder).when(uriBuilder).path(path);
        doReturn(uri).when(uriBuilder).build();

        data.get(path);
        data.get(path, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataGetItemsObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertNull(single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertNull(single.filterType);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_Get_PathFilterType() throws Exception {
        ArgumentCaptor<DataGetItemsObservable> captor = ArgumentCaptor.forClass(DataGetItemsObservable.class);

        String path = "path";
        int filterType = 123;

        Uri.Builder uriBuilder = Mockito.mock(Uri.Builder.class);
        Data data = spy(rxWear.data());
        doReturn(uriBuilder).when(data).getUriBuilder();
        doReturn(uriBuilder).when(uriBuilder).scheme(PutDataRequest.WEAR_URI_SCHEME);
        doReturn(uriBuilder).when(uriBuilder).path(path);
        doReturn(uri).when(uriBuilder).build();

        data.get(path, filterType);
        data.get(path, filterType, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Observable.create(captor.capture());

        DataGetItemsObservable single = captor.getAllValues().get(0);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertEquals(uri, single.uri);
        assertEquals(filterType, (int) single.filterType);
        assertTimeoutSet(single);
    }

    // GetFdForAsset

    @Test
    public void Data_GetFdForAsset_DataItemAsset() throws Exception {
        ArgumentCaptor<DataGetFdForAssetSingle> captor = ArgumentCaptor.forClass(DataGetFdForAssetSingle.class);

        DataItemAsset dataItemAsset = Mockito.mock(DataItemAsset.class);
        rxWear.data().getFdForAsset(dataItemAsset);
        rxWear.data().getFdForAsset(dataItemAsset, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        DataGetFdForAssetSingle single = captor.getAllValues().get(0);
        assertNull(single.asset);
        assertEquals(dataItemAsset, single.dataItemAsset);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertNull(single.asset);
        assertEquals(dataItemAsset, single.dataItemAsset);
        assertTimeoutSet(single);
    }

    @Test
    public void Data_GetFdForAsset_Asset() throws Exception {
        ArgumentCaptor<DataGetFdForAssetSingle> captor = ArgumentCaptor.forClass(DataGetFdForAssetSingle.class);

        Asset asset = Mockito.mock(Asset.class);
        rxWear.data().getFdForAsset(asset);
        rxWear.data().getFdForAsset(asset, TIMEOUT_TIME, TIMEOUT_TIMEUNIT);

        PowerMockito.verifyStatic(times(2));
        Single.create(captor.capture());

        DataGetFdForAssetSingle single = captor.getAllValues().get(0);
        assertNull(single.dataItemAsset);
        assertEquals(asset, single.asset);
        assertNoTimeoutSet(single);

        single = captor.getAllValues().get(1);
        assertNull(single.dataItemAsset);
        assertEquals(asset, single.asset);
        assertTimeoutSet(single);
    }

}
