(ns om-tutorial.view
  (:require [re-frame.core :refer [register-handler
                                   path
                                   register-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe]]
            [om-tutorial.view-helpers :as view-helpers]
            [om-tutorial.colors :as colors]
            [om-tutorial.actions :as actions]

            [om-tutorial.view-names :as >names]
            [om-tutorial.view-show-names :as >show-names]
            [om-tutorial.view-get-started :as >get-started]
            [om-tutorial.view-logged-out :as >logged-out]

            [reagent.core :as r]
            [goog.dom :as gdom])
  (:require-macros
   [om-tutorial.macros :refer [kw-obj]]))

(def styles
  {:main/container {:flex 1}

   :header {:padding 20
            :text-align "center"}
   :header/title {:font-size 30
                  :font-weight "bold"}

   :sync/status {:align-items "center"
                 :padding 10}
   :sync/name {:font-weight "bold"
               :padding "5px 0"}
   :show/container {:width 500
                    :padding 20
                    :overflow "auto"}

   })

(def view (partial view-helpers/view styles))
(def text (partial view-helpers/text styles))
(def button (partial view-helpers/button styles))

(def site-title
  "Baby Name Finder")
(def tagline
  "Baby Name Finder will give you a list of all the first & middle names of your ancestors, sorted by popularity within your tree, and within the United States.")

(defn show-names []
  (let [people (subscribe [:people-for-current-name])
        api-config (subscribe [:api-config])]
    [>show-names/main @people @api-config]))

(defn current-person [person total]
  [view :sync/status
   [text "Total searched " total]
   [text :sync/name (get-in person [:display :name])]
   [text (get-in person [:display :lifespan])]
   [text (:num-up person) " generations back"]])

(defn header []
  [view :header
   [text :header/title "Baby Name Finder"]])

(defn sync-status []
  (let [status (subscribe [:sync-status])]
    (fn []
      (let [status @status]
        (cond
          (= :initial status) nil
          (= :stopped status) [text "Stopped looking for names"]
          (nil? (:current status)) [text "Looking through your ancestors..."]
          :default [current-person (:current status) (:total status)]))
      )))

(defn baby-names []
  (let [boys-names (subscribe [:boys-names])
        girls-names (subscribe [:girls-names])
        viewing (subscribe [:viewing])]
    (fn []
      [>names/main
       :boys-names @boys-names
       :girls-names @girls-names
       :viewing @viewing
       :show-names show-names
       :on-click #(dispatch [:view %1 %2])]
      )))

(defn main-page []
  [view :main/container
   [header]
   [sync-status]
   [baby-names]])

(defn get-started []
  (let [api-config (subscribe [:api-config])
        root-id (subscribe [:root-id])]
    (fn []
      [>get-started/get-started
       #(actions/start-searching @api-config @root-id)])))

(defn logged-in-main []
  (let [sync-status (subscribe [:sync-status])]
    (fn []
      (if (= :initial @sync-status)
        [get-started]
        [main-page]))))

(defn logged-out-main []
  (let [api-config (subscribe [:api-config])]
    (fn []
      [>logged-out/main
       :site-title site-title
       :tagline tagline
       :on-login #(actions/log-in @api-config)])))

(defn baby-names-app [props]
  (let [logged-in? (subscribe [:logged-in?])]
    (fn []
      (if @logged-in?
        [logged-in-main]
        [logged-out-main]))))
