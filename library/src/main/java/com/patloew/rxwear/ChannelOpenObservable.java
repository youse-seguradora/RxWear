package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
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
public class ChannelOpenObservable extends BaseObservable<Channel> {

    private final String nodeId;
    private final String path;

    ChannelOpenObservable(RxWear rxWear,  String nodeId, String path, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.nodeId = nodeId;
        this.path = path;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Observer<? super Channel> observer) {
        setupWearPendingResult(Wearable.ChannelApi.openChannel(apiClient, nodeId, path), new ResultCallback<ChannelApi.OpenChannelResult>() {
            @Override
            public void onResult(@NonNull ChannelApi.OpenChannelResult openChannelResult) {
                if (!openChannelResult.getStatus().isSuccess()) {
                    observer.onError(new StatusException(openChannelResult.getStatus()));
                } else {
                    if(openChannelResult.getChannel() != null) {
                        observer.onNext(openChannelResult.getChannel());
                    }

                    observer.onCompleted();
                }
            }
        });
    }
}
