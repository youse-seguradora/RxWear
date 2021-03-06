package com.patloew.rxwear;

import android.content.Context;
import android.support.annotation.NonNull;

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
 * FILE MODIFIED by Marek Wałach, 2018
 *
 */
abstract class BaseObservable<RESULT> extends BaseRx<ObservableEmitter<RESULT>> implements ObservableOnSubscribe<RESULT> {

    BaseObservable(@NonNull Context context) {
        super(context);
    }

    @Override
    public final void subscribe(ObservableEmitter<RESULT> e) {
        e.setCancellable(this::unSubscribe);
        onSubscribe(e);
    }
}
