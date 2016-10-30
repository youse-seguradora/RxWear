package com.patloew.rxwear;

import com.google.android.gms.wearable.DataMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, Single.class })
public class MessageSendDataMapTest extends BaseTest {

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.spy(Single.class);
        PowerMockito.mockStatic(Observable.class);
        super.setup();
    }

    @Test
    public void Message_SendDataMap() throws Exception {
        String nodeId = "nodeId";
        String path = "path";
        Message.SendDataMap sendDataMap = rxWear.message().sendDataMap(nodeId, path);

        assertEquals(nodeId, sendDataMap.nodeId);
        assertEquals(path, sendDataMap.path);
        assertFalse(sendDataMap.toAllRemoteNodes);
    }

    @Test
    public void Message_SendDataMapToAllRemoteNodes() throws Exception {
        String path = "path";
        Message.SendDataMap sendDataMap = rxWear.message().sendDataMapToAllRemoteNodes(path);

        assertNull(sendDataMap.nodeId);
        assertEquals(path, sendDataMap.path);
        assertTrue(sendDataMap.toAllRemoteNodes);
    }

    @Test
    public void Message_SendDataMap_PutData() throws Exception {
        String nodeId = "nodeId";
        String path = "path";
        Message.SendDataMap sendDataMap = rxWear.message().sendDataMap(nodeId, path);

        {
            String keyBoolean = "boolean";
            boolean valueBoolean = true;
            assertEquals(sendDataMap, sendDataMap.putBoolean(keyBoolean, valueBoolean));
            assertTrue(sendDataMap.dataMap.containsKey(keyBoolean));
            assertEquals(valueBoolean, sendDataMap.dataMap.getBoolean(keyBoolean));
        }

        {
            String keyByte = "byte";
            byte valueByte = 123;
            assertEquals(sendDataMap, sendDataMap.putByte(keyByte, valueByte));
            assertTrue(sendDataMap.dataMap.containsKey(keyByte));
            assertEquals(valueByte, sendDataMap.dataMap.getByte(keyByte));
        }

        {
            String keyInt = "int";
            int valueInt = 1234;
            assertEquals(sendDataMap, sendDataMap.putInt(keyInt, valueInt));
            assertTrue(sendDataMap.dataMap.containsKey(keyInt));
            assertEquals(valueInt, sendDataMap.dataMap.getInt(keyInt));
        }

        {
            String keyLong = "long";
            long valueLong = 12345L;
            assertEquals(sendDataMap, sendDataMap.putLong(keyLong, valueLong));
            assertTrue(sendDataMap.dataMap.containsKey(keyLong));
            assertEquals(valueLong, sendDataMap.dataMap.getLong(keyLong));
        }

        {
            String keyFloat = "float";
            float valueFloat = 0.5f;
            assertEquals(sendDataMap, sendDataMap.putFloat(keyFloat, valueFloat));
            assertTrue(sendDataMap.dataMap.containsKey(keyFloat));
            assertEquals(valueFloat, sendDataMap.dataMap.getFloat(keyFloat));
        }

        {
            String keyDouble = "double";
            double valueDouble = 1.5;
            assertEquals(sendDataMap, sendDataMap.putDouble(keyDouble, valueDouble));
            assertTrue(sendDataMap.dataMap.containsKey(keyDouble));
            assertEquals(valueDouble, sendDataMap.dataMap.getDouble(keyDouble));
        }

        {
            String keyString = "string";
            String valueString = "value";
            assertEquals(sendDataMap, sendDataMap.putString(keyString, valueString));
            assertTrue(sendDataMap.dataMap.containsKey(keyString));
            assertEquals(valueString, sendDataMap.dataMap.getString(keyString));
        }

        {
            String keyDataMap = "dataMap";
            DataMap valueDataMap = new DataMap();
            assertEquals(sendDataMap, sendDataMap.putDataMap(keyDataMap, valueDataMap));
            assertTrue(sendDataMap.dataMap.containsKey(keyDataMap));
            assertEquals(valueDataMap, sendDataMap.dataMap.getDataMap(keyDataMap));
        }

        {
            String keyDataMapArrayList = "dataMapArrayList";
            ArrayList<DataMap> valueDataMapArrayList = new ArrayList<>(0);
            assertEquals(sendDataMap, sendDataMap.putDataMapArrayList(keyDataMapArrayList, valueDataMapArrayList));
            assertTrue(sendDataMap.dataMap.containsKey(keyDataMapArrayList));
            assertEquals(valueDataMapArrayList, sendDataMap.dataMap.getDataMapArrayList(keyDataMapArrayList));
        }

        {
            String keyIntegerArrayList = "integerArrayList";
            ArrayList<Integer> valueIntegerArrayList = new ArrayList<>(0);
            assertEquals(sendDataMap, sendDataMap.putIntegerArrayList(keyIntegerArrayList, valueIntegerArrayList));
            assertTrue(sendDataMap.dataMap.containsKey(keyIntegerArrayList));
            assertEquals(valueIntegerArrayList, sendDataMap.dataMap.getIntegerArrayList(keyIntegerArrayList));
        }

        {
            String keyStringArrayList = "stringArrayList";
            ArrayList<String> valueStringArrayList = new ArrayList<>(0);
            assertEquals(sendDataMap, sendDataMap.putStringArrayList(keyStringArrayList, valueStringArrayList));
            assertTrue(sendDataMap.dataMap.containsKey(keyStringArrayList));
            assertEquals(valueStringArrayList, sendDataMap.dataMap.getStringArrayList(keyStringArrayList));
        }

        {
            String keyByteArray = "byteArray";
            byte[] valueByteArray = new byte[] {};
            assertEquals(sendDataMap, sendDataMap.putByteArray(keyByteArray, valueByteArray));
            assertTrue(sendDataMap.dataMap.containsKey(keyByteArray));
            assertEquals(valueByteArray, sendDataMap.dataMap.getByteArray(keyByteArray));
        }

        {
            String keyLongArray = "longArray";
            long[] valueLongArray = new long[] {};
            assertEquals(sendDataMap, sendDataMap.putLongArray(keyLongArray, valueLongArray));
            assertTrue(sendDataMap.dataMap.containsKey(keyLongArray));
            assertEquals(valueLongArray, sendDataMap.dataMap.getLongArray(keyLongArray));
        }

        {
            String keyFloatArray = "floatArray";
            float[] valueFloatArray = new float[] {};
            assertEquals(sendDataMap, sendDataMap.putFloatArray(keyFloatArray, valueFloatArray));
            assertTrue(sendDataMap.dataMap.containsKey(keyFloatArray));
            assertEquals(valueFloatArray, sendDataMap.dataMap.getFloatArray(keyFloatArray));
        }

        {
            String keyStringArray = "stringArray";
            String[] valueStringArray = new String[] {};
            assertEquals(sendDataMap, sendDataMap.putStringArray(keyStringArray, valueStringArray));
            assertTrue(sendDataMap.dataMap.containsKey(keyStringArray));
            assertEquals(valueStringArray, sendDataMap.dataMap.getStringArray(keyStringArray));
        }

    }

    @Test
    public void Message_SendDataMap_ToSingle_OneNode() throws Exception {
        String nodeId = "nodeId";
        String path = "path";
        Message message = spy(rxWear.message());
        Message.SendDataMap sendDataMap = spy(message.sendDataMap(nodeId, path));

        sendDataMap.toSingle();

        verify(message).sendInternal(eq(nodeId), eq(path), any(byte[].class), isNull(Long.class), isNull(TimeUnit.class));
    }

    @Test
    public void Message_SendDataMap_ToSingle_ToAllRemoteNodes() throws Exception {
        String path = "path";
        Message message = spy(rxWear.message());
        Message.SendDataMap sendDataMap = spy(message.sendDataMapToAllRemoteNodes(path));

        TestSubscriber<Integer> sub = new TestSubscriber<>();
        sendDataMap.toSingle().subscribe(sub);

        sub.assertError(UnsupportedOperationException.class);
        sub.assertNoValues();
    }

    @Test
    public void Message_SendDataMap_ToObservable_OneNode() throws Exception {
        String nodeId = "nodeId";
        String path = "path";
        Message message = spy(rxWear.message());
        Message.SendDataMap sendDataMap = spy(message.sendDataMap(nodeId, path));

        sendDataMap.toObservable();

        verify(message).sendInternal(eq(nodeId), eq(path), any(byte[].class), isNull(Long.class), isNull(TimeUnit.class));
    }

    @Test
    public void Message_SendDataMap_ToObservable_ToAllRemoveNodes() throws Exception {
        String path = "path";
        Message message = spy(rxWear.message());
        Message.SendDataMap sendDataMap = spy(message.sendDataMapToAllRemoteNodes(path));
        doReturn(Observable.never()).when(message).sendToAllRemoteNodesInternal(eq(path), any(byte[].class), isNull(Long.class), isNull(TimeUnit.class));

        sendDataMap.toObservable();

        verify(message).sendToAllRemoteNodesInternal(eq(path), any(byte[].class), isNull(Long.class), isNull(TimeUnit.class));
    }

}
