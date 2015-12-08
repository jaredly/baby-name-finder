(ns om-tutorial.view-helpers
  (:require [reagent.core :as r]))

(def text-style {:display "inline-block"
                 :flex-shrink 0
                 :box-sizing "border-box"})

(def view-style {:display "flex"
                 :flex-direction "column"
                 :min-height 0
                 :flex-shrink 0
                 :box-sizing "border-box"})

(def button-style {:display "inline-block"
                   :flex-shrink 0
                   :box-sizing "border-box"
                   :cursor "pointer"
                   :background-color "transparent"
                   :border "1px solid"
                   :border-radius 5})

(defn parse-props [styles default-style props]
  (if (keyword? props) 
    {:style (merge default-style (props styles))}
    (let [style (merge default-style (:style props))
          props (merge {:style style} (dissoc props :style))]
      props)))

(defn better-el [dom-el default-style styles props & children]
  (let [[props children]
        (if (or (keyword? props) (map? props))
          [props children]
          [nil (concat [props] children)]
          )]
    (vec (concat [dom-el (parse-props styles default-style props)] children))))

(def view (partial better-el :div view-style))
(def text (partial better-el :span text-style))
; TODO have the button also stop-propagation
(def button (partial better-el :button button-style))

(defn hoverable [config & children]
  (let [hovered (r/atom false)]
    (fn [{:keys [style hover-style el props]
          :or {:el :div}} & children]
      (into
       [el (assoc props
                 :style (if @hovered
                          (merge style hover-style)
                          style)
                 :on-mouse-over #(do (reset! hovered true) nil)
                 :on-mouse-out #(do (reset! hovered false) nil)
                 )
        ] children))))
