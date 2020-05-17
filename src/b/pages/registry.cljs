(ns b.pages.registry
  (:require [b.pages.core :as p]
            [b.pages.help :as help]))

(p/reg-page "Help" help/component 1)
(p/reg-page "Refresh" (fn [] [:div]) 2)

(defn init [db]
  (p/set-page db "Help"))
