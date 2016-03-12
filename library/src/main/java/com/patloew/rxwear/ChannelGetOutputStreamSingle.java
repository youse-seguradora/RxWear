package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Channel;

import java.io.OutputStream;
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
public class ChannelGetOutputStreamSingle extends BaseSingle<OutputStream> {

    private final Channel channel;

    ChannelGetOutputStreamSingle(RxWear rxWear, Channel channel, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.channel = channel;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleSubscriber<? super OutputStream> subscriber) {
        setupWearPendingResult(channel.getOutputStream(apiClient), new ResultCallback<Channel.GetOutputStreamResult>() {
            @Override
            public void onResult(@NonNull Channel.GetOutputStreamResult getOutputStreamResult) {
                if (!getOutputStreamResult.getStatus().isSuccess()) {
                    subscriber.onError(new StatusException(getOutputStreamResult.getStatus()));
                } else {
                    subscriber.onSuccess(getOutputStreamResult.getOutputStream());
                }
            }
        });
    }
}
