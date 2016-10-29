package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.patloew.rxwear.events.NodeEvent;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;

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
class NodeListenerObservable extends BaseObservable<NodeEvent> {


    private NodeApi.NodeListener listener;

    NodeListenerObservable(RxWear rxWear, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Subscriber<? super NodeEvent> subscriber) {
        listener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                subscriber.onNext(new NodeEvent(node, true));
            }

            @Override
            public void onPeerDisconnected(Node node) {
                subscriber.onNext(new NodeEvent(node, false));
            }
        };

        ResultCallback<Status> resultCallback = new StatusErrorResultCallBack(subscriber);

        setupWearPendingResult(Wearable.NodeApi.addListener(apiClient, listener), resultCallback);
    }


    @Override
    protected void onUnsubscribed(GoogleApiClient apiClient) {
        Wearable.NodeApi.removeListener(apiClient, listener);
    }
}
