(ns reactive-system.core-test
  (:require [clojure.test :refer :all]
            [reactive-system.core :as reactive-system]))

(def ^:private scenarios [{:name     "required args true"
                           :fn       #'reactive-system/required-args?
                           :args     [{:file "file_name.txt"}]
                           :expected true}
                          {:name     "required args false"
                           :fn       #'reactive-system/required-args?
                           :args     [{:file nil}]
                           :expected false}
                          {:name     "read file success"
                           :fn       #'reactive-system/process-args
                           :args     [{:file "resources\\test\\test_file.txt"}]
                           :expected ["1" "2" "3"]}])

(deftest core-unit-tests
  (doseq [{:keys [name fn args expected]} scenarios]
    (testing name
      (let [res (apply fn args)]
        (is (= res expected))))))