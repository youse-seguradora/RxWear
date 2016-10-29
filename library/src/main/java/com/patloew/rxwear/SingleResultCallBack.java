package com.patloew.rxwear;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

import rx.SingleSubscriber;
import rx.functions.Func1;

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
class SingleResultCallBack<T extends Result, R> implements ResultCallback<T> {

    private final SingleSubscriber<? super R> subscriber;
    private final Func1<T, R> mapper;

    static <T extends Result, R> ResultCallback<T> get(@NonNull SingleSubscriber<? super R> subscriber, @NonNull Func1<T, R> mapper) {
        return new SingleResultCallBack<>(subscriber, mapper);
    }

    static <T extends Result> ResultCallback<T> get(@NonNull SingleSubscriber<? super T> subscriber) {
        return new SingleResultCallBack<>(subscriber, input -> input);
    }

    private SingleResultCallBack(@NonNull SingleSubscriber<? super R> subscriber, @NonNull Func1<T, R> mapper) {
        this.subscriber = subscriber;
        this.mapper = mapper;
    }

    @Override
    public void onResult(@NonNull T result) {
        if (!result.getStatus().isSuccess()) {
            subscriber.onError(new StatusException(result.getStatus()));
        } else {
            subscriber.onSuccess(mapper.call(result));
        }
    }
}
