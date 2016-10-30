package com.patloew.rxwear;

import android.net.Uri;

import com.google.android.gms.common.api.GoogleApiClient;
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
class ChannelReceiveFileSingle extends BaseSingle<Status> {

    final Channel channel;
    final Uri uri;
    final boolean append;

    ChannelReceiveFileSingle(RxWear rxWear, Channel channel, Uri uri, boolean append, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.channel = channel;
        this.uri = uri;
        this.append = append;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleSubscriber<? super Status> subscriber) {
        setupWearPendingResult(
                channel.receiveFile(apiClient, uri, append),
                SingleResultCallBack.get(subscriber)
        );
    }
}
