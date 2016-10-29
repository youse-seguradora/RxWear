package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.patloew.rxwear.events.NodeEvent;

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
public class Node {

    private final RxWear rxWear;

    Node(RxWear rxWear) {
        this.rxWear = rxWear;
    }

    // listen

    @Deprecated
    public Observable<NodeEvent> listen() {
        return listenInternal(null, null);
    }

    @Deprecated
    public Observable<NodeEvent> listen(long timeout, @NonNull TimeUnit timeUnit) {
        return listenInternal(timeout, timeUnit);
    }

    private Observable<NodeEvent> listenInternal(Long timeout, TimeUnit timeUnit) {
        return Observable.create(new NodeListenerObservable(rxWear, timeout, timeUnit));
    }

    // getConnectedNodes

    public Observable<com.google.android.gms.wearable.Node> getConnectedNodes() {
        return getConnectedNodesInternal(null, null);
    }

    public Observable<com.google.android.gms.wearable.Node> getConnectedNodes(long timeout, @NonNull TimeUnit timeUnit) {
        return getConnectedNodesInternal(timeout, timeUnit);
    }

    Observable<com.google.android.gms.wearable.Node> getConnectedNodesInternal(Long timeout, TimeUnit timeUnit) {
        return Single.create(new NodeGetConnectedSingle(rxWear, timeout, timeUnit))
                .flatMapObservable(Observable::from);
    }

    // getLocalNode

    public Single<com.google.android.gms.wearable.Node> getLocalNode() {
        return getLocalNodeInternal(null, null);
    }

    public Single<com.google.android.gms.wearable.Node> getLocalNode(long timeout, @NonNull TimeUnit timeUnit) {
        return getLocalNodeInternal(timeout, timeUnit);
    }

    private Single<com.google.android.gms.wearable.Node> getLocalNodeInternal(Long timeout, TimeUnit timeUnit) {
        return Single.create(new NodeGetLocalSingle(rxWear, timeout, timeUnit));
    }

}
