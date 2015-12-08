(ns om-tutorial.guard)

(defn guarded-expr* [expression]
  (if-not (and (seq? expression) (= "guard" (name (first expression))))
    {:just expression}
    {:cond (second expression)
     :body (rest (rest expression))}
    ))

(defn guarded-xform* [items]
  (let [head (first items)
        tail (rest items)
        guard (:cond head)]
    (cond
      (= 0 (count items)) items
      (not guard) (cons (:just head) (guarded-xform tail))
      :default `((if ~guard
         (do ~@(:body head))
         ~(guarded-xform tail))))))

(defn guarded* [expressions]
  (guarded-xform* (map guarded-expr* expressions)))

(defmacro guarded
  "The guarded macro can let you change this
  (do
    (if (test-something)
      (do
        (do-something)
        nil)
      (do
        (something-else)
        (if (nother-test)
          :some-thing
          (do
            (more-things)
            :the-result)))))

  Into this:

  (guarded
    (guard (test-something)
      (do-something)
      nil)
    (something-else)
    (guard (nother-test)
        :some-thing)
    (more-things)
    :the-result)

  Now, this might be antipatterny, b/c it indicates you are doing too much
  procedural code....
  "
  [& expressions]
  `(do ~@(guarded* expressions)))

