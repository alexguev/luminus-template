(ns luminus.config)

(def dependencies [:+site [:+clabango [:+auth-db 
                                       :+dailycred]
                           :+hiccup [:+auth-db 
                                     :+dailycred]]])

(defn to-map
  ([coll] 
   (apply to-map coll))
  ([k v]
   (if (vector? v)
     (array-map k (apply to-map v))
     (array-map k nil, v nil)))
  ([k v & more]
   (merge (apply to-map more)
          (to-map k v))))

(defn feature-dep [feat feats deps]
  (let [children (get deps feat)]
    (when (seq children)
      (or (some (set (keys children)) feats)
          (first (keys children))))))
  
(defn path [feature deps]
  (when (contains? deps feature)
    (loop [path []
           feat feature]
      (if-let [parent (some (fn [[k v]] 
                           (when (and (map? v) (some #{feat} (keys v))) 
                             k))
                         (seq deps))]
        (recur (into [parent] path) parent)
        path))))

(defn expand [features]
  "a convenient property of this function is that, after expanding, features will be sorted by order or execution"
  (loop [feats (seq features)
         deps (to-map dependencies)
         result []]
    ;(println (format "feats: %s \n deps: %s \n result: %s" feats deps result))
    ;(Thread/sleep 3000)
    (if (empty? feats)
      result
      (let [feat (first feats)
            feat-dep (feature-dep feat (concat feats result) deps)
            new-feats (if feat-dep 
                        (cons feat-dep (rest feats)) 
                        (rest feats)) 
            new-deps (merge deps (select-keys (get deps feat) [feat-dep]))
            new-result (-> #{feat} (remove result) vec (conj feat))] ;(if-not (some #{feat} result) (conj result feat) result)
        (recur new-feats
               new-deps
               new-result)))))

(defn augment-with-path [features]
  (second (reduce (fn [[deps result] feat] 
                    [(merge deps (select-keys (get deps feat) features)) 
                     (conj result [feat (path feat deps)])]) 
                  [(to-map dependencies) []] 
                  features)))

;(augment-with-path [:+site :+clabango :+dailycred])