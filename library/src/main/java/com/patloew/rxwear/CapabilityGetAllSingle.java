package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Wearable;

import java.util.Map;
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
class CapabilityGetAllSingle extends BaseSingle<Map<String, CapabilityInfo>> {

    final int nodeFilter;

    CapabilityGetAllSingle(RxWear rxWear, int nodeFilter, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.nodeFilter = nodeFilter;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleSubscriber<? super Map<String, CapabilityInfo>> subscriber) {
        setupWearPendingResult(
                Wearable.CapabilityApi.getAllCapabilities(apiClient, nodeFilter),
                SingleResultCallBack.get(subscriber, CapabilityApi.GetAllCapabilitiesResult::getAllCapabilities)
        );
    }
}
