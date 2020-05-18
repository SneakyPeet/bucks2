(ns b.domain.core
  (:require [re-frame.core :as rf]
            [b.utils :as u]
            [clojure.string :as string]))


(defn new-id []
  (str (random-uuid)))


(def default-color "#1abc9c")
(def default-colors
  #{default-color "#2ecc71" "#3498db" "#9b59b6" "#34495e" "#16a085" "#27ae60" "#2980b9" "#8e44ad" "#2c3e50"
    "#f1c40f" "#e67e22" "#e74c3c" "#ecf0f1" "#95a5a6" "#f39c12" "#d35400" "#c0392b" "#bdc3c7" "#7f8c8d"})


(rf/reg-sub
 ::colors
 (fn [_ _]
   default-colors))


(rf/reg-event-db
 ::init
 (fn [db _]
   (assoc db
          ::accounts {}
          ::tags {}
          ::entries {})))


(rf/reg-sub
 ::accounts-list
 (fn [db _]
   (->> (::accounts db {})
        vals
        (sort-by :added-timestamp))))


(rf/reg-event-db
 ::new-account
 (fn [db _]
   (let [id (new-id)]
     (assoc-in db
               [::accounts id]
               {:id id
                :name ""
                :tags #{}
                :added-timestamp (.getTime (js/Date.))}))))


(rf/reg-event-db
 ::update-account-field
 (fn [db [_ id k v]]
   (assoc-in db [::accounts id k] v)))


(rf/reg-event-db
 ::add-account-tag
 (fn [db [_ id tag-id]]
   (update-in db [::accounts id :tags] conj tag-id)))

(rf/reg-event-db
 ::remove-account-tag
 (fn [db [_ id tag-id]]
   (update-in db [::accounts id :tags] disj tag-id)))

;;;; TAGS

(defn- db->tags [db]
  (->> (::tags db {})
       (filter #(string/blank? (:name %)))
       (into {})))

(rf/reg-sub
 ::tags
 (fn [db _] (db->tags db)))


(rf/reg-sub
 ::tags-list
 (fn [db _]
   (->> (db->tags db)
        vals)))


(rf/reg-sub
 ::tags-by-name
 (fn [db _]
   (u/index-by :name (vals (db->tags db)))))


(rf/reg-event-db
 ::add-tag
 (fn [db [_ tag]]
   (assoc-in db [::tags (:id tag)] tag)))


(rf/reg-event-db
 ::set-tag-color
 (fn [db [_ id color]]
   (assoc-in db [::tags id :color] color)))


(defn new-tag [name]
  {:id (new-id)
   :name name
   :color default-color})
