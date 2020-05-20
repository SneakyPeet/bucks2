(ns b.components.account-table
  (:require [b.domain.core :as d]
            [re-frame.core :as rf]
            [b.components.common :as c]
            [b.components.form :as f]
            [clojure.set :as c.set]
            [b.domain.queries :as q]))

;;#{"36b8c47e-a6a5-4ba2-b265-d2c331c18f5a" "0ecf8ddc-5ed8-43bc-a99e-9719b21b6f40"}


(q/reg-query ::accounts-table "Accounts"
             :form {:fields
                    [{:type ::f/text
                      :id   :title
                      :label "Title"
                      :description ""}
                     {:type ::f/tags
                      :id   :header-tags
                      :label "Column Tags"
                      :description "Tags added here will appear as a column on the table"}]})


(defn component [query-fields]
  (let [{:keys [header-tags title]
         :or {header-tags #{}}} query-fields
        accounts @(rf/subscribe [::d/accounts-list])
        tags @(rf/subscribe [::d/tags])
        header-tags (->> header-tags
                         (map #(get tags %))
                         (remove nil?))
        header-tag-ids (set (map :id header-tags))]
    [:table.table.is-fullwidth.is-narrow
     [:thead
      [:tr
       [:td]
       [:<>
        (->> header-tags
             (map-indexed
              (fn [i t]
                [:td {:key i :style {:color (:color t)}} (:name t)])))]
       [:td]]]
     [:tbody
      (->> accounts
           (map-indexed
            (fn [i {:keys [tags] :as account}]
              [:tr {:key i}
               [:td (:name account)]
               (->> header-tags
                    (map-indexed
                     (fn [i t]
                       [:td {:key i}
                        (when (contains? tags (:id t))
                          [:span.icon [:i.fas.fa-check]])])))
               [:td (c/tags-as-text (c.set/difference tags header-tag-ids))]]))
           doall)]]))
