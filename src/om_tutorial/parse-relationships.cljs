(ns om-tutorial.parse-relations
  (:require
   [om-tutorial.get-meta :refer [get-meta]]
   [om-tutorial.rel-to-family :refer [rel-to-family]]
   [om-tutorial.one-child-rel :refer [one-child-rel]]))

(defn xform-persons [persons]
  (into {} (map #(when (% :id)
                   [(% :id) (assoc-in % [:display :meta] (get-meta %))])) persons))

(defn xform-relationships [relationships id persons]
  (into {} (map (partial rel-to-family id persons) relationships)))

(defn parse-child-parents [id child-and-parents-relationships families persons]
  (loop [child-and-parents-relationships child-and-parents-relationships
         data {:families families :parent-ids #{} :child-ids #{} :parents []}]
    (if (empty? child-and-parents-relationships) data
        (recur
         (rest child-and-parents-relationships)
         (one-child-rel id (first child-and-parents-relationships) data persons)))))

; TODO actuallt write this
(defn estimate-meta [person families]
  (get-in [:display :meta] person))


(defn parse-relations
  [id {:keys [relationships childAndParentsRelationships persons]}]
  (let [persons (xform-persons persons)
        person (persons id)
        families (xform-relationships relationships id persons)]
    (assoc (parse-child-parents id childAndParentsRelationships families persons)
           :person (assoc-in person [:display :meta] (estimate-meta person families))
           :persons persons)))
