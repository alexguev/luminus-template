(ns luminus.config
  (require [clojure.zip :as z]))

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

(to-map dependencies)

(defn children-deps [feat deps]
  (get deps feat))

(defn default-dep [feat deps]
    (first (keys (children-deps feat deps))))

(default-dep :+site (to-map dependencies))

(children-deps :+site (to-map dependencies))

(defn expanded? [feat feats deps]
  (let [children (children-deps feat deps)]
    (or (empty? children)
        (some (set (keys children)) feats))))
  
(expanded? :+site [:+clabango] (to-map dependencies))  
(expanded? :+site [:+clabango] 
           {:+hiccup {:+auth-db nil, :+dailycred nil}, :+clabango {:+auth-db nil, :+dailycred nil}, :+site {:+clabango {:+auth-db nil, :+dailycred nil}, :+hiccup {:+auth-db nil, :+dailycred nil}}})  

  
(defn spy [msg v]
  (println (format "%s: %s" msg v))
  v)

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


(def deps {:+dailycred nil, 
           :+auth-db nil, 
           :+hiccup {:+auth-db nil, :+dailycred nil}, 
           :+clabango {:+auth-db nil, :+dailycred nil}, 
           :+site {:+clabango {:+auth-db nil, :+dailycred nil}, :+hiccup {:+auth-db nil, :+dailycred nil}}})
(path :+dailycred deps)

(some (fn [[k v]] (and (map? v) (some #{:+hiccup} (keys v))) k) (seq deps))

(and 1 2 nil)

(some #{:a} [:a :b])

(defn expand [features]
  (loop [feats (seq features)
         deps (to-map dependencies)
         result []]
    ;(println (format "feats: %s \n deps: %s \n result: %s" feats deps result))
    (if (empty? feats)
      result
      (let [feat (first feats)
            new-feats (into (when-not (expanded? feat (into feats result) deps) 
                              (vector (default-dep feat deps)))
                            (rest feats))
            new-deps (merge deps (select-keys (children-deps feat deps) (into feats result))) ; select default dep, or dep in features
            new-result (conj result feat)]
        (recur new-feats
               new-deps
               new-result)))))

(expand [:+site :+clabango])

(defn augment [features]
  (defn path [feat]
    (let [dz (z/zipper map? (comp merge keys) identity (apply to-map dependencies))]
      ))
  (map #(vector % (path %)) features))


