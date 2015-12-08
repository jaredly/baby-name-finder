(ns om-tutorial.rel-to-family
  (:require-macros
   [om-tutorial.macros :refer [kw-obj]]))

(def COUPLE_TYPE "http://gedcomx.org/Couple")
(def MARRIAGE_TYPE "http://gedcomx.org/Marriage")

(defn is-female [person]
  (if-let [gender (get-in person [:display :gender])]
    (= (.toLowerCase gender) "female")
    false))

(defn is-marriage-fact [{type :type}]
  (= type MARRIAGE_TYPE))

(defn get-value [items]
  (if (empty? items) nil
      (if-let [val (:value (first items))]
        val
        (recur (rest items)))))

(defn get-normalized [item]
  (if-let [norm (:normlaized item)]
    (get-value norm)))

(defn get-date-year [formal]
  (when formal
    (re-find #"\d{4}" formal)))

(defn parse-marriage [{:keys [date place]}]
  {:date (get-normalized date)
   :year (get-date-year (:formal date))
   :place (get-normalized place)})

(defn get-marriage [facts]
  (if (empty? facts) nil
      (let [fact (first facts)]
        (if (and (not (nil? fact))
                 (is-marriage-fact fact))
          (parse-marriage fact)
          (recur (rest facts))))))


(defn rel-to-family [id persons {:keys [type facts] {p1 :resourceId} :person1 {p2 :resourceId} :person2}]
  (when (= type COUPLE_TYPE)
    (let [spouse-id (if (= p1 id) p2 p1)
          spouse (persons spouse-id)
          female-spouse (is-female spouse)
          [mother-id father-id]
            (if female-spouse [spouse-id id] [id spouse-id])
          mother (persons mother-id)
          father (persons father-id)
          marriage (get-marriage facts)]
      [spouse-id
       (kw-obj
        spouse
        mother
        father
        marriage
        :children [])]
      )))
