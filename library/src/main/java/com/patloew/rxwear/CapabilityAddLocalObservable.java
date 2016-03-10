package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
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
public class CapabilityAddLocalObservable extends BaseObservable<Status> {

    private final String capability;

    CapabilityAddLocalObservable(RxWear rxWear, String capability, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.capability = capability;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Observer<? super Status> observer) {
        setupWearPendingResult(Wearable.CapabilityApi.addLocalCapability(apiClient, capability), new ResultCallback<CapabilityApi.AddLocalCapabilityResult>() {
            @Override
            public void onResult(@NonNull CapabilityApi.AddLocalCapabilityResult addLocalCapabilityResult) {
                if (!addLocalCapabilityResult.getStatus().isSuccess()) {
                    observer.onError(new StatusException(addLocalCapabilityResult.getStatus()));
                } else {
                    observer.onNext(addLocalCapabilityResult.getStatus());
                    observer.onCompleted();
                }
            }
        });
    }
}
