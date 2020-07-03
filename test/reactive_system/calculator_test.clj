(ns reactive-system.calculator-test
  (:require [clojure.test :refer :all]
            [reactive-system.calculator :as calculator]))

(def ^:private scenarios [{:name     "process-row"
                           :fn       #'calculator/process-row
                           :args     [["1" "{0}+1" "3"]]
                           :expected (seq ["1" "2" "3"])}
                          {:name     "calc-cell-val calculation"
                           :fn       #'calculator/calc-cell-val
                           :args     ["1+1"]
                           :expected "2"}
                          {:name     "calc-cell-val no calculation"
                           :fn       #'calculator/calc-cell-val
                           :args     ["1"]
                           :expected "1"}
                          {:name     "infix->prefix long"
                           :fn       #'calculator/infix->prefix
                           :args     ["1+2-2*5"]
                           :expected "(* 5 (- 2 (+ 1 2)))"}
                          {:name     "realise-cell"
                           :fn       #'calculator/realise-cell
                           :args     [["1" "10+{0}"] ["1"] "10+{0}"]
                           :expected ["1" "10+1"]}])


(deftest core-unit-tests
  (doseq [{:keys [name fn args expected]} scenarios]
    (testing name
      (let [res (apply fn args)]
        (is (= res expected))))))
