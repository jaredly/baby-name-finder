(ns om-tutorial.view-show-names
  (:require [om-tutorial.view-helpers :as view-helpers :refer [hoverable]]
            [om-tutorial.colors :as colors]
            [om-tutorial.api :as api]
            [re-frame.core :refer [subscribe]]
            [reagent.core :as r]))

(def styles
  {:show/container {:overflow "auto"
                    :width 500}
   :show/person {:margin "10px 0"}
   :show/name-row {:flex-direction "row"}
   :show/top {:align-items "center"
              :flex-direction "row"
              :cursor "pointer"}
   :show/lifespan {:font-size "90%"
                   :color "#777"
                   :padding "0 10px"}
   :show/image {:width 40
                :height 40
                :border-radius 20
                :background-color "#eef"
                :background-size "cover"
                :margin "0 10px 0 0"}
   :show/image-large {:width 100
                      :height 100
                      :margin "0 15px 0 0"}

   :open/name-row {:flex 1}
   :open/second-row {:justify-content "space-between"
                     :flex-direction "row"}
   :open/name {:font-size "1.1em"
               :font-weight "bold"}
   :open/event {:margin "5px 0"
                :line-height "1.3"
                :flex-direction "row"
               :flex-wrap "wrap"
               :word-wrap "wrap"
               :font-size "80%"}
   :open/event-label {:font-weight "bold"
                     :margin-right 5}
   :open/event-date {:word-wrap "wrap"}
   :open/event-place {:word-wrap "wrap"}

   :open/view-link {:font-size "80%"
                    :text-decoration "none"
                    :color "#77a"}

   :main/link {:justify-content "flex-end"
               :flex-direction "row"}

   :main/trail {}
   :main/trail-item {:align-items "center"}
   :main/trail-rel {:margin "5px 0"
                    :font-size "70%"
                    :text-transform "capitalize"
                    :color "#555"}
   :main/trail-name {:font-size "80%"}
   :main/trail-you {:text-align "center"}
   })

(def view (partial view-helpers/view styles))
(def text (partial view-helpers/text styles))
(def button (partial view-helpers/button styles))

(defn collapsible [render]
  (let [collapsed (r/atom false)]
    (fn [render]
      (render @collapsed #(do (swap! collapsed not) nil)))))

(defn profile-style [person api-config large]
  {:style
   (assoc
    (merge (:show/image styles)
           (when large (:show/image-large styles)))
    :background-image
    (str "url("
         (api/portrait-url api-config (:id person))
         ")"))})

; TODO show relationship, and line on click (toggle + expand)
; maybe also portrait? and like memories maybe...

(defn person-evt [label place date]
  (when (or place date)
    [view :open/event
     [view :open/event-label label]
     [view :open/event-date date]
     [view :open/event-place place]]))

(defn person-birth [person]
  (person-evt "Born:"
              (get-in person [:display :birthPlace])
              (get-in person [:display :birthDate])))

(defn person-death [person]
  (person-evt "Died:"
              (get-in person [:display :deathPlace])
              (get-in person [:display :deathDate])))

(defn show-trail [trail]
  (conj
   (into
    [view :main/trail]
    (reverse (map
              #(-> [view :main/trail-item
                    [text :main/trail-name (:name %)]
                    [text :main/trail-rel (name (:rel %))]
                    ])
              trail)))
   [view :main/trail-you "You"]))

(defn show-person [person api-config]
  [collapsible
   (fn show-person-inner [expanded toggle]
     [view :show/person
      [view {:style (styles :show/top)
             :on-click toggle}
       [view (profile-style person api-config expanded)]
       (if-not expanded
         [view :show/name-row
          [text :show/name (get-in person [:display :name])]
          [text :show/lifespan (get-in person [:display :lifespan])]
          ]
         [view :open/name-row
          [view :show/name-row
           [text :open/name (get-in person [:display :name])]
           [text :open/lifespan (get-in person [:display :lifespan])]
           ]
          [view
           [text :open/relation (get-in person [:display :relation])]
           ]
          [person-birth person]
          [person-death person]
          ]
         )]
      (when expanded
        [view :show/main
         [view :main/link
          [:a {:href (api/view-link api-config (:id person))
               :target "_blank"
               :style (:open/view-link styles)}
           "View in FamilySearch"]
          ]
         [show-trail (:trail person)]
         ])
      ])])

(defn main [people api-config]
  (into
   [view :show/container]
   (map #(-> ^{:key (:id %)} [show-person % api-config]) people)
   ))
