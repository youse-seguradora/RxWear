package com.patloew.rxwear;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.patloew.rxwear.events.ChannelEvent;
import com.patloew.rxwear.events.NodeEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

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
 * limitations under the License.
 *
 * -------------------------------
 *
 * Factory for Google Wearable API observables.
 *
 */
public class RxWear {

    private static RxWear instance = null;

    private static Long timeoutTime = null;
    private static TimeUnit timeoutUnit = null;

    private final Context ctx;

    /* Initializes the singleton instance of RxWear
     *
     * @param ctx Context.
     */
    public static void init(@NonNull Context ctx) {
        if(instance == null) { instance = new RxWear(ctx); }
    }

    /* Set a default timeout for all requests to the Wearable API made in the lib.
     * When a timeout occurs, onError() is called with a StatusException.
     */
    public static void setDefaultTimeout(long time, @NonNull TimeUnit timeUnit) {
        if(timeUnit != null) {
            timeoutTime = time;
            timeoutUnit = timeUnit;
        } else {
            throw new IllegalArgumentException("timeUnit parameter must not be null");
        }
    }

    /* Reset the default timeout.
     */
    public static void resetDefaultTimeout() {
        timeoutTime = null;
        timeoutUnit = null;
    }

    /* Gets the singleton instance of RxWear, after it was initialized.
     */
    private static RxWear get() {
        if(instance == null) { throw new IllegalStateException("RxWear not initialized"); }
        return instance;
    }


    private RxWear(@NonNull Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    Context getContext() {
        return ctx;
    }

    static Long getDefaultTimeout() {
        return timeoutTime;
    }

    static TimeUnit getDefaultTimeoutUnit() {
        return timeoutUnit;
    }


    /* Can be used to check whether connection to Wearable API was successful.
     *
     * This Completable completes if the connection was successful.
     */
    public static Completable checkConnection() {
        return Completable.fromSingle(getWearableClient());
    }

    public static Single<GoogleApiClient> getWearableClient() {
        return GoogleAPIClientSingle.create(RxWear.get().getContext(), Wearable.API);
    }

    public static class Capability {

        private Capability() { }

        // listen

        public static Observable<CapabilityInfo> listen(@NonNull String capability) {
            return listenInternal(capability, null, null, null, null);
        }

        public static Observable<CapabilityInfo> listen(@NonNull String capability, long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(capability, null, null, timeout, timeUnit);
        }

        public static Observable<CapabilityInfo> listen(@NonNull Uri uri, int filterType) {
            return listenInternal(null, uri, filterType, null, null);
        }

        public static Observable<CapabilityInfo> listen(@NonNull Uri uri, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(null, uri, filterType, timeout, timeUnit);
        }

        private static Observable<CapabilityInfo> listenInternal(String capability, Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
            return Observable.create(new CapabilityListenerObservable(RxWear.get(), capability, uri, filterType, timeout, timeUnit));
        }

        // getAll

        public static Observable<CapabilityInfo> getAll(int nodeFilter) {
            return getAllInternal(nodeFilter, null, null);
        }

        public static Observable<CapabilityInfo> getAll(int nodeFilter, long timeout, @NonNull TimeUnit timeUnit) {
            return getAllInternal(nodeFilter, timeout, timeUnit);
        }

        private static Observable<CapabilityInfo> getAllInternal(int nodeFilter, Long timeout, TimeUnit timeUnit) {
            return Single.create(new CapabilityGetAllSingle(RxWear.get(), nodeFilter, timeout, timeUnit))
                    .flatMapObservable(new Func1<Map<String, CapabilityInfo>, Observable<CapabilityInfo>>() {
                        @Override
                        public Observable<CapabilityInfo> call(Map<String, CapabilityInfo> capabilityInfoMap) {
                            return Observable.from(capabilityInfoMap.values());
                        }
                    });
        }

        // get

        public static Single<CapabilityInfo> get(@NonNull String capability, int nodeFilter) {
            return getInternal(capability, nodeFilter, null, null);
        }

        public static Single<CapabilityInfo> get(@NonNull String capability, int nodeFilter, long timeout, @NonNull TimeUnit timeUnit) {
            return getInternal(capability, nodeFilter, timeout, timeUnit);
        }

        private static Single<CapabilityInfo> getInternal(String capability, int nodeFilter, Long timeout, TimeUnit timeUnit) {
            return Single.create(new CapabilityGetSingle(RxWear.get(), capability, nodeFilter, timeout, timeUnit));
        }

        // addLocal

        public static Single<Status> addLocal(@NonNull String capability) {
            return addLocalInternal(capability, null, null);
        }

        public static Single<Status> addLocal(@NonNull String capability, long timeout, @NonNull TimeUnit timeUnit) {
            return addLocalInternal(capability, timeout, timeUnit);
        }

        private static Single<Status> addLocalInternal(String capability, Long timeout, TimeUnit timeUnit) {
            return Single.create(new CapabilityAddLocalSingle(RxWear.get(), capability, timeout, timeUnit));
        }

        // removeLocal

        public static Single<Status> removeLocal(@NonNull String capability) {
            return removeLocalInternal(capability, null, null);
        }

        public static Single<Status> removeLocal(@NonNull String capability, long timeout, @NonNull TimeUnit timeUnit) {
            return removeLocalInternal(capability, timeout, timeUnit);
        }

        private static Single<Status> removeLocalInternal(String capability, Long timeout, TimeUnit timeUnit) {
            return Single.create(new CapabilityRemoveLocalSingle(RxWear.get(), capability, timeout, timeUnit));
        }
    }


    public static class Channel {

        private Channel() { }

        // listen

        public static Observable<ChannelEvent> listen() {
            return listenInternal(null, null, null);
        }

        public static Observable<ChannelEvent> listen(long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(null, timeout, timeUnit);
        }

        public static Observable<ChannelEvent> listen(@NonNull com.google.android.gms.wearable.Channel channel) {
            return listenInternal(channel, null, null);
        }

        public static Observable<ChannelEvent> listen(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(channel, timeout, timeUnit);
        }

        private static Observable<ChannelEvent> listenInternal(com.google.android.gms.wearable.Channel channel, Long timeout, TimeUnit timeUnit) {
            return Observable.create(new ChannelListenerObservable(RxWear.get(), channel, timeout, timeUnit));
        }

        // close

        public static Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel) {
            return closeInternal(channel, null, null, null);
        }

        public static Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
            return closeInternal(channel, null, timeout, timeUnit);
        }

        public static Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel, int errorCode) {
            return closeInternal(channel, errorCode, null, null);
        }

        public static Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel, int errorCode, long timeout, @NonNull TimeUnit timeUnit) {
            return closeInternal(channel, errorCode, timeout, timeUnit);
        }

        private static Single<Status> closeInternal(com.google.android.gms.wearable.Channel channel, Integer errorCode, Long timeout, TimeUnit timeUnit) {
            return Single.create(new ChannelCloseSingle(RxWear.get(), channel, errorCode, timeout, timeUnit));
        }

        // sendFile

        public static Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri) {
            return sendFileInternal(channel, uri, null, null, null, null);
        }

        public static Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, long timeout, @NonNull TimeUnit timeUnit) {
            return sendFileInternal(channel, uri, null, null, timeout, timeUnit);
        }

        public static Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, long startOffset, long length) {
            return sendFileInternal(channel, uri, startOffset, length, null, null);
        }

        public static Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, long startOffset, long length, long timeout, @NonNull TimeUnit timeUnit) {
            return sendFileInternal(channel, uri, startOffset, length, timeout, timeUnit);
        }

        private static Single<Status> sendFileInternal(com.google.android.gms.wearable.Channel channel, Uri uri, Long startOffset, Long length, Long timeout, TimeUnit timeUnit) {
            return Single.create(new ChannelSendFileSingle(RxWear.get(), channel, uri, startOffset, length, timeout, timeUnit));
        }

        // receiveFile

        public static Single<Status> receiveFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, boolean append) {
            return receiveFileInternal(channel, uri, append, null, null);
        }

        public static Single<Status> receiveFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, boolean append, long timeout, @NonNull TimeUnit timeUnit) {
            return receiveFileInternal(channel, uri, append, timeout, timeUnit);
        }

        private static Single<Status> receiveFileInternal(com.google.android.gms.wearable.Channel channel, Uri uri, boolean append, Long timeout, TimeUnit timeUnit) {
            return Single.create(new ChannelReceiveFileSingle(RxWear.get(), channel, uri, append, timeout, timeUnit));
        }

        // getInputStream

        public static Single<InputStream> getInputStream(@NonNull com.google.android.gms.wearable.Channel channel) {
            return getInputStreamInternal(channel, null, null);
        }

        public static Single<InputStream> getInputStream(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
            return getInputStreamInternal(channel, timeout, timeUnit);
        }

        private static Single<InputStream> getInputStreamInternal(com.google.android.gms.wearable.Channel channel, Long timeout, TimeUnit timeUnit) {
            return Single.create(new ChannelGetInputStreamSingle(RxWear.get(), channel, timeout, timeUnit));
        }

        // getOutputStream

        public static Single<OutputStream> getOutputStream(@NonNull com.google.android.gms.wearable.Channel channel) {
            return getOutputStreamInternal(channel, null, null);
        }

        public static Single<OutputStream> getOutputStream(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
            return getOutputStreamInternal(channel, timeout, timeUnit);
        }

        private static Single<OutputStream> getOutputStreamInternal(com.google.android.gms.wearable.Channel channel, Long timeout, TimeUnit timeUnit) {
            return Single.create(new ChannelGetOutputStreamSingle(RxWear.get(), channel, timeout, timeUnit));
        }

        // open

        public static Single<com.google.android.gms.wearable.Channel> open(@NonNull String nodeId, @NonNull String path) {
            return openInternal(nodeId, path, null, null);
        }

        public static Single<com.google.android.gms.wearable.Channel> open(@NonNull String nodeId, @NonNull String path, long timeout, @NonNull TimeUnit timeUnit) {
            return openInternal(nodeId, path, timeout, timeUnit);
        }

        private static Single<com.google.android.gms.wearable.Channel> openInternal(String nodeId, String path, Long timeout, TimeUnit timeUnit) {
            return Single.create(new ChannelOpenSingle(RxWear.get(), nodeId, path, timeout, timeUnit));
        }
    }

    public static class Data {

        private Data() { }

        // listen

        public static Observable<DataEvent> listen() {
            return listenInternal(null, null, null, null);
        }

        public static Observable<DataEvent> listen(long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(null, null, timeout, timeUnit);
        }

        public static Observable<DataEvent> listen(@NonNull Uri uri, int filterType) {
            return listenInternal(uri, filterType, null, null);
        }

        public static Observable<DataEvent> listen(@NonNull Uri uri, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(uri, filterType, timeout, timeUnit);
        }

        public static Observable<DataEvent> listen(@NonNull String path, int filterType) {
            return listenInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), filterType, null, null);
        }

        public static Observable<DataEvent> listen(@NonNull String path, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), filterType, timeout, timeUnit);
        }

        private static Observable<DataEvent> listenInternal(Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
            return Observable.create(new DataListenerObservable(RxWear.get(), uri, filterType, timeout, timeUnit));
        }

        // delete

        public static Single<Integer> delete(@NonNull Uri uri) {
            return deleteInternal(uri, null, null, null);
        }

        public static Single<Integer> delete(@NonNull Uri uri, @NonNull Long timeout, @NonNull TimeUnit timeUnit) {
            return deleteInternal(uri, null, timeout, timeUnit);
        }

        public static Single<Integer> delete(@NonNull Uri uri, int filterType) {
            return deleteInternal(uri, filterType, null, null);
        }

        public static Single<Integer> delete(@NonNull Uri uri, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return deleteInternal(uri, filterType, timeout, timeUnit);
        }

        private static Single<Integer> deleteInternal(Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
            return Single.create(new DataDeleteItemsSingle(RxWear.get(), uri, filterType, timeout, timeUnit));
        }

        // put

        public static Single<DataItem> put(@NonNull PutDataRequest putDataRequest) {
            return putInternal(putDataRequest, null, null);
        }

        public static Single<DataItem> put(@NonNull PutDataRequest putDataRequest, long timeout, @NonNull TimeUnit timeUnit) {
            return putInternal(putDataRequest, timeout, timeUnit);
        }

        public static Single<DataItem> put(@NonNull PutDataMapRequest putDataMapRequest) {
            return putInternal(putDataMapRequest.asPutDataRequest(), null, null);
        }

        public static Single<DataItem> put(@NonNull PutDataMapRequest putDataMapRequest, long timeout, @NonNull TimeUnit timeUnit) {
            return putInternal(putDataMapRequest.asPutDataRequest(), timeout, timeUnit);
        }

        private static Single<DataItem> putInternal(PutDataRequest putDataRequest, Long timeout, TimeUnit timeUnit) {
            return Single.create(new DataPutItemSingle(RxWear.get(), putDataRequest, timeout, timeUnit));
        }


        // getSingle

        @Deprecated
        // use get() instead
        public static Single<DataItem> getSingle(@NonNull Uri uri) {
            return getSingleInternal(uri, null, null);
        }

        @Deprecated
        // use get() instead
        public static Single<DataItem> getSingle(@NonNull Uri uri, long timeout, @NonNull TimeUnit timeUnit) {
            return getSingleInternal(uri, timeout, timeUnit);
        }

        private static Single<DataItem> getSingleInternal(Uri uri, Long timeout, TimeUnit timeUnit) {
            return Single.create(new DataGetItemSingle(RxWear.get(), uri, timeout, timeUnit));
        }

        // get

        public static Observable<DataItem> get(@NonNull Uri uri, int filterType) {
            return getInternal(uri, filterType, null, null);
        }

        public static Observable<DataItem> get(@NonNull Uri uri, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return getInternal(uri, filterType, timeout, timeUnit);
        }

        public static Observable<DataItem> get(@NonNull String path, int filterType) {
            return getInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), filterType, null, null);
        }

        public static Observable<DataItem> get(@NonNull String path, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return getInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), filterType, timeout, timeUnit);
        }

        public static Observable<DataItem> get(@NonNull Uri uri) {
            return getInternal(uri, null, null, null);
        }

        public static Observable<DataItem> get(@NonNull Uri uri, long timeout, @NonNull TimeUnit timeUnit) {
            return getInternal(uri, null, timeout, timeUnit);
        }

        public static Observable<DataItem> get(@NonNull String path) {
            return getInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), null, null, null);
        }

        public static Observable<DataItem> get(@NonNull String path, long timeout, @NonNull TimeUnit timeUnit) {
            return getInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), null, timeout, timeUnit);
        }

        public static Observable<DataItem> get() {
            return getInternal(null, null, null, null);
        }

        public static Observable<DataItem> get(long timeout, @NonNull TimeUnit timeUnit) {
            return getInternal(null, null, timeout, timeUnit);
        }

        private static Observable<DataItem> getInternal(Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
            return Observable.create(new DataGetItemsObservable(RxWear.get(), uri, filterType, timeout, timeUnit));
        }

        // getFdForAsset

        public static Single<DataApi.GetFdForAssetResult> getFdForAsset(@NonNull DataItemAsset dataItemAsset) {
            return getFdForAssetInternal(dataItemAsset, null, null, null);
        }

        public static Single<DataApi.GetFdForAssetResult> getFdForAsset(@NonNull DataItemAsset dataItemAsset, long timeout, @NonNull TimeUnit timeUnit) {
            return getFdForAssetInternal(dataItemAsset, null, timeout, timeUnit);
        }

        public static Single<DataApi.GetFdForAssetResult> getFdForAsset(@NonNull Asset asset) {
            return getFdForAssetInternal(null, asset, null, null);
        }

        public static Single<DataApi.GetFdForAssetResult> getFdForAsset(@NonNull Asset asset, long timeout, @NonNull TimeUnit timeUnit) {
            return getFdForAssetInternal(null, asset, timeout, timeUnit);
        }

        private static Single<DataApi.GetFdForAssetResult> getFdForAssetInternal(DataItemAsset dataItemAsset, Asset asset, Long timeout, TimeUnit timeUnit) {
            return Single.create(new DataGetFdForAssetSingle(RxWear.get(), dataItemAsset, asset, timeout, timeUnit));
        }

        /* A helper class with a fluent interface for putting a Serializable
         * based on a DataMap.
         *
         * Example:
         * RxWear.Data.PutSerializable.urgentTo("/path", serializable)
         *      .subscribe(dataItem -> {
         *          // do something
         *      });
         */
        public static class PutSerializable {

            private PutSerializable() { }

            public static Single<DataItem> withDataItem(DataItem dataItem, Serializable serializable) {
                return createSingle(PutDataRequest.createFromDataItem(dataItem), serializable);
            }

            public static Single<DataItem> urgentWithDataItem(DataItem dataItem, Serializable serializable) {
                return createSingle(PutDataRequest.createFromDataItem(dataItem).setUrgent(), serializable);
            }

            public static Single<DataItem> withAutoAppendedId(String pathPrefix, Serializable serializable) {
                return createSingle(PutDataRequest.createWithAutoAppendedId(pathPrefix), serializable);
            }

            public static Single<DataItem> urgentWithAutoAppendedId(String pathPrefix, Serializable serializable) {
                return createSingle(PutDataRequest.createWithAutoAppendedId(pathPrefix).setUrgent(), serializable);
            }

            public static Single<DataItem> to(String path, Serializable serializable) {
                return createSingle(PutDataRequest.create(path), serializable);
            }

            public static Single<DataItem> urgentTo(String path, Serializable serializable) {
                return createSingle(PutDataRequest.create(path).setUrgent(), serializable);
            }

            private static Single<DataItem> createSingle(PutDataRequest request, Serializable serializable) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    new ObjectOutputStream(out).writeObject(serializable);
                    request.setData(out.toByteArray());
                    return putInternal(request, null, null);
                } catch(IOException e) {
                    return Single.error(e);
                }
            }

        }

        /* A helper class with a fluent interface for putting a DataItem
         * based on a DataMap.
         *
         * Example:
         * RxWear.Data.PutDataMap.to("/path")
         *      .putString("key", "value")
         *      .putInt("key", 0)
         *      .toSingle()
         *      .subscribe(dataItem -> {
         *          // do something
         *      });
         */
        public static class PutDataMap {
            private final PutDataMapRequest request;

            private PutDataMap(String path, DataMapItem dataMapItem, String pathPrefix) {
                if(path != null) {
                    request = PutDataMapRequest.create(path);
                } else if(dataMapItem != null) {
                    request = PutDataMapRequest.createFromDataMapItem(dataMapItem);
                } else {
                    request = PutDataMapRequest.createWithAutoAppendedId(pathPrefix);
                }
            }

            public static PutDataMap withDataMapItem(DataMapItem source) {
                return new PutDataMap(null, source, null);
            }

            public static PutDataMap withAutoAppendedId(String pathPrefix) {
                return new PutDataMap(null, null, pathPrefix);
            }

            public static PutDataMap to(String path) {
                return new PutDataMap(path, null, null);
            }

            public PutDataMap setUrgent() {
                request.setUrgent();
                return this;
            }

            public PutDataMap putAll(DataMap dataMap) {
                request.getDataMap().putAll(dataMap);
                return this;
            }

            public PutDataMap putBoolean(String key, boolean value) {
                request.getDataMap().putBoolean(key, value);
                return this;
            }

            public PutDataMap putByte(String key, byte value) {
                request.getDataMap().putByte(key, value);
                return this;
            }

            public PutDataMap putInt(String key, int value) {
                request.getDataMap().putInt(key, value);
                return this;
            }

            public PutDataMap putLong(String key, long value) {
                request.getDataMap().putLong(key, value);
                return this;
            }

            public PutDataMap putFloat(String key, float value) {
                request.getDataMap().putFloat(key, value);
                return this;
            }

            public PutDataMap putDouble(String key, double value) {
                request.getDataMap().putDouble(key, value);
                return this;
            }

            public PutDataMap putString(String key, String value) {
                request.getDataMap().putString(key, value);
                return this;
            }

            public PutDataMap putAsset(String key, Asset value) {
                request.getDataMap().putAsset(key, value);
                return this;
            }

            public PutDataMap putDataMap(String key, DataMap value) {
                request.getDataMap().putDataMap(key, value);
                return this;
            }

            public PutDataMap putDataMapArrayList(String key, ArrayList<DataMap> value) {
                request.getDataMap().putDataMapArrayList(key, value);
                return this;
            }

            public PutDataMap putIntegerArrayList(String key, ArrayList<Integer> value) {
                request.getDataMap().putIntegerArrayList(key, value);
                return this;
            }

            public PutDataMap putStringArrayList(String key, ArrayList<String> value) {
                request.getDataMap().putStringArrayList(key, value);
                return this;
            }

            public PutDataMap putByteArray(String key, byte[] value) {
                request.getDataMap().putByteArray(key, value);
                return this;
            }

            public PutDataMap putLongArray(String key, long[] value) {
                request.getDataMap().putLongArray(key, value);
                return this;
            }

            public PutDataMap putFloatArray(String key, float[] value) {
                request.getDataMap().putFloatArray(key, value);
                return this;
            }

            public PutDataMap putStringArray(String key, String[] value) {
                request.getDataMap().putStringArray(key, value);
                return this;
            }

            public Single<DataItem> toSingle() {
                return putInternal(request.asPutDataRequest(), null, null);
            }

            public Observable<DataItem> toObservable() {
                return putInternal(request.asPutDataRequest(), null, null).toObservable();
            }
        }

    }

    public static class Message {

        private Message() { }

        // listen

        public static Observable<MessageEvent> listen() {
            return listenInternal(null, null, null, null);
        }

        public static Observable<MessageEvent> listen(@NonNull Long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(null, null, timeout, timeUnit);
        }

        public static Observable<MessageEvent> listen(@NonNull Uri uri, int filterType) {
            return listenInternal(uri, filterType, null, null);
        }

        public static Observable<MessageEvent> listen(@NonNull Uri uri, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(uri, filterType, timeout, timeUnit);
        }

        public static Observable<MessageEvent> listen(@NonNull String path, int filterType) {
            return listenInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), filterType, null, null);
        }

        public static Observable<MessageEvent> listen(@NonNull String path, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build(), filterType, timeout, timeUnit);
        }

        private static Observable<MessageEvent> listenInternal(Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
            return Observable.create(new MessageListenerObservable(RxWear.get(), uri, filterType, timeout, timeUnit));
        }

        // send

        public static Single<Integer> send(@NonNull String nodeId, @NonNull String path, @NonNull byte[] data) {
            return sendInternal(nodeId, path, data, null, null);
        }

        public static Single<Integer> send(@NonNull String nodeId, @NonNull String path, @NonNull byte[] data, long timeout, @NonNull TimeUnit timeUnit) {
            return sendInternal(nodeId, path, data, timeout, timeUnit);
        }

        private static Single<Integer> sendInternal(String nodeId, String path, byte[] data, Long timeout, TimeUnit timeUnit) {
            return Single.create(new MessageSendSingle(RxWear.get(), nodeId, path, data, timeout, timeUnit));
        }

        // sendToAllRemoteNodes

        public static Observable<Integer> sendToAllRemoteNodes(@NonNull final String path, @NonNull final byte[] data) {
            return sendToAllRemoteNodesInternal(path, data, null, null);
        }

        public static Observable<Integer> sendToAllRemoteNodes(@NonNull final String path, @NonNull final byte[] data, final long timeout, @NonNull final TimeUnit timeUnit) {
            return sendToAllRemoteNodesInternal(path, data, timeout, timeUnit);
        }

        private static Observable<Integer> sendToAllRemoteNodesInternal(final String path, final byte[] data, final Long timeout, final TimeUnit timeUnit) {
            return Node.getConnectedNodesInternal(timeout, timeUnit).flatMap(new Func1<com.google.android.gms.wearable.Node, Observable<Integer>>() {
                @Override
                public Observable<Integer> call(com.google.android.gms.wearable.Node node) {
                    return sendInternal(node.getId(), path, data, timeout, timeUnit).toObservable();
                }
            });
        }

        /* A helper class to send a Serializable in a message */
        public static class SendSerializable {

            private SendSerializable() { }

            public static Single<Integer> to(String nodeId, String path, Serializable serializable) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    new ObjectOutputStream(out).writeObject(serializable);
                    return sendInternal(nodeId, path, out.toByteArray(), null, null);
                } catch(Throwable throwable) {
                    return Single.error(throwable);
                }
            }

            public static Observable<Integer> toAllRemoteNodes(String path, Serializable serializable) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    new ObjectOutputStream(out).writeObject(serializable);
                    return sendToAllRemoteNodesInternal(path, out.toByteArray(), null, null);
                } catch(Throwable throwable) {
                    return Observable.error(throwable);
                }
            }
        }

        /* A helper class with a fluent interface for putting a DataItem
         * based on a DataMap.
         *
         * Example:
         * RxWear.Message.SendDataMap.to(nodeId, "/path")
         *      .putString("key", "value")
         *      .putInt("key", 0)
         *      .toSingle()
         *      .subscribe(requestId -> {
         *          // do something
         *      });
         */
        public static class SendDataMap {
            private final String nodeId;
            private final String path;
            private final DataMap dataMap = new DataMap();
            private final boolean toAllRemoteNodes;

            private SendDataMap(String nodeId, String path, boolean toAllRemoteNodes) {
                this.nodeId = nodeId;
                this.path = path;
                this.toAllRemoteNodes = toAllRemoteNodes;
            }

            public static SendDataMap to(String nodeId, String path) {
                return new SendDataMap(nodeId, path, false);
            }

            public static SendDataMap toAllRemoteNodes(String path) {
                return new SendDataMap(null, path, true);
            }

            public SendDataMap putAll(DataMap dataMap) {
                dataMap.putAll(dataMap);
                return this;
            }

            public SendDataMap putBoolean(String key, boolean value) {
                dataMap.putBoolean(key, value);
                return this;
            }

            public SendDataMap putByte(String key, byte value) {
                dataMap.putByte(key, value);
                return this;
            }

            public SendDataMap putInt(String key, int value) {
                dataMap.putInt(key, value);
                return this;
            }

            public SendDataMap putLong(String key, long value) {
                dataMap.putLong(key, value);
                return this;
            }

            public SendDataMap putFloat(String key, float value) {
                dataMap.putFloat(key, value);
                return this;
            }

            public SendDataMap putDouble(String key, double value) {
                dataMap.putDouble(key, value);
                return this;
            }

            public SendDataMap putString(String key, String value) {
                dataMap.putString(key, value);
                return this;
            }

            public SendDataMap putAsset(String key, Asset value) {
                dataMap.putAsset(key, value);
                return this;
            }

            public SendDataMap putDataMap(String key, DataMap value) {
                dataMap.putDataMap(key, value);
                return this;
            }

            public SendDataMap putDataMapArrayList(String key, ArrayList<DataMap> value) {
                dataMap.putDataMapArrayList(key, value);
                return this;
            }

            public SendDataMap putIntegerArrayList(String key, ArrayList<Integer> value) {
                dataMap.putIntegerArrayList(key, value);
                return this;
            }

            public SendDataMap putStringArrayList(String key, ArrayList<String> value) {
                dataMap.putStringArrayList(key, value);
                return this;
            }

            public SendDataMap putByteArray(String key, byte[] value) {
                dataMap.putByteArray(key, value);
                return this;
            }

            public SendDataMap putLongArray(String key, long[] value) {
                dataMap.putLongArray(key, value);
                return this;
            }

            public SendDataMap putFloatArray(String key, float[] value) {
                dataMap.putFloatArray(key, value);
                return this;
            }

            public SendDataMap putStringArray(String key, String[] value) {
                dataMap.putStringArray(key, value);
                return this;
            }

            public Observable<Integer> toObservable() {
                if(toAllRemoteNodes) {
                    return Message.sendToAllRemoteNodesInternal(path, dataMap.toByteArray(), null, null);
                } else {
                    return Message.sendInternal(nodeId, path, dataMap.toByteArray(), null, null).toObservable();
                }
            }

            /* This should only be used with to(). If used with
             * toAllRemoteNodes(), an Exception will be thrown
             * if more than one item is emitted (i.e. more than one
             * node is connected).
             */
            public Single<Integer> toSingle() {
                if(toAllRemoteNodes) {
                    return Single.error(new UnsupportedOperationException("toSingle() can not be used with toAllRemoteNodes()"));
                } else {
                    return Message.sendInternal(nodeId, path, dataMap.toByteArray(), null, null);
                }
            }
        }

    }


    public static class Node {

        private Node() { }

        // listen

        @Deprecated
        public static Observable<NodeEvent> listen() {
            return listenInternal(null, null);
        }

        @Deprecated
        public static Observable<NodeEvent> listen(long timeout, @NonNull TimeUnit timeUnit) {
            return listenInternal(timeout, timeUnit);
        }

        private static Observable<NodeEvent> listenInternal(Long timeout, TimeUnit timeUnit) {
            return Observable.create(new NodeListenerObservable(RxWear.get(), timeout, timeUnit));
        }

        // getConnectedNodes

        public static Observable<com.google.android.gms.wearable.Node> getConnectedNodes() {
            return getConnectedNodesInternal(null, null);
        }

        public static Observable<com.google.android.gms.wearable.Node> getConnectedNodes(long timeout, @NonNull TimeUnit timeUnit) {
            return getConnectedNodesInternal(timeout, timeUnit);
        }

        private static Observable<com.google.android.gms.wearable.Node> getConnectedNodesInternal(Long timeout, TimeUnit timeUnit) {
            return Single.create(new NodeGetConnectedSingle(RxWear.get(), timeout, timeUnit)).flatMapObservable(new Func1<List<com.google.android.gms.wearable.Node>, Observable<com.google.android.gms.wearable.Node>>() {
                @Override
                public Observable<com.google.android.gms.wearable.Node> call(List<com.google.android.gms.wearable.Node> nodes) {
                    return Observable.from(nodes);
                }
            });
        }

        // getLocalNode

        public static Single<com.google.android.gms.wearable.Node> getLocalNode() {
            return getLocalNodeInternal(null, null);
        }

        public static Single<com.google.android.gms.wearable.Node> getLocalNode(long timeout, @NonNull TimeUnit timeUnit) {
            return getLocalNodeInternal(timeout, timeUnit);
        }

        private static Single<com.google.android.gms.wearable.Node> getLocalNodeInternal(Long timeout, TimeUnit timeUnit) {
            return Single.create(new NodeGetLocalSingle(RxWear.get(), timeout, timeUnit));
        }

    }
}
