package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.Wearable;
import com.patloew.rxwear.events.ChannelClosedEvent;
import com.patloew.rxwear.events.ChannelEvent;
import com.patloew.rxwear.events.ChannelOpenedEvent;
import com.patloew.rxwear.events.InputClosedEvent;
import com.patloew.rxwear.events.OutputClosedEvent;

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
class ChannelListenerObservable extends BaseObservable<ChannelEvent> {

    final Channel channel;
    private ChannelApi.ChannelListener listener;

    ChannelListenerObservable(RxWear rxWear, Channel channel, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.channel = channel;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Subscriber<? super ChannelEvent> subscriber) {
        listener = new ChannelApi.ChannelListener() {
            @Override public void onChannelOpened(Channel channel) {
                subscriber.onNext(new ChannelOpenedEvent(channel));
            }

            @Override public void onChannelClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
                subscriber.onNext(new ChannelClosedEvent(channel, closeReason, appSpecificErrorCode));
            }

            @Override public void onInputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
                subscriber.onNext(new InputClosedEvent(channel, closeReason, appSpecificErrorCode));
            }

            @Override public void onOutputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
                subscriber.onNext(new OutputClosedEvent(channel, closeReason, appSpecificErrorCode));
            }
        };

        ResultCallback<Status> resultCallback = new StatusErrorResultCallBack(subscriber);

        if(channel != null) {
            setupWearPendingResult(channel.addListener(apiClient, listener), resultCallback);
        } else {
            setupWearPendingResult(Wearable.ChannelApi.addListener(apiClient, listener), resultCallback);
        }
    }


    @Override
    protected void onUnsubscribed(GoogleApiClient apiClient) {
        if(channel != null) {
            channel.removeListener(apiClient, listener);
        } else {
            Wearable.ChannelApi.removeListener(apiClient, listener);
        }
    }
}
