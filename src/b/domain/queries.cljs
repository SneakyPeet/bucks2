(ns b.domain.queries
  (:require [re-frame.core :as rf]))

(def ^:private *queries (atom {}))


(defn reg-query [query-key title & {:keys [group description form]
                             :or {group "General" description "" form {}}}]
  (swap! *queries assoc query-key {:query-key query-key
                                   :title title
                                   :group group
                                   :description description
                                   :form form}))


(rf/reg-sub
 ::queries-by-group
 (fn [_ _]
   (->> @*queries
        vals
        (group-by :group)
        (map (fn [[g q]]
               {:group g
                :queries q})))))
