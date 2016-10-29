package com.patloew.rxwear;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.patloew.rxwear.events.ChannelEvent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;

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
public class Channel {

    private final RxWear rxWear;

    Channel(RxWear rxWear) {
        this.rxWear = rxWear;
    }

    // listen

    public Observable<ChannelEvent> listen() {
        return listenInternal(null, null, null);
    }

    public Observable<ChannelEvent> listen(long timeout, @NonNull TimeUnit timeUnit) {
        return listenInternal(null, timeout, timeUnit);
    }

    public Observable<ChannelEvent> listen(@NonNull com.google.android.gms.wearable.Channel channel) {
        return listenInternal(channel, null, null);
    }

    public Observable<ChannelEvent> listen(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
        return listenInternal(channel, timeout, timeUnit);
    }

    private Observable<ChannelEvent> listenInternal(com.google.android.gms.wearable.Channel channel, Long timeout, TimeUnit timeUnit) {
        return Observable.create(new ChannelListenerObservable(rxWear, channel, timeout, timeUnit));
    }

    // close

    public Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel) {
        return closeInternal(channel, null, null, null);
    }

    public Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
        return closeInternal(channel, null, timeout, timeUnit);
    }

    public Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel, int errorCode) {
        return closeInternal(channel, errorCode, null, null);
    }

    public Single<Status> close(@NonNull com.google.android.gms.wearable.Channel channel, int errorCode, long timeout, @NonNull TimeUnit timeUnit) {
        return closeInternal(channel, errorCode, timeout, timeUnit);
    }

    private Single<Status> closeInternal(com.google.android.gms.wearable.Channel channel, Integer errorCode, Long timeout, TimeUnit timeUnit) {
        return Single.create(new ChannelCloseSingle(rxWear, channel, errorCode, timeout, timeUnit));
    }

    // sendFile

    public Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri) {
        return sendFileInternal(channel, uri, null, null, null, null);
    }

    public Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, long timeout, @NonNull TimeUnit timeUnit) {
        return sendFileInternal(channel, uri, null, null, timeout, timeUnit);
    }

    public Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, long startOffset, long length) {
        return sendFileInternal(channel, uri, startOffset, length, null, null);
    }

    public Single<Status> sendFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, long startOffset, long length, long timeout, @NonNull TimeUnit timeUnit) {
        return sendFileInternal(channel, uri, startOffset, length, timeout, timeUnit);
    }

    private Single<Status> sendFileInternal(com.google.android.gms.wearable.Channel channel, Uri uri, Long startOffset, Long length, Long timeout, TimeUnit timeUnit) {
        return Single.create(new ChannelSendFileSingle(rxWear, channel, uri, startOffset, length, timeout, timeUnit));
    }

    // receiveFile

    public Single<Status> receiveFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, boolean append) {
        return receiveFileInternal(channel, uri, append, null, null);
    }

    public Single<Status> receiveFile(@NonNull com.google.android.gms.wearable.Channel channel, @NonNull Uri uri, boolean append, long timeout, @NonNull TimeUnit timeUnit) {
        return receiveFileInternal(channel, uri, append, timeout, timeUnit);
    }

    private Single<Status> receiveFileInternal(com.google.android.gms.wearable.Channel channel, Uri uri, boolean append, Long timeout, TimeUnit timeUnit) {
        return Single.create(new ChannelReceiveFileSingle(rxWear, channel, uri, append, timeout, timeUnit));
    }

    // getInputStream

    public Single<InputStream> getInputStream(@NonNull com.google.android.gms.wearable.Channel channel) {
        return getInputStreamInternal(channel, null, null);
    }

    public Single<InputStream> getInputStream(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
        return getInputStreamInternal(channel, timeout, timeUnit);
    }

    private Single<InputStream> getInputStreamInternal(com.google.android.gms.wearable.Channel channel, Long timeout, TimeUnit timeUnit) {
        return Single.create(new ChannelGetInputStreamSingle(rxWear, channel, timeout, timeUnit));
    }

    // getOutputStream

    public Single<OutputStream> getOutputStream(@NonNull com.google.android.gms.wearable.Channel channel) {
        return getOutputStreamInternal(channel, null, null);
    }

    public Single<OutputStream> getOutputStream(@NonNull com.google.android.gms.wearable.Channel channel, long timeout, @NonNull TimeUnit timeUnit) {
        return getOutputStreamInternal(channel, timeout, timeUnit);
    }

    private Single<OutputStream> getOutputStreamInternal(com.google.android.gms.wearable.Channel channel, Long timeout, TimeUnit timeUnit) {
        return Single.create(new ChannelGetOutputStreamSingle(rxWear, channel, timeout, timeUnit));
    }

    // open

    public Single<com.google.android.gms.wearable.Channel> open(@NonNull String nodeId, @NonNull String path) {
        return openInternal(nodeId, path, null, null);
    }

    public Single<com.google.android.gms.wearable.Channel> open(@NonNull String nodeId, @NonNull String path, long timeout, @NonNull TimeUnit timeUnit) {
        return openInternal(nodeId, path, timeout, timeUnit);
    }

    private Single<com.google.android.gms.wearable.Channel> openInternal(String nodeId, String path, Long timeout, TimeUnit timeUnit) {
        return Single.create(new ChannelOpenSingle(rxWear, nodeId, path, timeout, timeUnit));
    }
}
