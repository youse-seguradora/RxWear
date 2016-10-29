package com.patloew.rxwear.transformers;

import com.google.android.gms.wearable.DataItem;
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
 * Transformer that optionally filters DataItems by path and/or type and
 * returns an Observable<DataMap> of the dataMap from the DataItem.
 */
public class DataItemGetDataMap implements Observable.Transformer<DataItem, DataMap> {

    private final String path;
    private final boolean isPrefix;

    private DataItemGetDataMap(String path, boolean isPrefix) {
        this.path = path;
        this.isPrefix = isPrefix;
    }

    public static Observable.Transformer<DataItem, DataMap> noFilter() {
        return new DataItemGetDataMap(null, false);
    }

    public static Observable.Transformer<DataItem, DataMap> filterByPath(String path) {
        return new DataItemGetDataMap(path, false);
    }

    public static Observable.Transformer<DataItem, DataMap> filterByPathPrefix(String pathPrefix) {
        return new DataItemGetDataMap(pathPrefix, true);
    }

    @Override
    public Observable<DataMap> call(Observable<DataItem> observable) {
        if(path != null) {
            observable = observable.filter(dataItem -> {
                if (isPrefix) {
                    return dataItem.getUri().getPath().startsWith(path);
                } else {
                    return dataItem.getUri().getPath().equals(path);
                }
            });
        }

        return observable.map(dataItem -> DataMapItem.fromDataItem(dataItem).getDataMap());
    }
}
