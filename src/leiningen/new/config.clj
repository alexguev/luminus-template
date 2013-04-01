(ns luminus.config
  (require [clojure.zip :as z]))

(def dependencies [:+site [:+clabango [:+auth-db 
                                       :+dailycred]
                           :+hiccup [:+auth-db 
                                     :+dailycred]]])

(defn children-deps [feat deps] 
  (when-let [n (first (keep-indexed #(if (= feat %2) %1) deps))]
    (let [children (get deps (inc n))]
      (when (vector? children)
        children))))

(defn default-dep [feat deps]
    (first (children-deps feat deps)))

(defn expanded? [feat feats deps]
  (let [children (filter (comp not vector?) (children-deps feat deps))]
    (or (empty? children)
        (some (set children) feats))))
  
(defn spy [msg v]
  (println (format "%s: %s" msg v))
  v)
  
(defn expand [features]
  (loop [feats (seq features)
         deps dependencies
         result []]
    (if (empty? feats)
      result
      (let [feat (first feats)
            new-feats (into (when-not (expanded? feat feats deps) (vector (default-dep feat deps))) 
                            (rest feats))
            new-deps (into deps (children-deps feat deps))
            new-result (conj result feat)]
        (recur new-feats
               new-deps
               new-result)))))

(expand [:+site :+dailycred])

{k (if (vector? v) (apply to-map v) v)}

(defn to-map
  ([k v] 
   (if (vector? v)
     {k (apply to-map v)}
     {k nil, v nil}))
  ([k v & more] 
   (merge (to-map k v)
          (apply to-map more))))

(apply to-map dependencies)

(defn augment [features]
  (defn path [feat]
    (let [dz (z/zipper map? (comp merge keys) identity (apply to-map dependencies))]
      ))
  (map #(vector % (path %)) features))


