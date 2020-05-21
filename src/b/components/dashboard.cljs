(ns b.components.dashboard
  (:require [re-frame.core :as rf]
            [b.domain.queries :as d.q]
            [b.components.queries :as q]
            [b.domain.core :as d]
            [b.components.form :as f]
            [b.components.resizeable-grid :as grid]
            [b.utils :as u]))


(d.q/reg-query ::text-block "Text Bloc"
             :form {:fields
                    [{:type ::f/text
                      :id   :text
                      :label "Text"
                      :description ""}]})


(defn- new-dash-header []
  (q/make-query ::text-block {:text "New Dashboard"}))


(rf/reg-event-db
 ::add-new-dashboard
 (fn [db _]
   (let [id (d/new-id)
         header (new-dash-header)
         header-layout (grid/new-component-layout-config {} {:id (:id header)})]
     (-> db
         (assoc ::current-dash id)
         (assoc-in [::dashboards id]
                   {:id id
                    :pos (count (::dashboards db))
                    :title "New Dash"
                    :queries {(:id header) {:query header
                                            :layout header-layout}}})))))


(rf/reg-event-db
 ::show-dashboard
 (fn [db [_ id]]
   (assoc db ::current-dash id)))


(rf/reg-sub
 ::dashboard-list
 (fn [db _]
   (->> (::dashboards db {})
        vals
        (sort-by :pos))))


(rf/reg-sub
 ::current-dash-id
 (fn [db _]
   (::current-dash db)))


(rf/reg-sub
 ::current-dash
 (fn [db _]
   (get (::dashboards db) (::current-dash db))))


(rf/reg-event-db
 ::set-current-dash
 (fn [db [_ id]]
   (assoc db ::current-dash id)))


(rf/reg-event-db
 ::update-layout
 (fn [db [_ layout-map]]
   (let [layouts (vals layout-map)
         dash-id (::current-dash db)
         dash (get db dash-id)]
      (reduce
       (fn [db layout]
         (let [q-id (:i layout)]
           (assoc-in db [::dashboards dash-id :queries q-id :layout]
                     (->> layout
                          (filter #(some? (last %)))
                          (into {})))))
       db
       layouts))))


(rf/reg-sub
 ::show-query-modal?
 (fn [db _]
   (::show-query-modal? db false)))


(rf/reg-event-db
 ::toggle-query-modal
 (fn [db _]
   (update db ::show-query-modal? not)))


(defn- queries-modal []
  (when @(rf/subscribe [::show-query-modal?])
    [:div.modal.is-active
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Pick a query to add"]
       [:button.delete {:on-click #(rf/dispatch [::toggle-query-modal])}]]
      [:section.modal-card-body
       [q/query-selector
        (fn [query-data]
          (prn query-data)
          (rf/dispatch [::toggle-query-modal]))]]
      [:footer.modal-card-foot]]
     ]))


(defn- dashboard []
  (let [current-dash @(rf/subscribe [::current-dash])
        queries (->> (:queries current-dash)
                     vals)
        queries-components (->> queries
                                (map (fn [{:keys [query]}]
                                       [:div.box {:key (:id query)} (str (:query-key query))])))
        grid-layout (->> queries
                         (map :layout)
                         (u/index-by :i))]
    (when current-dash
      [:div
       [grid/component
        {:layout-map grid-layout
         :changed #(rf/dispatch [::update-layout %])}
        queries-components]
       ])))


(defn dashboards []
  (let [dashboards @(rf/subscribe [::dashboard-list])]
    [:div
     [:a {:on-click #(rf/dispatch [::add-new-dashboard])} "new dash "]
     [:a {:on-click #(rf/dispatch [::toggle-query-modal])} "show queries modal"]
     [queries-modal]
     (->> dashboards
          (map-indexed
           (fn [i d]
             [:a {:key i
                  :on-click #(rf/dispatch [::set-current-dash (:id d)])}
              (:title d) " "])))
     [dashboard]]))
