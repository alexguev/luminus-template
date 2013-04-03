(ns luminus.config)

(defn handler [name path] (println name path))

(def dependency-tree 
  {:deps
   [{:name :+site
     :handler handler
     :default true
     :deps [{:name :+clabango
             :handler handler
             :default true
             :deps [{:name :+auth-db
                     :handler handler
                     :default true}
                    {:name :+auth-dailycred
                     :handler handler}]}
            {:name :+hiccup
             :handler handler
             :deps [{:name :+auth-db
                     :handler handler
                     :default true}
                    {:name :+auth-dailycred
                     :handler handler}]}]}]})

(defn filter-deps [features deps]
  (or (not-empty (filter #(some #{(:name %)} features) deps))
      (filter :default deps)))

(defn prune [features]
  (clojure.walk/prewalk
   (fn [item]
     (if (map? item)
       (update-in item [:deps] (partial filter-deps features))
       item))     
   dependency-tree))

(defn process-features 
  ([features] (add-features [] features))
  ([path features]
   (doseq [{:keys [handler name deps]} features]
     (if handler (handler name path))
     (add-features (conj path name) deps)
   )))

(->> [:+site :+auth-dailycred] prune :deps process-features)


(prune [:+site])