package com.patloew.rxwear;

import android.net.Uri;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Wearable;

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
class CapabilityListenerObservable extends BaseObservable<CapabilityInfo> {

    final String capability;
    final Uri uri;
    final Integer filterType;

    private CapabilityApi.CapabilityListener listener;

    CapabilityListenerObservable(RxWear rxWear, String capability, Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.capability = capability;
        this.uri = uri;
        this.filterType = filterType;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Subscriber<? super CapabilityInfo> subscriber) {
        listener = subscriber::onNext;

        ResultCallback<Status> resultCallback = new StatusErrorResultCallBack(subscriber);

        if(capability != null) {
            setupWearPendingResult(Wearable.CapabilityApi.addCapabilityListener(apiClient, listener, capability), resultCallback);
        } else {
            setupWearPendingResult(Wearable.CapabilityApi.addListener(apiClient, listener, uri, filterType), resultCallback);
        }
    }


    @Override
    protected void onUnsubscribed(GoogleApiClient apiClient) {
        if(capability != null) {
            Wearable.CapabilityApi.removeCapabilityListener(apiClient, listener, capability);
        } else {
            Wearable.CapabilityApi.removeListener(apiClient, listener);
        }
    }
}
