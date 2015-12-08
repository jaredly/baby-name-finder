(ns om-tutorial.macros)

(defn kw-obj* [made items]
  (if (empty? items) made
      (let [head (first items)]
        (if (keyword? head)
          (recur
           (cons [head (first (rest items))] made)
           (rest (rest items)))
          (recur
           (cons [(keyword (name head)) head] made)
           (rest items))))))

(defmacro kw-obj [& names]
  `(into {} [~@(kw-obj* [] names)]))
