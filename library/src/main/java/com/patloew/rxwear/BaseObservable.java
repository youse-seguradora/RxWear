package com.patloew.rxwear;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

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
abstract class BaseObservable<T> extends BaseRx<T> implements ObservableOnSubscribe<T> {

    protected BaseObservable(@NonNull RxWear rxWear, Long timeout, TimeUnit timeUnit) {
        super(rxWear, timeout, timeUnit);
    }

    @Override
    public final void subscribe(ObservableEmitter<T> emitter) {
        final GoogleApiClient apiClient = createApiClient(new ApiClientConnectionCallbacks(emitter));

        try {
            apiClient.connect();
        } catch (Throwable ex) {
            emitter.onError(ex);
        }

        emitter.setCancellable(() -> {
            if (apiClient.isConnected() || apiClient.isConnecting()) {
                onUnsubscribed(apiClient);
                apiClient.disconnect();
            }
        });
    }

    protected abstract void onGoogleApiClientReady(GoogleApiClient apiClient, ObservableEmitter<T> emitter);

    protected class ApiClientConnectionCallbacks extends BaseRx.ApiClientConnectionCallbacks {

        final protected ObservableEmitter<T> emitter;

        private ApiClientConnectionCallbacks(ObservableEmitter<T> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onConnected(Bundle bundle) {
            try {
                onGoogleApiClientReady(apiClient, emitter);
            } catch (Throwable ex) {
                emitter.onError(ex);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            emitter.onError(new GoogleAPIConnectionSuspendedException(cause));
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            emitter.onError(new GoogleAPIConnectionException("Error connecting to GoogleApiClient.", connectionResult));
        }
    }
}
