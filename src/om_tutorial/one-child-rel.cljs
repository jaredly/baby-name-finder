(ns om-tutorial.one-child-rel)

(defn maybe-conj [thing list]
  (if thing (conj list thing) list))

(defn one-child-rel [id {:keys [child father mother]} data persons]
  (let [child-id (:resourceId child)
        father-id (:resourceId father)
        mother-id (:resourceId mother)
        i-am-child (= id child-id)]
    (if i-am-child
      (let [parent-map {:mother (persons mother-id) :father (persons father-id)}
            parents (conj (:parents data) parent-map)
            parent-ids (maybe-conj father-id (maybe-conj mother-id (:parent-ids data)))]
        (assoc data :parents parents :parent-ids parent-ids))
      (let [child-ids (conj (:child-ids data) child-id)
            families (:families data)
            spouse (if (= id father-id) mother-id father-id)
            families
            (if (families spouse)
              (update-in families [spouse :children] #(conj % (persons child-id)))
              (assoc families spouse
                     {:father (persons father-id)
                      :mother (persons mother-id)
                      :spouse (persons spouse)
                      :children [(persons child-id)]}))]
        (assoc data :families families :child-ids child-ids)))))
