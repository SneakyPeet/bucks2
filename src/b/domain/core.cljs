(ns b.domain.core
  (:require [re-frame.core :as rf]
            [b.utils :as u]))


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
        (sort-by :added-timestamp)
        reverse)))


(rf/reg-event-db
 ::new-account
 (fn [db _]
   (let [id (random-uuid)]
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

(rf/reg-sub
 ::tags
 (fn [db _] (::tags db {})))


(rf/reg-sub
 ::tags-list
 (fn [db _]
   (->> (::tags db {})
        vals)))


(rf/reg-sub
 ::tags-by-name
 (fn [db _]
   (u/index-by :name (vals (::tags db {})))))


(rf/reg-event-db
 ::add-tag
 (fn [db [_ tag]]
   (assoc-in db [::tags (:id tag)] tag)))


(defn new-tag [name]
  {:id (random-uuid)
   :name name
   :color "red"})
