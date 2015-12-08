(ns om-tutorial.core)

(defn error-401 []
  (let [e (js/Error. "Unauthorized")]
    (aset e "status" 401)
    e))

(defn error-invalid [xhr]
  (let [message
        (str
         "Invalid status code: " (.-status xhr)
         "\n" (.stringify js/JSON (.-response xhr)))
        e (js/Error. message)]
    e))

(defn handle-response [xhr resolve reject]
  (cond
     (= xhr.status 204) (resolve nil)
     (= xhr.status 200) (resolve (.-response xhr))
     (= xhr.status 401) (reject (error-401))
     :default (reject (error-invalid xhr))))

(defn get [& {:keys [method url headers body type]}]
  (js/Promise.
   (fn [resolve reject]
     (let [xhr (js/XMLHttpRequest.)]
       (.open xhr method url)
       (dorun
        (map
         (fn [[key value]]
           (.setRequestHeader xhr key value))
         headers))
       (aset xhr "responseType" type)
       (aset xhr "onload" #(handle-response xhr resolve reject))
       (aset xhr "onerror" #(reject (js/Error. url)))
       (if body
         (.send xhr body)
         (.send xhr))
       ))))
