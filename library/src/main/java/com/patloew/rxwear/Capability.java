package com.patloew.rxwear;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityInfo;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;

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
public class Capability {

    private final RxWear rxWear;

    Capability(RxWear rxWear) {
        this.rxWear = rxWear;
    }

    // listen

    public Observable<CapabilityInfo> listen(@NonNull String capability) {
        return listenInternal(capability, null, null, null, null);
    }

    public Observable<CapabilityInfo> listen(@NonNull String capability, long timeout, @NonNull TimeUnit timeUnit) {
        return listenInternal(capability, null, null, timeout, timeUnit);
    }

    public Observable<CapabilityInfo> listen(@NonNull Uri uri, int filterType) {
        return listenInternal(null, uri, filterType, null, null);
    }

    public Observable<CapabilityInfo> listen(@NonNull Uri uri, int filterType, long timeout, @NonNull TimeUnit timeUnit) {
        return listenInternal(null, uri, filterType, timeout, timeUnit);
    }

    private Observable<CapabilityInfo> listenInternal(String capability, Uri uri, Integer filterType, Long timeout, TimeUnit timeUnit) {
        return Observable.create(new CapabilityListenerObservable(rxWear, capability, uri, filterType, timeout, timeUnit));
    }

    // getAll

    public Observable<CapabilityInfo> getAll(int nodeFilter) {
        return getAllInternal(nodeFilter, null, null);
    }

    public Observable<CapabilityInfo> getAll(int nodeFilter, long timeout, @NonNull TimeUnit timeUnit) {
        return getAllInternal(nodeFilter, timeout, timeUnit);
    }

    private Observable<CapabilityInfo> getAllInternal(int nodeFilter, Long timeout, TimeUnit timeUnit) {
        return Single.create(new CapabilityGetAllSingle(rxWear, nodeFilter, timeout, timeUnit))
                .flatMapObservable(capabilityInfoMap -> Observable.from(capabilityInfoMap.values()));
    }

    // get

    public Single<CapabilityInfo> get(@NonNull String capability, int nodeFilter) {
        return getInternal(capability, nodeFilter, null, null);
    }

    public Single<CapabilityInfo> get(@NonNull String capability, int nodeFilter, long timeout, @NonNull TimeUnit timeUnit) {
        return getInternal(capability, nodeFilter, timeout, timeUnit);
    }

    private Single<CapabilityInfo> getInternal(String capability, int nodeFilter, Long timeout, TimeUnit timeUnit) {
        return Single.create(new CapabilityGetSingle(rxWear, capability, nodeFilter, timeout, timeUnit));
    }

    // addLocal

    public Single<Status> addLocal(@NonNull String capability) {
        return addLocalInternal(capability, null, null);
    }

    public Single<Status> addLocal(@NonNull String capability, long timeout, @NonNull TimeUnit timeUnit) {
        return addLocalInternal(capability, timeout, timeUnit);
    }

    private Single<Status> addLocalInternal(String capability, Long timeout, TimeUnit timeUnit) {
        return Single.create(new CapabilityAddLocalSingle(rxWear, capability, timeout, timeUnit));
    }

    // removeLocal

    public Single<Status> removeLocal(@NonNull String capability) {
        return removeLocalInternal(capability, null, null);
    }

    public Single<Status> removeLocal(@NonNull String capability, long timeout, @NonNull TimeUnit timeUnit) {
        return removeLocalInternal(capability, timeout, timeUnit);
    }

    private Single<Status> removeLocalInternal(String capability, Long timeout, TimeUnit timeUnit) {
        return Single.create(new CapabilityRemoveLocalSingle(rxWear, capability, timeout, timeUnit));
    }
}
