(ns om-tutorial.view-names
  (:require [om-tutorial.view-helpers :as view-helpers :refer [hoverable]]
            [om-tutorial.colors :as colors]))

(def styles
  {
   :baby/container {:flex 1
                    :flex-direction "row"}
   :baby/lists {:flex-direction "row"
                :padding "20px"
                :flex 1
                :overflow "auto"}
   :baby/names {:padding "5px" :flex 1}
   :baby/title {:font-weight "bold"
                :margin-bottom 10
                :text-align "center"}
   :baby/list {}
   :baby/name {:padding "5px"}

   :name/hovered-row {:background-color "#eee"}
   :name/selected-row {:background-color "#aaa"}
   :name/row {:cursor "pointer"}
   :name/name {:font-size "110%"
               :text-transform "capitalize"
               :padding "5px 0"}
   :name/num {:width 60
              :padding "0 10px"
              :font-size "90%"
              :color "#888"}
   :name/count {:margin-left "20px"
                :padding "0 10px"}
   })

(def view (partial view-helpers/view styles))
(def text (partial view-helpers/text styles))
(def button (partial view-helpers/button styles))

(defn baby-names [gender names viewing on-click]
  [view :baby/names
   [text :baby/title gender " Names"]
   [view :baby/list
    [:table nil
     (into
      [:tbody nil]
      (map
       (fn [idx [name ids]]
         [hoverable
          {:el :tr
           :style (if (= viewing name)
                    (styles :name/selected-row)
                    (styles :name/row))
           :hover-style (:name/hovered-row styles)
           :props {:on-click #(on-click name)}}

          [:td nil [text :name/num idx]]
          [:td nil [text :name/name name]]
          [:td nil [text :name/count (count ids)]]])
       (range 1 (int (count names)))
       names))]]])

(defn main [& {[gender-key name] :viewing
               :keys [girls-names boys-names on-click show-names]}]
  [view :baby/container
   [view :baby/lists
    [baby-names "Girl" girls-names
     (when (= gender-key :girls-names) name)
     #(on-click :girls-names %)
     ]
    [baby-names "Boy" boys-names
     (when (= gender-key :boys-names) name)
     #(on-click :boys-names %)
     ]]
   (when-not (nil? gender-key)
     [show-names])
   ]
  )
