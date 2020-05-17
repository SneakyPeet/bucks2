(ns b.pages.help
  (:require [b.components.tags-input :as tags]
            [re-frame.core :as rf]))

#_(rf/reg-event-db
 ::test
 (fn []))

(defn component []
  [:div
   [:h1 "Help"]
   [:br]
   [tags/component
    :items ["one" "two" "three"]
    :selected-items ["one"]
    :select prn
    :de-select #(prn "d" %)
    :can-add? true]])
