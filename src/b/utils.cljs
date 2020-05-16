(ns b.utils
  (:require [re-frame.core :as rf]
            [clojure.string :as string]))

(defonce ^:private *dev? (atom false))

(defn dev? [] @*dev?)

(defn set-if-dev []
  (when (string/starts-with? (.. js/window -location -href) "http://localhost")
    (reset! *dev? true)))


(defn index-by [f coll]
  (->> coll
       (map (juxt f identity))
       (into {})))
