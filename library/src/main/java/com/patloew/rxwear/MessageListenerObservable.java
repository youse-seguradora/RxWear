package com.patloew.rxwear;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

import rx.Observer;

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
public class MessageListenerObservable extends BaseObservable<MessageEvent> {

    private final Uri uri;
    private final Integer filterType;

    private MessageApi.MessageListener listener;

    MessageListenerObservable(RxWear rxWear, Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.uri = uri;
        this.filterType = filterType;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Observer<? super MessageEvent> observer) {
        listener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                observer.onNext(messageEvent);
            }
        };

        ResultCallback<Status> resultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (!status.isSuccess()) {
                    observer.onError(new StatusException(status));
                }
            }
        };

        if(uri != null) {
            setupWearPendingResult(Wearable.MessageApi.addListener(apiClient, listener, uri, filterType), resultCallback);
        } else {
            setupWearPendingResult(Wearable.MessageApi.addListener(apiClient, listener), resultCallback);
        }
    }


    @Override
    protected void onUnsubscribed(GoogleApiClient apiClient) {
        Wearable.MessageApi.removeListener(apiClient, listener);
    }
}
