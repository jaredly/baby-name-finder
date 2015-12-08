(ns om-tutorial.subs
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [re-frame.core :refer [register-sub]]
            [om-tutorial.config :as config]
            [schema.core :as s])
  (:require-macros
   [reagent.ratom :refer [reaction]]
   [schema.core :as s]))

(defn second-sort [a b]
  (if (= (count (a 1)) (count (b 1)))
    (compare (a 0) (b 0))
    (compare (b 1) (a 1))))

;; ------------ subscription handlers
(register-sub
 :boys-names
 (fn [db _]
   (let [names (reaction (:boys-names @db))]
     (reaction (sort second-sort @names)))))

(register-sub
 :girls-names
 (fn [db _]
   (let [names (reaction (:girls-names @db))]
     (reaction (sort second-sort @names)))))

(register-sub
 :people-for-current-name
 (fn [db _]
   (reaction
    (let [ids (get-in @db (:viewing @db))
          people (:people @db)]
      (map people ids)))))

(register-sub
 :logged-in?
 (fn [db _]
   (reaction (not (= :missing (:user @db))))))

(register-sub
 :api-config
 (fn [db _]
   (reaction (assoc config/api-base :token (:token @db)))))

(register-sub :token (fn [db _] (reaction (:token @db))))
(register-sub :viewing (fn [db _] (reaction (:viewing @db))))
(register-sub :user (fn [db _] (reaction (:user @db))))
(register-sub :sync-status (fn [db _] (reaction (:sync-status @db))))
(register-sub :root-id (fn [db _] (reaction (get-in @db [:user :personId]))))
