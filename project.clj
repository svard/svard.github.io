(defproject svard.github.io "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [om "0.6.2"]
                 [sablono "0.2.14"]]
  :plugins [[lein-cljsbuild "1.0.2"]]
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/main-dev.js"
                           :output-dir "resources/public/js/out"
                           :optimizations :none
                           :source-map true}}
               {:id "prod"
                :source-paths ["src/cljs" "src/rx"]
                :compiler {:output-to "resources/public/js/main.js"
                           :optimizations :advanced
                           :preamble ["react/react.min.js"
                                      "rx.lite.min.js"]
                           :externs ["react/externs/react.js"
                                     "resources/public/js/rx.lite.js"]
                           :closure-warnings {:externs-validation :off
                                              :non-standard-jsdoc :off}
                           :pretty-print false}}]})
