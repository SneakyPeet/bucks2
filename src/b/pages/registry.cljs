(ns b.pages.registry
  (:require [b.pages.core :as p]
            [b.domain.queries :as q]
            [b.pages.help :as help]
            [b.pages.accounts :as accounts]))

(p/reg-page "Accounts" accounts/component 0)
(p/reg-page "Help" help/component 1)
(p/reg-page "Refresh" (fn [] [:div]) 2)

(defn init [db]
  (p/set-page db "Help"))
