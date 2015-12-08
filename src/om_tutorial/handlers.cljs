(ns om-tutorial.handlers
  (:require [schema.core :as s]
            [clojure.string :as str]
            [reagent.core :as r]
            [re-frame.core :refer [register-handler]])
  (:require-macros
   [schema.core :as s]))

(s/defn maybe-lower :- (s/maybe s/Str)
  [text :- (s/maybe s/Str)]
  (when text (str/lower-case text)))

(def Person
  {:display {:name s/Str
             :gender (s/maybe s/Str)}})

(s/defn first-name :- (s/maybe s/Str)
  [person :- Person]
  (-> person
      (get-in [:display :name])
      (str/split " ")
      (get 0)
      maybe-lower))

(s/defn get-gender :- s/Str
  [person :- Person]
  (str/lower-case (get-in person [:display :gender] "Unknown")))

(s/defn gender-key :- s/Keyword
  [person :- Person]
  (let [gender (get-gender person)]
    (if (= gender "female")
      :girls-names
      :boys-names)))

;; ------------ event handlers
(register-handler
 :found-person
 (fn
   [db [_ person]]
   (-> db
       (update-in [:people] assoc (:id person) person)
       (update-in [:sync-status :total] inc)
       (assoc-in [:sync-status :current] person)
       (update-in [(gender-key person) (first-name person)]
                  (fnil conj []) (:id person)))))

(register-handler
 :initialize
 (fn [db [_ initial-state]]
   (merge db initial-state)))

(register-handler
 :started-searching
 (fn [db [_ people stop]]
   (assoc db :sync-status {:chan people
                           :stop stop
                           :total 0
                           :started (.getTime (js/Date.))
                           :current nil})))

(register-handler
 :stopped-searching
 (fn [db _]
   (.log js/console
         (/ (- (.now js/Date)
               (get-in db [:sync-status :started]))
            1000)
         "Seconds elapsed")
   (assoc db :sync-status :stopped)))

(register-handler
 :view
 (fn [db [_ gender-key name]]
   (assoc db :viewing [gender-key name])))
