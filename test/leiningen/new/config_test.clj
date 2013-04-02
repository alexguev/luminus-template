(ns luminus.config-test
  (use clojure.test
       luminus.config))

(deftest expand-test
  (is (= [:+site :+clabango :+auth-db] (expand [:+site])))
  (is (= [:+site :+clabango :+dailycred] (expand [:+site :+dailycred])))
  (is (= [:+site :+hiccup :+dailycred] (expand [:+site :+hiccup :+dailycred])))
  (is (= [:+site :+hiccup :+auth-db] (expand [:+site :+hiccup])))
  (is (= [:+site :+hiccup :+auth-db :+other] (expand [:+site :+hiccup :+other])))
  (is (= [:+site :+clabango :+auth-db :+other] (expand [:+site :+other]))))

(deftest feature-dep-test
  (def deps {:+clabango {:+auth-db nil, :+dailycred nil}, 
             :+site {:+clabango {:+auth-db nil, :+dailycred nil}, :+hiccup {:+auth-db nil, :+dailycred nil}}})
  (is (= :+clabango (feature-dep :+site [] deps)))
  (is (= :+hiccup (feature-dep :+site [:+hiccup] deps)))
  (is (= :+auth-db (feature-dep :+clabango [] deps)))
  (is (= :+dailycred (feature-dep :+clabango [:+dailycred] deps))))

(def deps1 (to-map dependencies))

(feature-dep :+clabango [] deps1)

(deftest path-test
  (def deps {:+dailycred nil, 
             :+clabango {:+auth-db nil, :+dailycred nil}, 
             :+site {:+clabango {:+auth-db nil, :+dailycred nil}, :+hiccup {:+auth-db nil, :+dailycred nil}}})
  (is (= [:+site] (path :+clabango deps)))
  (is (= [:+site :+clabango] (path :+dailycred deps)))
  (is (= [] (path :+site deps))))


(run-tests)