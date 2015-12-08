(ns om-tutorial.view-get-started
  (:require [om-tutorial.view-helpers :as view-helpers :refer [hoverable]]
            [om-tutorial.colors :as colors]))

(def styles
  {:get-started/container {:max-width 400
                           :align-items "center"}
   :get-started/title {:font-size "30px"
                       :text-align "center"
                       :margin-bottom 30}
   :get-started/message {:line-height "1.5"
                         :margin-bottom 20}
   :get-started/button {:padding "5px 30px"
                        :font-size "1em"
                        :border-radius "1em"}
   })

(def view (partial view-helpers/view styles))
(def text (partial view-helpers/text styles))
(def button (partial view-helpers/button styles))

(defn get-started [on-start-searching]
  [view :get-started/container
   [text :get-started/title "Let's get started!"]
   [text :get-started/message "Click below to search through 7 generations of your ancestors (and their siblings) for girls & boys names."]
   [button {:style (:get-started/button styles)
            :on-click on-start-searching}
    "Start Searching"]]
  )
