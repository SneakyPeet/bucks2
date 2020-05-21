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


(defn make-query [query-key fields]
  {:id (d/new-id)
   :query-key query-key
   :fields fields})


(defn- finished-query []
  (let [query-key (:query-key @(rf/subscribe [::selected-query]))
        fields @(rf/subscribe [::query-fields])]
    (make-query query-key fields)))


(defn- select-query []
  (let [query-groups @(rf/subscribe [::q/queries-by-group])]
    [:<>
     (->> query-groups
          (map-indexed
           (fn [i {:keys [group queries]}]
             [:div.content {:key i}
              [:p.heading group]
              [:div
               (->> queries
                    (map-indexed
                     (fn [j {:keys [title description] :as query}]
                       (let [select #(rf/dispatch [::select-query query])]
                         [:div.column.is-half
                          {:key (str i j)}
                          [:p.title.is-5 [:a {:on-click select}
                                       title]]
                          [:p.subtitle.is-7 description]]))))]])))]))


(defn query-form [save-query]
  (let [query @(rf/subscribe [::selected-query])]
    [:div
     [:div
      [:h1.title (:title query)]]
     (when-let [d (:description query)] [:h2.subtitle d])
     [f/form [::query-fields] (:form query) #_#_:debug? true]
     [:div.field.is-grouped.is-grouped-right
      [:p.control
       [:button.button.is-primary
        {:on-click #(do
                      (when save-query (save-query (finished-query)))
                      (rf/dispatch [::reset]))}
        "Add"]]
      [:p.control
       [:button.button
        {:on-click #(rf/dispatch [::reset])}
        "Back"]]]]))


(defn query-selector [save-query]
  (let [query-groups @(rf/subscribe [::q/queries-by-group])
        selector-state @(rf/subscribe [::selector-state])]
    (case selector-state
      ::select-query [select-query]
      ::query-form [query-form save-query]
      [:div.has-text-danger "Unknown State"])))
