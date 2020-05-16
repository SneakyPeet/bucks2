(ns b.pages.core
  (:require [re-frame.core :as rf]
            [b.utils :as u]))

(defonce ^:private *pages (atom {}))

(defn reg-page [title component order]
  (swap! *pages assoc title {:title title :component component :order order}))


(defn set-page [db p] (assoc db ::page p))


(rf/reg-event-db
 ::page
 (fn [db [_ p]]
   (assoc db ::page p)))


(rf/reg-sub
 ::current-page
 (fn [db _]
   (::page db)))


(defn component []
  (let [p @(rf/subscribe [::current-page])
        pm @*pages
        comp (get pm p [:span])]
    [:div
     [:div.tabs
      [:ul
       (->> pm
            vals
            (sort-by :order)
            (map-indexed
             (fn [i {:keys [title]}]
               [:li {:key i
                     :class (when (= p title) :is-active)}
                [:a
                 {:on-click #(rf/dispatch [::page title])}
                 title]])))]]
     [(:component comp)]]))
