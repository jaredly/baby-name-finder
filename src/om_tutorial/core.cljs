(ns om-tutorial.core
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch
                                   dispatch-sync]]
            [cljs.core.async :as a
             :refer [>! <! chan buffer close!
                     alts! timeout]]
            [schema.core :as s]

            [om-tutorial.api :as api]
            [om-tutorial.view :as view]
            [om-tutorial.config :as config]
            [om-tutorial.handlers]
            [om-tutorial.subs])

  (:require-macros
   [schema.core :as s]
   [cljs.core.async.macros :refer [go go-loop]]))

;; -------- events & stuff

(s/defn get-user [token :- s/Str cb #_(:- (t/fn User -> nil))]
  (go
    (let [api-config (assoc config/api-base :token token)
          user (<! (api/get-user api-config))]
      (cb user))))

(defn render []
  (r/render [view/baby-names-app]
            (js/document.getElementById "app")))

;; ----------- initial state
(def initial-state
  {:sync-status :initial
   :token nil
   :user :missing
   :people {}
   :viewing nil
   :boys-names {}
   :girls-names {}})

(defn ^:export start-notoken []
  (dispatch-sync [:initialize initial-state])
  (render))

(defn ^:export start-token [token]
  (get-user
   token
   (fn [user]
     (dispatch-sync
      [:initialize (assoc initial-state
                          :user user
                          :token token
                          :login-error (= :missing user))])
     (render))))
