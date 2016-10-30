# Changelog

## Version 1.3.0

* BREAKING CHANGE: RxWear no longer provides static methods. Create an instance once and share it, e.g. via dependency injection or by providing the instance via your Application class.
* BREAKING CHANGE: Removed deprecated Data method `getSingle()`. Use `get()` instead.
* Updated Play Services (9.8.0) and RxJava (1.2.1).

## Version 1.2.0

* Added `PutDataMap.toObservable()`.
* Deprecated `RxWear.Data.getSingle()`. Use `RxWear.Data.get()` instead.
* Added convenience methods to `RxWear.Data` and `RxWear.Message` to directly filter out DataItems/MessageEvents by path.
* Fixed issue #1, which was caused by Wearable API issue [74204](https://code.google.com/p/android/issues/detail?id=74204)

## Version 1.1.0

* BREAKING CHANGE: The lib now uses Singles instead of Observables if only one item is emitted.
* BREAKING CHANGE: Removed unnecessary `getRemoteNodes()`.
* BREAKING CHANGE: Observables, which previously returned a List/Map, now emit the items/values of these Collections.