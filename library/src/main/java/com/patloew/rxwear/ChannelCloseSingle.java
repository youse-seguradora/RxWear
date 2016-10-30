package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Channel;

import java.util.concurrent.TimeUnit;

import rx.SingleSubscriber;

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
class ChannelCloseSingle extends BaseSingle<Status> {

    final Channel channel;
    final Integer errorCode;

    ChannelCloseSingle(RxWear rxWear, Channel channel, Integer errorCode, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.channel = channel;
        this.errorCode = errorCode;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleSubscriber<? super Status> subscriber) {
        ResultCallback<Status> resultCallBack = SingleResultCallBack.get(subscriber);

        if(errorCode != null) {
            setupWearPendingResult(channel.close(apiClient, errorCode), resultCallBack);
        } else {
            setupWearPendingResult(channel.close(apiClient), resultCallBack);
        }
    }
}
