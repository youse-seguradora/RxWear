package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleEmitter;

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
class ChannelOpenSingle extends BaseSingle<Channel> {

    final String nodeId;
    final String path;

    ChannelOpenSingle(RxWear rxWear, String nodeId, String path, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.nodeId = nodeId;
        this.path = path;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleEmitter<Channel> emitter) {
        setupWearPendingResult(
                Wearable.ChannelApi.openChannel(apiClient, nodeId, path),
                openChannelResult -> {
                    if (!openChannelResult.getStatus().isSuccess()) {
                        emitter.onError(new StatusException(openChannelResult.getStatus()));
                    } else {
                        if(openChannelResult.getChannel() != null) {
                            emitter.onSuccess(openChannelResult.getChannel());
                        } else {
                            emitter.onError(new IOException("Channel connection could not be opened"));
                        }
                    }
                }
        );
    }
}
