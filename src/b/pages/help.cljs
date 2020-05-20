(ns b.pages.help
  (:require [b.components.common :as c]
            [b.components.account-table :as at]
            [b.components.form :as f]
            [re-frame.core :as rf]))

#_(rf/reg-event-db
 ::test
 (fn []))

(defn component []
  [:div
   [:h1 "Help"]
   [:br]
   [c/color-picker]
   [at/component {}]
   [f/form [:foo :bar] f/example-form :debug? true]])
