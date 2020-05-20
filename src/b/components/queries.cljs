(ns b.components.queries
  (:require [b.domain.queries :as q]
            [b.domain.core :as d]
            [re-frame.core :as rf]
            [b.components.form :as f]))

(q/reg-query :one "One" :group "Accounts" :description "You Ma en pa")
(q/reg-query :one1 "One1")
(q/reg-query :one2 "One2" :group "Accounts")
(q/reg-query :one3 "One3")
(q/reg-query :one4 "One4" :group "Foo")


(rf/reg-sub
 ::selector-state
 (fn [db _]
   (::selector-state db ::select-query)))


(rf/reg-sub
 ::selected-query
 (fn [db _]
   (::selected-query db)))


(rf/reg-sub
 ::query-fields
 (fn [db _]
   (::query-fields db {})))


(rf/reg-event-db
 ::select-query
 (fn [db [_ query]]
   (assoc db
          ::selected-query query
          ::selector-state ::query-form)))


(rf/reg-event-db
 ::reset
 (fn [db [_ query]]
   (-> db
       (dissoc ::selected-query ::query-fields)
       (assoc ::selector-state ::select-query))))


(defn- finished-query []
  (let [query-key (:query-key @(rf/subscribe [::selected-query]))
        fields @(rf/subscribe [::query-fields])
        id (d/new-id)]
    {:id id
     :query-key query-key
     :fields fields}))


(defn- select-query []
  (let [query-groups @(rf/subscribe [::q/queries-by-group])]
    [:<>
     (->> query-groups
          (map-indexed
           (fn [i {:keys [group queries]}]
             [:div {:key i}
              [:p.heading group]
              [:div.columns.is-mobile.is-multiline
               (->> queries
                    (map-indexed
                     (fn [j {:keys [title description] :as query}]
                       (let [select #(rf/dispatch [::select-query query])]
                         [:div.column.is-one-third
                          {:key (str i j)}
                          [:div.box {:on-click select :style {:cursor "pointer"}}
                           [:p.heading [:a {:on-click select}
                                        title]]
                           [:p description]]]))))]
              [:br]])))]))


(defn query-form [save-query]
  (let [query @(rf/subscribe [::selected-query])]
    [:div
     [:h1.heading (:title query)]
     (when-let [d (:description query)] [:p d])
     [f/form [::query-fields] (:form query) :debug? true]
     [:div.field.is-horizontal
      [:div.field-label.is-normal]
      [:div.field-body
       [:div.control
        [:div.buttons
         [:button.button.is-primary
          {:on-click #(do
                        (when save-query (save-query (finished-query)))
                        (rf/dispatch [::reset]))}
          "Create"]
         [:button.button
          {:on-click #(rf/dispatch [::reset])}
          "back"]]]]]]))


(defn query-selector [save-query]
  (let [query-groups @(rf/subscribe [::q/queries-by-group])
        selector-state @(rf/subscribe [::selector-state])]
    (case selector-state
      ::select-query [select-query]
      ::query-form [query-form save-query]
      [:div.has-text-danger "Unknown State"])))
