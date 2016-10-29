package com.patloew.rxwear;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

import rx.Completable;
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
 * limitations under the License.
 */
public class RxWear {

    Long timeoutTime = null;
    TimeUnit timeoutUnit = null;

    final Context ctx;

    private final Capability capability = new Capability(this);
    private final Channel channel = new Channel(this);
    private final Data data = new Data(this);
    private final Message message = new Message(this);
    private final Node node = new Node(this);


    public RxWear(@NonNull Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    /* Set a default timeout for all requests to the Wearable API made in the lib.
     * When a timeout occurs, onError() is called with a StatusException.
     */
    public void setDefaultTimeout(long time, @NonNull TimeUnit timeUnit) {
        if(timeUnit != null) {
            timeoutTime = time;
            timeoutUnit = timeUnit;
        } else {
            throw new IllegalArgumentException("timeUnit parameter must not be null");
        }
    }

    /* Reset the default timeout.
     */
    public void resetDefaultTimeout() {
        timeoutTime = null;
        timeoutUnit = null;
    }


    /* Can be used to check whether connection to Wearable API was successful.
     *
     * This Completable completes if the connection was successful.
     */
    public Completable checkConnection() {
        return Completable.fromSingle(getWearableClient());
    }

    public Single<GoogleApiClient> getWearableClient() {
        return GoogleAPIClientSingle.create(ctx, Wearable.API);
    }


    public Capability capability() {
        return capability;
    }

    public Channel channel() {
        return channel;
    }

    public Data data() {
        return data;
    }

    public Message message() {
        return message;
    }

    public Node node() {
        return node;
    }
}
