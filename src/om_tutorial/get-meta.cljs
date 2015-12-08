(ns om-tutorial.get-meta
  (:require-macros
   [om-tutorial.macros :refer [kw-obj]]))

(def empty-meta
  {:estimated true
   :esitmated-birth true
   :estimated-death true})

(defn get-meta [{:keys [birthDate lifespan]}]
  (let [parts (if-not lifespan nil (.split lifespan " "))]
    (if (= 2 (count parts))
      empty-meta
      ;; TODO make this better...
      (let [[born died] parts
            born (js/parseInt born 10)
            died (js/parseInt died 10)
            estimated-birth false
            estimated-death false
            [born estimated-birth]
            (if (and (js/isNaN born) (not (js/isNaN died)))
              [(- died 40) true]
              [born false])
            [died estimated-death]
            (if (and (js/isNaN died) (not (js/isNaN born)))
              [(+ born 40) true]
              [died false])
            ]
        (if (js/isNaN born)
          empty-meta
          (kw-obj
           :age (- died born)
           born
           estimated-birth
           estimated-death
           :estimated (or estimated-birth estimated-death))
          )
        {:born born :died died}))))
