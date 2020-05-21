(ns b.pages.help
  (:require [b.components.common :as c]
            [b.components.account-table :as at]
            [b.components.form :as f]
            [b.components.queries :as q]
            [b.components.resizeable-grid :as rg]
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
   [rg/component]])
