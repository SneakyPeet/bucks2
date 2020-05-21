(ns b.pages.help
  (:require [b.components.common :as c]
            [b.components.account-table :as at]
            [b.components.form :as f]
            [b.components.queries :as q]
            [b.components.resizeable-grid :as rg]
            [b.components.dashboard :as dash]
            [re-frame.core :as rf]))

#_(rf/reg-event-db
 ::test
 (fn []))

(defn component []
  [:div
   [:h1 "Help"]
   [:br]
   #_[c/color-picker]
   #_[at/component {}]
   #_[f/form [:foo :bar] f/example-form :debug? true]
   #_[q/query-selector prn]
   #_[rg/component
    {:layout-map
     {"a" {:i "a" :x 0 :y 0 :w 1 :h 2 :static true}
      "b" {:i "b" :x 1 :y 0 :w 3 :h 2 :minW 2 :maxW 4}
      "c" {:i "c" :x 4 :y 0 :w 1 :h 2}}
     :changed prn}
    [:div.box {:key "a"} "a"]
    [:div.box {:key "b"} "b"]
      [:div.box {:key "c"} "c"]]
   #_[dash/component "12345"]
   [dash/dashboards]
   ])
