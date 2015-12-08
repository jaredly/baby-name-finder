(ns om-tutorial.syncer
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [schema.core :as s]
            [cljs.core.async
             :as a
             :refer [>! <! chan buffer close!
                     alts! timeout]]
            [om-tutorial.api :as api]))

(def SyncerConfig
  {:api s/Any
   :max-num s/Num
   :max-up s/Num
   :max-down s/Num})

; Just to validate
(defn init
  [config] config)

(defn make-trail-item [person rel]
  {:rel rel
   :id (:id person)
   :name (get-in person [:display :name])
   :gender (.toLowerCase (get-in person [:display :gender]))
   :lifespan (get-in person [:display :lifespan])
   :place (or (get-in person [:display :birthPlace]) (get-in person [:display :deathPlace]))
   })

; TODO
(defn calc-relation [& a]
  nil)

(defn make-worker
  [config put-ids take-ids result-chan]
  (go-loop []
    (let [{:keys [id trail num-up num-down]} (<! take-ids)
          relations (<! (api/get-relations (:api config) id))
          person (:person relations)
          should-go-up (and (< num-up (:max-up config)) (= 0 num-down))
          should-go-down (< num-down (:max-down config))
          add!
          (fn [person rel trail num-up num-down]
            #_(.log js/console "adding person" (clj->js (:display person)))
            (a/put!
             put-ids
             {:id (:id person)
              :trail
              (conj trail
                    (make-trail-item person rel))
              :num-up num-up
              :num-down num-down}))]

      (if should-go-up
        (doall (map (fn [{:keys [mother father]}]
                      (when mother (add! mother :mother trail (inc num-up) 0))
                      (when father (add! father :father trail (inc num-up) 0))) (:parents relations))))

      (if should-go-down
        (doall
         (map
          (fn [child-id]
            (add! (get-in relations [:persons child-id]) :child trail num-up (inc num-down)))
          (:child-ids relations))))

      (>!
       result-chan
       {:id id
        :display (:display person)
        :trail trail
        :num-up num-up
        :relation (calc-relation trail num-up num-down)
        ; TODO should I also have parents & children saved?
        })
      (recur))))

(defn unique-chan [in size key]
  (let [out (chan size)]
    (go-loop [seen #{}]
      (when-let [item (<! in)]
        (if (contains? seen (key item))
          (recur seen)
          (do
            (>! out item)
            (recur (conj seen (key item)))))))
    out))

(defn crawl
  [config root-id]
  (let [id-chan (chan)
        unique-ids (unique-chan id-chan (:max-num config) :id)
        result-chan (chan)
        ]
    (dotimes [n (:num-workers config 5)]
      (make-worker config id-chan unique-ids result-chan))
    (a/put! id-chan {:id root-id :trail [] :num-up 0 :num-down 0})
    result-chan))

(comment

  (def config
    {:base "https://familysearch.org"
     :token "USYS795A7F999087B847126173A92C89C249_idses-prod04.a.fsglobal.net"})

  (def sconf
    {:api config
     :max-num 1000
     :max-up 7
     :max-down 1})

  (go (def user (<! (api/get-user config))))

  (:displayName user)

  (def ch (crawl sconf (:personId user)))

  (def stop false)

  (go-loop []
    (enable-console-print!)
    (prn (:display (<! ch)))
    (when-not stop (recur)))

  (set! stop true)

  )
