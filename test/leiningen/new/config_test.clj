(ns luminus.config-test
  (use clojure.test
       luminus.config))

(deftest feature-precedence-test
  (is (= 0 (feature-precedence :+site [:+site])))
  (is (= -1 (feature-precedence :+clabango [:+clabango])))
  (is (= -1 (feature-precedence :+hiccup [:+hiccup])))
  (is (= -2 (feature-precedence :+auth-simple [:+auth-simple])))
  (is (= -2 (feature-precedence :+dailycred [:+dailycred])))
  (is (= -3 (feature-precedence :+dailycred-simple [:+dailycred-simple])))
  (is (= -3 (feature-precedence :+oauth [:+oauth]))))

(run-tests)

;(ns-unmap 'luminus.config-test 'feature-precedence-test)