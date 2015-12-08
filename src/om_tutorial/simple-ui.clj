(ns om-tutorial.simple-ui
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defmacro defui-simple [name params & body]
  `(do
     (defui component#
       Object
       (render
        [this#]
        (let [props# (om/props this#)]
          ((fn ~params ~@body) props#))))
     (def ~name (om/factory component#))))

(comment
  (defui-simple baby-names-list [baby-names]
    (apply
     dom/div nil
     (map baby-name baby-names)))

  (defui BabyNameList
    Object
    (render
     [this]
     (let [baby-names (om/props this)]
       (apply dom/div nil
              (map baby-name baby-names)))))

  (def baby-name-list (om/factory BabyNameList))
  )
