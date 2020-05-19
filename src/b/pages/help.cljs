(ns b.pages.help
  (:require [b.components.common :as c]
            [b.components.account-table :as at]
            [re-frame.core :as rf]))

#_(rf/reg-event-db
 ::test
 (fn []))

(defn component []
  [:div
   [:h1 "Help"]
   [:br]
   [c/color-picker]
   [at/component {}]])
