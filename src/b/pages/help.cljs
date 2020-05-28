(ns b.pages.help
  (:require [b.components.common :as c]
            [b.components.account-table :as at]
            [b.components.form :as f]
            [b.components.queries :as q]
            [b.components.resizeable-grid :as rg]
            [b.components.dashboard :as dash]
            [b.domain.entries :as d.e]
            [re-frame.core :as rf]))

#_(rf/reg-event-db
 ::test
 (fn []))

(defn entries-overview []
  [:table.table.is-fullwidth
   [:tbody

    (->> d.e/entry-config
         vals
         (group-by :group)
         (sort-by (fn [[k _]] (get-in d.e/groups [k :pos])))
         (map-indexed
          (fn [i [k entries]]
            [:<> {:key i}
             [:tr
              [:td (cond-> {:col-span 2}
                     (> i 0) (assoc-in [:style :padding-top] "35px"))
               [:strong k] [:div [:small (get-in d.e/groups [k :description])]]]]
             (->> entries
                  (sort-by :pos)
                  (map-indexed
                   (fn [j e]
                     [:tr {:key (str i j)}
                      [:td (:name e)]
                      [:td (:description e)]])))])))]])

(defn component []
  [:div
   [:h1 "Help"]
   [entries-overview]
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
