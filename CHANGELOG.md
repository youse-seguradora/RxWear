# Changelog

## Version 1.1.0

* BREAKING CHANGE: The lib now uses Singles instead of Observables if only one item is emitted.
* BREAKING CHANGE: Removed unnecessary `getRemoteNodes()`.
* BREAKING CHANGE: Observables, which previously returned a List/Map, now emit the items/values of these Collections.