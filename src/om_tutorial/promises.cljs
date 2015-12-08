(ns om-tutorial.promises
  (:require
   [cljs.core.async.impl.protocols :as impl]
   [cljs.core.async.impl.dispatch :as dispatch]))

(extend-type js/Promise
  impl/ReadPort
  (take! [promise handler]
    (.then promise
           (fn [val]
             (dispatch/run #((impl/commit handler) val)))
           (fn [err]
             (dispatch/run #((impl/commit handler) err))))
    nil))
