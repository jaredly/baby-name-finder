(ns om-tutorial.api
  (:require [schema.core :as s]
            [om-tutorial.http :as http]
            [om-tutorial.parse-relations :refer [parse-relations]]
            [cljs.core.async
             :as a
             :refer [>! <! chan buffer close!
                     alts! timeout]])
  (:require-macros
   [schema.core :as s]
   [cljs.core.async.macros :refer [go go-loop]]
   [om-tutorial.macros :refer [kw-obj]]))

(def LoggedOutAPI
  {:base s/Str})

(def APIConfig
  (assoc LoggedOutAPI :token s/Str))

(s/defn make-redirect :- s/Str []
  (str
   (aget js/location "protocol")
   "//"
   (aget js/location "host")
   "/"))

(def endpoints
  {:relations #(str %1 "/platform/tree/persons-with-relationships?person=" %2 "&persons=")
   :portrait #(str %1 "/platform/tree/persons/" %2 "/portrait"
                   "?default=" (js/encodeURIComponent (str (make-redirect) "missing-profile.png")))
   :user #(str %1 "/platform/users/current")
   :view-link #(str %1 "/ark:/61903/4:1:" %2)
   })

(defn view-link [config id]
  ((:view-link endpoints) (:base config) id))

(s/defn auth-header
  [token :- s/Str]
  (when token
    {"Authorization" (str "Bearer " token)}))

(s/defn get-url
  ([config :- APIConfig url :- s/Str]
   (http/get url
             {:headers (auth-header (:token config))}))
  ([config :- APIConfig url :- s/Str xform #_(t/fn Response -> s/any)]
    (http/get url
            {:xform xform :headers (auth-header (:token config))})
     ))

(s/defn portrait-url
  [config :- APIConfig id :- s/Str]
  ((:portrait endpoints) (:base config) id))

(s/defn get-relations
  [config :- APIConfig, id :- s/Str]
  (let [url ((:relations endpoints) (:base config) id)]
    (get-url config url (map #(parse-relations id (:response %))))))

(s/defn get-user
  [config :- APIConfig]
  (let [url ((:user endpoints) (:base config))
        xform #(if-let [user (get-in % [:response :users 0])]
                 user
                 :missing)]
    (get-url config url (map xform))))

(s/defn login-url :- s/Str [config]
  (str (:base config)
       "/cis-web/oauth2/v3/authorization?response_type=code&client_id="
       (:client-id config)
       "&redirect_uri="
       (js/encodeURIComponent (make-redirect))))

(s/defn log-in [config]
  (let [redirect (make-redirect)
        url (login-url config)]
    (aset js/window "location" url)))

(comment
  (go
    (enable-console-print!)

    (def user
      (<! (get-user config)))

    (prn (:displayName user))

    (def relations
      (:response (<! (get-relations config (:personId user)))))
    (def parsed (parse-relations (:personId user) relations))
    (def parent-id (first (:parent-ids parsed)))
    (def parent-relations
      (parse-relations parent-id (:response (<! (get-relations config parent-id)))))
    (def grandparent-id (first (:parent-ids parent-relations)))
    (def grandparent-relations
      (parse-relations grandparent-id (:response (<! (get-relations config grandparent-id)))))
    )

  (aset js/window "parsed" (pr-str relations))

  )
