(ns b.pages.accounts
  (:require [b.domain.core :as d]
            [re-frame.core :as rf]
            [b.components.common :as c]))


(defn- new-account-button []
  [:a
   {:on-click #(rf/dispatch [::d/new-account])}
   "add"])


(defn- accounts []
  (let [coll @(rf/subscribe [::d/accounts-list])]
    [:table.table.is-fullwidth.is-narrow
     [:thead
      [:tr
       [:td "Account"]
       [:td "Tags"]]]
     [:tbody
      (->> coll
           (map
            (fn [{:keys [id] :as account}]
              [:tr {:key (:added-timestamp account)}
               [:td
                [:input.input
                 {:type "text"
                  :placeholder "Add account name"
                  :value (:name account)
                  :on-change
                  #(rf/dispatch
                    [::d/update-account-field id :name (.. % -target -value)])}]]
               [:td
                [c/tags-input
                 :selected-tag-ids (:tags account)
                 :select #(rf/dispatch [::d/add-account-tag id %])
                 :de-select #(rf/dispatch [::d/remove-account-tag id %])]]])))
      [:tr {:col-span 2}
       [:td [new-account-button]]]]]))


(defn tags []
  (let [coll @(rf/subscribe [::d/tags-list])]
    [:ul.content
     (->> coll
          (map-indexed
           (fn [i t]

             [:li {:key i :style {:display "flex" :flex-diraction "row"}}
              (:name t)
              [c/color-picker
               :selected-color (:color t)
               :pick #(rf/dispatch [::d/set-tag-color (:id t) %])]])))]))


(defn component []
  [:div.columns
   [:div.column
    [:h1.heading "Accounts"]
    [accounts]]
   [:div.column
    [:h1.heading "Tags"]
    [tags]]])
