package com.patloew.rxwear.transformers;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import rx.Observable;

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
 *
 * ---------------------------------
 *
 * Transformer that optionally filters DataEvents by path and/or type and
 * returns an Observable<DataMap> of the dataMap from the DataItem.
 */
public class DataEventGetDataMap implements Observable.Transformer<DataEvent, DataMap> {

    private final String path;
    private final boolean isPrefix;
    private final Integer type;

    private DataEventGetDataMap(String path, boolean isPrefix, Integer type) {
        this.path = path;
        this.isPrefix = isPrefix;
        this.type = type;
    }

    public static Observable.Transformer<DataEvent, DataMap> noFilter() {
        return new DataEventGetDataMap(null, false, null);
    }

    public static Observable.Transformer<DataEvent, DataMap> filterByPath(String path) {
        return new DataEventGetDataMap(path, false, null);
    }

    public static Observable.Transformer<DataEvent, DataMap> filterByPathAndType(String path, int type) {
        return new DataEventGetDataMap(path, false, type);
    }

    public static Observable.Transformer<DataEvent, DataMap> filterByPathPrefix(String pathPrefix) {
        return new DataEventGetDataMap(pathPrefix, true, null);
    }

    public static Observable.Transformer<DataEvent, DataMap> filterByPathPrefixAndType(String pathPrefix, int type) {
        return new DataEventGetDataMap(pathPrefix, true, type);
    }

    public static Observable.Transformer<DataEvent, DataMap> filterByType(int type) {
        return new DataEventGetDataMap(null, false, type);
    }

    @Override
    public Observable<DataMap> call(Observable<DataEvent> observable) {
        if(type != null) {
            observable = observable.filter(dataEvent -> dataEvent.getType() == type);
        }

        if(path != null) {
            observable = observable.filter(dataEvent -> {
                if (isPrefix) {
                    return dataEvent.getDataItem().getUri().getPath().startsWith(path);
                } else {
                    return dataEvent.getDataItem().getUri().getPath().equals(path);
                }
            });
        }

        return observable.map(this::getDataMap);
    }

    private DataMap getDataMap(DataEvent dataEvent) {
        return DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
    }
}
