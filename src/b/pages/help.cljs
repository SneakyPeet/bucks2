(ns b.pages.help
  (:require [b.components.common :as c]
            [re-frame.core :as rf]))

#_(rf/reg-event-db
 ::test
 (fn []))

(defn component []
  [:div
   [:h1 "Help"]
   [:br]
   [c/color-picker]])
