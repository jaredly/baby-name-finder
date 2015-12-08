(ns om-tutorial.view-logged-out
  (:require [om-tutorial.view-helpers :as view-helpers]
            [om-tutorial.colors :as colors]))

(def styles
  {:logged-out {:align-items "center"
                :padding-bottom 100}
   :logged-out/logo {:font-size 40
                     :font-weight "bold"
                     :color colors/logo
                     :margin-bottom 20}
   :logged-out/message {:max-width 500
                        :margin-bottom 20
                        :line-height 1.5}
   :logged-out/login-message {:font-size "90%"
                              :margin-bottom 15}
   :logged-out/login-button {:font-size "100%"
                             :padding ".5em 1em"
                             :border-radius ".5em"}
   })

(def view (partial view-helpers/view styles))
(def text (partial view-helpers/text styles))
(def button (partial view-helpers/button styles))

(defn main [& {:keys [site-title tagline on-login]}]
  [view :logged-out
   [text :logged-out/logo site-title]
   [text :logged-out/message "Welcome!" tagline]
   [text :logged-out/login-message "Sign in with FamilySearch to get started!"]
   [button {:style (:logged-out/login-button styles)
            :on-click on-login}
    "Sign in with FamilySearch"]])
