package com.patloew.rxwear;

import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.PutDataRequest;
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

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
@PrepareOnlyThisForTest({ ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class, BaseRx.class })
public class DataOnSubscribeTest extends BaseOnSubscribeTest {

    @Mock Uri uri;
    @Mock DataItemBuffer dataItemBuffer;
    @Mock DataItem dataItem;

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

    // DataListenerObservable

    @Test
    public void DataListenerObservable_Success() {
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        TestObserver<DataEvent> sub = Observable.create(observable).test();

        sub.assertNotTerminated();
        sub.assertNoValues();
    }

    @Test
    public void DataListenerObservable_StatusException() {
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, null, null, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class))).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertError(Observable.create(observable).test(), StatusException.class);
    }

    @Test
    public void DataListenerObservable_Uri_Success() {
        int filterType = 0;
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);
        TestObserver<DataEvent> sub = Observable.create(observable).test();

        sub.assertNotTerminated();
        sub.assertNoValues();
    }

    @Test
    public void DataListenerObservable_Uri_StatusException() {
        int filterType = 0;
        DataListenerObservable observable = PowerMockito.spy(new DataListenerObservable(rxWear, uri, filterType, null, null));

        setPendingResultValue(status);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.addListener(Matchers.any(GoogleApiClient.class), Matchers.any(DataApi.DataListener.class), Matchers.any(Uri.class), Matchers.anyInt())).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertError(Observable.create(observable).test(), StatusException.class);
    }

    // DataDeleteItemsSingle

    @Test
    public void DataDeleteItemsObservable_Success() {
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, null, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.deleteDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertSingleValue(Single.create(single).test(), 1);
    }

    @Test
    public void DataDeleteItemsSingle_StatusException() {
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, null, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.deleteDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertError(Single.create(single).test(), StatusException.class);
    }

    @Test
    public void DataDeleteItemsSingle_FilterType_Success() {
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        int filterType = 0;
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, filterType, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.deleteDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertSingleValue(Single.create(single).test(), 1);
    }

    @Test
    public void DataDeleteItemsSingle_FilterType_StatusException() {
        DataApi.DeleteDataItemsResult result = Mockito.mock(DataApi.DeleteDataItemsResult.class);
        int filterType = 0;
        DataDeleteItemsSingle single = PowerMockito.spy(new DataDeleteItemsSingle(rxWear, uri, filterType, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getNumDeleted()).thenReturn(1);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.deleteDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertError(Single.create(single).test(), StatusException.class);
    }

    // DataPutItemSingle

    @Test
    public void DataPutItemSingle_Success() {
        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        DataApi.DataItemResult result = Mockito.mock(DataApi.DataItemResult.class);
        DataPutItemSingle single = PowerMockito.spy(new DataPutItemSingle(rxWear, putDataRequest, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getDataItem()).thenReturn(dataItem);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.putDataItem(apiClient, putDataRequest)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertSingleValue(Single.create(single).test(), dataItem);
    }

    @Test
    public void DataPutItemSingle_StatusException() {
        PutDataRequest putDataRequest = Mockito.mock(PutDataRequest.class);
        DataApi.DataItemResult result = Mockito.mock(DataApi.DataItemResult.class);
        DataPutItemSingle single = PowerMockito.spy(new DataPutItemSingle(rxWear, putDataRequest, null, null));

        when(result.getStatus()).thenReturn(status);
        when(result.getDataItem()).thenReturn(dataItem);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.putDataItem(apiClient, putDataRequest)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertError(Single.create(single).test(), StatusException.class);
    }

    // DataGetItemsObservable

    @Test
    public void DataGetItemsObservable_Uri_FilterType_Success() {
        int filterType = 0;
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, filterType, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertNoValue(Observable.create(observable).test());
    }

    @Test
    public void DataGetItemsObservable_Uri_FilterType_StatusException() {
        int filterType = 0;
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, filterType, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getDataItems(apiClient, uri, filterType)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertError(Observable.create(observable).test(), StatusException.class);
    }

    @Test
    public void DataGetItemsObservable_Uri_Success() {
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertNoValue(Observable.create(observable).test());
    }

    @Test
    public void DataGetItemsObservable_Uri_StatusException() {
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertError(Observable.create(observable).test(), StatusException.class);
    }

    @Test
    public void DataGetItemsObservable_Success() {
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, uri, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getDataItems(apiClient, uri)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertNoValue(Observable.create(observable).test());
    }

    @Test
    public void DataGetItemsObservable_StatusException() {
        DataGetItemsObservable observable = PowerMockito.spy(new DataGetItemsObservable(rxWear, null, null, null, null));

        when(dataItemBuffer.getCount()).thenReturn(0);
        when(dataItemBuffer.getStatus()).thenReturn(status);
        setPendingResultValue(dataItemBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getDataItems(apiClient)).thenReturn(pendingResult);

        setupBaseObservableSuccess(observable);

        assertError(Observable.create(observable).test(), StatusException.class);
    }

    // DataGetFdForAssetSingle

    @Test
    public void DataGetFdForAssetSingle_DataItemAsset_Success() {
        DataItemAsset dataItemAsset = Mockito.mock(DataItemAsset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, dataItemAsset, null, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getFdForAsset(apiClient, dataItemAsset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertSingleValue(Single.create(single).test(), result);
    }

    @Test
    public void DataGetFdForAssetSingle_DataItemAsset_StatusException() {
        DataItemAsset dataItemAsset = Mockito.mock(DataItemAsset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, dataItemAsset, null, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getFdForAsset(apiClient, dataItemAsset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertError(Single.create(single).test(), StatusException.class);
    }

    @Test
    public void DataGetFdForAssetSingle_Asset_Success() {
        Asset asset = Mockito.mock(Asset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, null, asset, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(true);
        when(dataApi.getFdForAsset(apiClient, asset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertSingleValue(Single.create(single).test(), result);
    }

    @Test
    public void DataGetFdForAssetSingle_Asset_StatusException() {
        Asset asset = Mockito.mock(Asset.class);
        DataApi.GetFdForAssetResult result = Mockito.mock(DataApi.GetFdForAssetResult.class);
        DataGetFdForAssetSingle single = PowerMockito.spy(new DataGetFdForAssetSingle(rxWear, null, asset, null, null));

        when(result.getStatus()).thenReturn(status);
        setPendingResultValue(result);
        when(status.isSuccess()).thenReturn(false);
        when(dataApi.getFdForAsset(apiClient, asset)).thenReturn(pendingResult);

        setupBaseSingleSuccess(single);

        assertError(Single.create(single).test(), StatusException.class);
    }


}
