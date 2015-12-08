(defproject om-tutorial "0.1.0-SNAPSHOT"
  :description "My first Om program!"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [cljs-http "0.1.38"]
                 [prismatic/schema "1.0.3"]
                 [reagent "0.5.1"]
                 [re-frame "0.6.0"]
                 [binaryage/devtools "0.4.1"]
                 [org.omcljs/om "1.0.0-alpha22"]
                 [com.cemerick/piggieback "0.2.1"]
                 [figwheel-sidecar "0.5.0-SNAPSHOT" :scope "test"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
