(ns b.deploy
  (:require [clojure.string :as string]))

(def ^:private html-path "deploy/index.html")
(def ^:private js-dir "deploy/cljs-out/")
(def ^:private dev-js-file-name "dev-main.js")
(defn- js-path [f] (str js-dir f))


(defn- hash-js []
  (let [fp (str (.getTime (java.util.Date.)) ".js")]
    (->> (slurp (js-path dev-js-file-name))
         (spit (js-path fp)))

    (as-> (slurp html-path) content
      (string/replace content (re-pattern dev-js-file-name) fp)
      (spit html-path content))))

(defn -main [& params]
  (hash-js))
