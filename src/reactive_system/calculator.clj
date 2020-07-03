(ns reactive-system.calculator
  (:require [clojure.string :as str]))

(def choices
  "Please choose one of the following:
    a. print current state
    b. change value of cell
    q. quit")

(defn- valid-cell?
  [cell]
  (or (and (str/includes? cell "{")
           (re-find #"\{\d+\}" cell))
      (re-find #"\d+" cell)))

(defn- remove-spaces
  [string]
  (str/replace string #"[\s]" ""))

(defn- requires-calc?
  [cell]
  (when cell
    (re-find #"[\{\}+\-\*/]" cell)))

(def referenced-cell-idx-re #"(.*?)\{(\d+)\}(.*)")          ;

(defn- build-new-cell
  [found row final-row]
  (reduce
    (fn [final-cell current-part]
      (if (requires-calc? current-part)
        (str final-cell current-part)
        (str final-cell (or
                          (nth final-row (Integer. current-part))
                          (nth row (Integer. current-part))))))
    ""
    found))

(defn- realise-cell
  [initial-row final-row current-cell]
  (loop [updated-cell current-cell
         current-depth 0
         max-depth (count initial-row)]
    (let [found (->> (re-find referenced-cell-idx-re updated-cell)
                     (rest)
                     (filter not-empty))]

      (cond
        (> current-depth max-depth) (do
                                      (println "circular reference detected at" current-cell)
                                      (conj final-row nil))
        (not-empty found) (recur (build-new-cell found initial-row final-row)
                                 (inc current-depth)
                                 max-depth)
        :else (conj final-row updated-cell)))))

(defn- first-infix->parts
  [infix]
  (when infix
    (rest (re-find #"(\d+)([+\-\*/])(\d+)(.*)" infix))))

(defn- rest-infix->parts
  [infix]
  (when infix
    (rest (re-find #"([+\-\*/])(\d+)(.*)" infix))))

(defn- infix-parts->prefix
  [operand-a operator operand-b]
  (str "(" operator " " operand-a " " operand-b ")"))

(defn- infix->prefix
  [infix]
  (let [[operand-a operator operand-b remaining-infix] (first-infix->parts infix)]
    (loop [remaining remaining-infix
           final (infix-parts->prefix operand-a operator operand-b)]
      (if (empty? remaining)
        final
        (let [[operator operand-b rest] (rest-infix->parts remaining)]
          (recur rest (infix-parts->prefix operand-b operator final)))))))

(defn- calc-cell-val
  [cell]
  (if (requires-calc? cell)
    (-> cell
        (infix->prefix)
        (read-string)
        (eval)
        (str))
    cell))

(defn- process-row
  [row]
  (let [relevant-cells (filter valid-cell? row)]
    (->> relevant-cells
         (reduce (partial realise-cell relevant-cells) [])
         (filter not-empty)
         (map calc-cell-val))))

(defn- print-row
  [row]
  (if (empty? row)
    (println [])
    (let [final-row (process-row row)]
      (println (->> (map-indexed (fn [idx cell]
                                   (str "[" idx ": " cell "]"))
                                 final-row)
                    (str/join ", ")))))
  row)

(defn- get-update-index
  [max-idx]
  (println "Please enter the index you desire to change:")
  (loop [index (read-line)]
    (cond
      (re-find #"[^\d]" index) (do
                                 (println index "is not a valid index, please enter a number")
                                 (recur (read-line)))
      (or (> (Integer. index) max-idx)
          (> 0 (Integer. index))) (do
                                    (println index "is out of bounds, please enter a number between 0 and" max-idx)
                                    (recur (read-line)))
      :else (Integer. index))))

(defn- get-desired-change
  []
  (println "Please enter the info you wish to change:")
  (loop [new-cell (remove-spaces (read-line))]
    (if (re-find #"[^\d\{\}+\-\*/]" new-cell)
      (do
        (println new-cell "is not a valid cell, please enter a valid cell format")
        (recur (read-line)))
      new-cell)))

(defn- update-row
  [row]
  (let [idx (get-update-index (count row))
        change (get-desired-change)]
    (assoc row idx change)))

(defn- unknown-choice
  [choice row]
  (println "unknown choice" choice)
  row)

(defn init
  [row]
  (loop [current-row row]
    (println choices)
    (let [choice (read-line)
          new-row (case choice
                    "a" (print-row current-row)
                    "b" (update-row current-row)
                    "q" nil
                    (unknown-choice choice current-row))]
      (if new-row
        (recur new-row)
        (println "good-bye! :(")))))