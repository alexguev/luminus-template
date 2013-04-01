(ns luminus.config
  (use [clojure.walk :only [prewalk]]))

(def dependency-tree {:+site {:+clabango ^:d {:+auth-simple ^:d {}
                                                    :+dailycred {:+dailycred-simple ^:d {}
                                                                 :+oauth {}}}
                              :+hiccup {:+auth-simple ^:d {}
                                        :+dailycred {:+dailycred-simple ^:d {}
                                                     :+oauth {}}}}})

; tbd: use expanded-features
(defn feature-precedence [feature expanded-features]
  (loop [deps dependency-tree
         level 0]
    (println (keys deps))
    (if (empty? deps)
      0
      (if (some (partial = feature) (keys deps)) 
        level
        (recur (apply merge (vals deps))
               (dec level))))))

;fixme
(defn some-expandable-feature [features]
  (some (fn [feature] 
          (and (not (contains? result feature)) 
               (when-let [dependencies (get dependency-tree feature)]
                 [feature dependencies])) )
        features))


(some-expandable-feature [:site])

;tbd ask is there any expandable ? if yes, then expand, otherwise we are done
(defn expand 
  "'features' is a set features, features are represented using keywords."
  [features]
  (loop [result (set features)]
    (if-let [[feature dependencies] (some-expandable-feature features result)]
      result
      "false")))

(defmulti add-feature (fn [feature previous-features] 
                        [feature previous-features]))
  
(defmethod add-feature [:+clabango []] [_ []]
  [["+clabango"]])

(defmethod add-feature [:+clabango [:+site]] [_ _]
  (reduce into [(add-feature :+clabango []) 
                ["+site"]]))

(defmethod add-feature [:+hiccup []] [_ []]
  ["+hiccup"])

(defmethod add-feature [:+hiccup [:+site]] [_ _]
  (reduce into [(add-feature :+hiccup []) 
                ["+site"]]))
