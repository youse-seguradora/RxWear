package com.patloew.rxwear;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, Single.class, PutDataMapRequest.class})
public class DataPutDataMapTest extends BaseTest {

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.spy(Single.class);
        PowerMockito.mockStatic(Observable.class);
        super.setup();
    }

    @Test
    public void Data_PutDataMap() throws Exception {
        Data.PutDataMap putDataMap = rxWear.data().putDataMap();
        assertFalse(putDataMap.urgent);
        putDataMap.urgent();
        assertTrue(putDataMap.urgent);

        PutDataMapRequest request = Mockito.mock(PutDataMapRequest.class);
        PowerMockito.mockStatic(PutDataMapRequest.class, invocation -> request);

        // WithDataMapItem

        DataMapItem dataMapItem = Mockito.mock(DataMapItem.class);
        putDataMap.withDataMapItem(dataMapItem);

        PowerMockito.verifyStatic();
        PutDataMapRequest.createFromDataMapItem(dataMapItem);

        // WithAutoAppendedId

        String pathPrefix = "/path/prefix";
        putDataMap.withAutoAppendedId(pathPrefix);

        PowerMockito.verifyStatic();
        PutDataMapRequest.createWithAutoAppendedId(pathPrefix);

        // WithAutoAppendedId

        String path = "/path";
        putDataMap.to(path);

        PowerMockito.verifyStatic();
        PutDataMapRequest.create(path);

        verify(request, times(3)).setUrgent();

    }

    @Test
    public void Data_PutDataMap_PutData() throws Exception {
        String path = "/path";
        PutDataMapRequest request = Mockito.mock(PutDataMapRequest.class);
        PowerMockito.mockStatic(PutDataMapRequest.class, invocation -> request);
        DataMap dataMap = new DataMap();
        doReturn(dataMap).when(request).getDataMap();
        Data.RxFitPutDataMapRequest putDataMapRequest = rxWear.data().putDataMap().to(path);

        {
            String keyBoolean = "boolean";
            boolean valueBoolean = true;
            assertEquals(putDataMapRequest, putDataMapRequest.putBoolean(keyBoolean, valueBoolean));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyBoolean));
            assertEquals(valueBoolean, putDataMapRequest.request.getDataMap().getBoolean(keyBoolean));
        }

        {
            String keyByte = "byte";
            byte valueByte = 123;
            assertEquals(putDataMapRequest, putDataMapRequest.putByte(keyByte, valueByte));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyByte));
            assertEquals(valueByte, putDataMapRequest.request.getDataMap().getByte(keyByte));
        }

        {
            String keyInt = "int";
            int valueInt = 1234;
            assertEquals(putDataMapRequest, putDataMapRequest.putInt(keyInt, valueInt));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyInt));
            assertEquals(valueInt, putDataMapRequest.request.getDataMap().getInt(keyInt));
        }

        {
            String keyLong = "long";
            long valueLong = 12345L;
            assertEquals(putDataMapRequest, putDataMapRequest.putLong(keyLong, valueLong));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyLong));
            assertEquals(valueLong, putDataMapRequest.request.getDataMap().getLong(keyLong));
        }

        {
            String keyFloat = "float";
            float valueFloat = 0.5f;
            assertEquals(putDataMapRequest, putDataMapRequest.putFloat(keyFloat, valueFloat));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyFloat));
            assertEquals(valueFloat, putDataMapRequest.request.getDataMap().getFloat(keyFloat));
        }

        {
            String keyDouble = "double";
            double valueDouble = 1.5;
            assertEquals(putDataMapRequest, putDataMapRequest.putDouble(keyDouble, valueDouble));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyDouble));
            assertEquals(valueDouble, putDataMapRequest.request.getDataMap().getDouble(keyDouble));
        }

        {
            String keyString = "string";
            String valueString = "value";
            assertEquals(putDataMapRequest, putDataMapRequest.putString(keyString, valueString));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyString));
            assertEquals(valueString, putDataMapRequest.request.getDataMap().getString(keyString));
        }

        {
            String keyDataMap = "dataMap";
            DataMap valueDataMap = new DataMap();
            assertEquals(putDataMapRequest, putDataMapRequest.putDataMap(keyDataMap, valueDataMap));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyDataMap));
            assertEquals(valueDataMap, putDataMapRequest.request.getDataMap().getDataMap(keyDataMap));
        }

        {
            String keyDataMapArrayList = "dataMapArrayList";
            ArrayList<DataMap> valueDataMapArrayList = new ArrayList<>(0);
            assertEquals(putDataMapRequest, putDataMapRequest.putDataMapArrayList(keyDataMapArrayList, valueDataMapArrayList));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyDataMapArrayList));
            assertEquals(valueDataMapArrayList, putDataMapRequest.request.getDataMap().getDataMapArrayList(keyDataMapArrayList));
        }

        {
            String keyIntegerArrayList = "integerArrayList";
            ArrayList<Integer> valueIntegerArrayList = new ArrayList<>(0);
            assertEquals(putDataMapRequest, putDataMapRequest.putIntegerArrayList(keyIntegerArrayList, valueIntegerArrayList));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyIntegerArrayList));
            assertEquals(valueIntegerArrayList, putDataMapRequest.request.getDataMap().getIntegerArrayList(keyIntegerArrayList));
        }

        {
            String keyStringArrayList = "stringArrayList";
            ArrayList<String> valueStringArrayList = new ArrayList<>(0);
            assertEquals(putDataMapRequest, putDataMapRequest.putStringArrayList(keyStringArrayList, valueStringArrayList));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyStringArrayList));
            assertEquals(valueStringArrayList, putDataMapRequest.request.getDataMap().getStringArrayList(keyStringArrayList));
        }

        {
            String keyByteArray = "byteArray";
            byte[] valueByteArray = new byte[] {};
            assertEquals(putDataMapRequest, putDataMapRequest.putByteArray(keyByteArray, valueByteArray));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyByteArray));
            assertEquals(valueByteArray, putDataMapRequest.request.getDataMap().getByteArray(keyByteArray));
        }

        {
            String keyLongArray = "longArray";
            long[] valueLongArray = new long[] {};
            assertEquals(putDataMapRequest, putDataMapRequest.putLongArray(keyLongArray, valueLongArray));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyLongArray));
            assertEquals(valueLongArray, putDataMapRequest.request.getDataMap().getLongArray(keyLongArray));
        }

        {
            String keyFloatArray = "floatArray";
            float[] valueFloatArray = new float[] {};
            assertEquals(putDataMapRequest, putDataMapRequest.putFloatArray(keyFloatArray, valueFloatArray));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyFloatArray));
            assertEquals(valueFloatArray, putDataMapRequest.request.getDataMap().getFloatArray(keyFloatArray));
        }

        {
            String keyStringArray = "stringArray";
            String[] valueStringArray = new String[] {};
            assertEquals(putDataMapRequest, putDataMapRequest.putStringArray(keyStringArray, valueStringArray));
            assertTrue(putDataMapRequest.request.getDataMap().containsKey(keyStringArray));
            assertEquals(valueStringArray, putDataMapRequest.request.getDataMap().getStringArray(keyStringArray));
        }
    }

    @Test
    public void Data_PutDataMap_ToSingle() throws Exception {
        String path = "/path";
        PutDataMapRequest request = Mockito.mock(PutDataMapRequest.class);
        PowerMockito.mockStatic(PutDataMapRequest.class, invocation -> request);
        DataMap dataMap = new DataMap();
        doReturn(dataMap).when(request).getDataMap();
        Data data = spy(rxWear.data());
        Data.RxFitPutDataMapRequest putDataMapRequest = spy(data.putDataMap().to(path));

        putDataMapRequest.toSingle();

        verify(data).putInternal(any(PutDataRequest.class), isNull(Long.class), isNull(TimeUnit.class));
    }

    @Test
    public void Data_PutDataMap_ToObservable() throws Exception {
        String path = "/path";
        PutDataMapRequest request = Mockito.mock(PutDataMapRequest.class);
        PowerMockito.mockStatic(PutDataMapRequest.class, invocation -> request);
        DataMap dataMap = new DataMap();
        doReturn(dataMap).when(request).getDataMap();
        Data data = spy(rxWear.data());
        Data.RxFitPutDataMapRequest putDataMapRequest = spy(data.putDataMap().to(path));

        putDataMapRequest.toObservable();

        verify(data).putInternal(any(PutDataRequest.class), isNull(Long.class), isNull(TimeUnit.class));
    }

}
