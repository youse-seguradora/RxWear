package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItemAsset;
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
public class DataGetFdForAssetObservable extends BaseObservable<DataApi.GetFdForAssetResult> {

    private final DataItemAsset dataItemAsset;
    private final Asset asset;

    DataGetFdForAssetObservable(RxWear rxWear, DataItemAsset dataItemAsset, Asset asset, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.dataItemAsset = dataItemAsset;
        this.asset = asset;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Observer<? super DataApi.GetFdForAssetResult> observer) {
        ResultCallback<DataApi.GetFdForAssetResult> resultCallback = new ResultCallback<DataApi.GetFdForAssetResult>() {
            @Override
            public void onResult(@NonNull DataApi.GetFdForAssetResult getFdForAssetResult) {
                if (!getFdForAssetResult.getStatus().isSuccess()) {
                    observer.onError(new StatusException(getFdForAssetResult.getStatus()));
                } else {
                    observer.onNext(getFdForAssetResult);
                    observer.onCompleted();
                }
            }
        };

        if(asset != null) {
            setupWearPendingResult(Wearable.DataApi.getFdForAsset(apiClient, asset), resultCallback);
        } else {
            setupWearPendingResult(Wearable.DataApi.getFdForAsset(apiClient, dataItemAsset), resultCallback);
        }
    }
}
