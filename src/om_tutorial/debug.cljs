(ns om-tutorial.debug
  (:require [om-tutorial.core :as core]
            [devtools.core :as devtools]
            [re-frame.core :refer [dispatch
                                   dispatch-sync]]
            [cljs.core.async :as a
             :refer [>! <! chan buffer close!
                     alts! timeout]]
            [schema.core :as s]

            [om-tutorial.core :as core]
            [om-tutorial.api :as api]
            [om-tutorial.view :as view]
            [om-tutorial.config :as config]
            [om-tutorial.handlers]))

(devtools/set-pref! :install-sanity-hints true) ; this is optional
(devtools/install!)

(enable-console-print!)

(defonce -init
  (dispatch-sync [:initialize core/initial-state]))

; re-render on reload
(core/render)

(comment

  (actions/stop-searching )

  (dispatch [:stop-searching])

  (dispatch [:initialize core/initial-state])

  (core/start-token config/token)

  (:displayName (:user @re-frame.db/app-db))
  (:sync-status @re-frame.db/app-db)

  (actions/stop-searching
    (:sync-status @re-frame.db/app-db))


  (go
    (def rels
      (<! (api/get-relations
            (config/api token)
            (:personId user)))))
  )
