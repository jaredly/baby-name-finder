(ns om-tutorial.http
  (:require [schema.core :as s]
            [cljs.core.async
             :as a
             :refer [>! <! chan buffer close!
                     alts! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn get
  [url {:keys [headers xform]}]
  (let [xhr (js.XMLHttpRequest.) res-chan (if xform (chan 1 xform) (chan))]
    (.open xhr "GET" url)
    (doall
     (map (fn [[key val]]
            (.setRequestHeader xhr (name key) val))
          (assoc headers :accept "application/x-fs-v1+json,application/json")))
    (aset xhr "onload"
          (fn []
            (a/put!
             res-chan
             {:response (js->clj (.-response xhr) :keywordize-keys true)
              :status (.-status xhr)
              })))
    (aset xhr "onerror"
          (fn [err]
            (a/put!
             res-chan
             {:response nil
              :error err
              :status (.-status xhr)})))
    (aset xhr "responseType" "json")
    (.send xhr)
    res-chan
    )
  )
