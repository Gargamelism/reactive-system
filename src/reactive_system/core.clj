(ns reactive-system.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [reactive-system.calculator :as calculator])
  (:gen-class))

(def cli-options
  [["-f" "--file FILE" "initial file containing comment separated values, for example \"2, 18, =2*{0}, 9, ={2}+1*5\""
    :id :file]])

(defn- required-args?
  [{:keys [file]}]
  (boolean file))

(defn- process-args
  [{:keys [file]}]
  (with-open [file-reader (clojure.java.io/reader file)]
    (let [first-line (-> (line-seq file-reader)
                         (first)
                         (str/replace #"[=\s]" "")
                         (str/split #","))]
      (if (not-empty first-line)
        {:initial-row first-line}
        {:exit-message "cannot parse file"}))))


(defn- validate-args
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) {:exit-message summary :ok? true}
      errors {:exit-message (str/join "\n" (concat ["ERROR!"] errors))}
      (required-args? options) (process-args options)
      :else {:exit-message summary})))

(defn -main
  [& args]
  (let [{:keys [initial-row
                exit-message]} (validate-args args)]
    (if exit-message
      (println exit-message)
      (calculator/init initial-row))))
