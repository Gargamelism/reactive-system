(defproject reactive-system "0.1.1-SNAPSHOT"
  :description "Reactive System"
  :url "https://github.com/Gargamelism/reactive-system"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.7"]]
  :main reactive-system.core
  :aot [reactive-system.core])
