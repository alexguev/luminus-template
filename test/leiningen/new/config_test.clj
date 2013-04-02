(ns luminus.config-test
  (use clojure.test
       luminus.config))

(deftest expand-test
  (is (= [:+site :+clabango :+auth-db] (expand [:+site])))
  (is (= [:+site :+clabango :+dailycred] (expand [:+site :+dailycred])))
  (is (= [:+site :+hiccup :+dailycred] (expand [:+site :+hiccup :+dailycred])) "mmm ... dailycred moves")
  (is (= [:+site :+hiccup :+auth-db] (expand [:+site :+hiccup])))
  (is (= [:+site :+hiccup :+auth-db :+other] (expand [:+site :+hiccup :+other])))
  (is (= [:+site :+clabango :+auth-db :+other] (expand [:+site :+other]))))

(run-tests)