package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

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
public class NodeGetLocalSingle extends BaseSingle<Node> {

    NodeGetLocalSingle(RxWear rxWear, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleSubscriber<? super Node> subscriber) {
        setupWearPendingResult(Wearable.NodeApi.getLocalNode(apiClient), new ResultCallback<NodeApi.GetLocalNodeResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetLocalNodeResult getLocalNodeResult) {
                if (!getLocalNodeResult.getStatus().isSuccess()) {
                    subscriber.onError(new StatusException(getLocalNodeResult.getStatus()));
                } else {
                    subscriber.onSuccess(getLocalNodeResult.getNode());
                }
            }
        });

    }
}
