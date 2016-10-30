package com.patloew.rxwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataRequest;
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
class DataPutItemSingle extends BaseSingle<DataItem> {

    final PutDataRequest putDataRequest;

    DataPutItemSingle(RxWear rxWear, PutDataRequest putDataRequest, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
        this.putDataRequest = putDataRequest;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final SingleSubscriber<? super DataItem> subscriber) {
        setupWearPendingResult(
                Wearable.DataApi.putDataItem(apiClient, putDataRequest),
                SingleResultCallBack.get(subscriber, DataApi.DataItemResult::getDataItem)
        );
    }
}
