package com.patloew.rxwear;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.SingleSubscriber;
import rx.subscriptions.Subscriptions;

/* Copyright (C) 2015 Michał Charmas (http://blog.charmas.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ---------------
 *
 * FILE MODIFIED by Patrick Löwenstein, 2016
 *
 */
abstract class BaseSingle<T> extends BaseRx<T> implements Single.OnSubscribe<T> {

    protected BaseSingle(@NonNull RxWear rxWear, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
    }

    protected BaseSingle(@NonNull Context ctx, @NonNull Api<? extends Api.ApiOptions.NotRequiredOptions>[] services, Scope[] scopes) {
        super(ctx, services, scopes);
    }

    @Override
    public final void call(SingleSubscriber<? super T> subscriber) {
        final GoogleApiClient apiClient = createApiClient(new ApiClientConnectionCallbacks(subscriber));

        try {
            apiClient.connect();
        } catch (Throwable ex) {
            subscriber.onError(ex);
        }

        subscriber.add(Subscriptions.create(() -> {
            if (apiClient.isConnected() || apiClient.isConnecting()) {
                onUnsubscribed(apiClient);
                apiClient.disconnect();
            }
        }));
    }

    protected abstract void onGoogleApiClientReady(GoogleApiClient apiClient, SingleSubscriber<? super T> subscriber);

    protected class ApiClientConnectionCallbacks extends BaseRx.ApiClientConnectionCallbacks {

        final protected SingleSubscriber<? super T> subscriber;

        private ApiClientConnectionCallbacks(SingleSubscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onConnected(Bundle bundle) {
            try {
                onGoogleApiClientReady(apiClient, subscriber);
            } catch (Throwable ex) {
                subscriber.onError(ex);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            subscriber.onError(new GoogleAPIConnectionSuspendedException(cause));
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            subscriber.onError(new GoogleAPIConnectionException("Error connecting to GoogleApiClient.", connectionResult));
        }
    }
}
