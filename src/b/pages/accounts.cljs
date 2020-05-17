(ns b.pages.accounts
  (:require [b.domain.core :as d]
            [re-frame.core :as rf]
            [b.components.tags-input :as tags-input]))


(defn- new-account-button []
  [:a
   {:on-click #(rf/dispatch [::d/new-account])}
   "add"])


(defn- add-tag
  "adds a tag to an account, creates the tag if it does not exist"
  [existing-tags-by-name account-id tag-name]
  (let [tag (get existing-tags-by-name tag-name)]
    (if tag
      (rf/dispatch [::d/add-account-tag account-id (:id tag)])
      (let [new-tag (d/new-tag tag-name)]
        (rf/dispatch [::d/add-tag new-tag])
        (rf/dispatch [::d/add-account-tag account-id (:id new-tag)])))))


(defn- de-select-tag [existing-tags-by-name account-id tag-name]
  (let [tag-id (get-in existing-tags-by-name [tag-name :id])]
    (rf/dispatch [::d/remove-account-tag account-id tag-id])))


(defn- accounts []
  (let [coll @(rf/subscribe [::d/accounts-list])
        *existing-tags-by-name (rf/subscribe [::d/tags-by-name])
        tags @(rf/subscribe [::d/tags])
        tag-source (fn [] (or (keys @*existing-tags-by-name) []))]
    [:table.table.is-fullwidth
     [:thead
      [:tr
       [:th "Account "
        [:small.has-text-light-grey.has-text-weight-normal
         [new-account-button]]]
       [:th "Tags"]]]
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
                [tags-input/component
                 :can-add? true
                 :source tag-source
                 :selected-items (map
                                  #(get-in tags [% :name])
                                  (:tags account))
                 :select #(add-tag @*existing-tags-by-name id %)
                 :de-select #(de-select-tag @*existing-tags-by-name id %)]]])))
      [:tr {:col-span 2}
       [:td [new-account-button]]]]]))


(defn tags []
  (let [coll @(rf/subscribe [::d/tags-list])]
    [:ul.content
     (->> coll
          (map-indexed
           (fn [i t]
             [:li {:key i} (:name t)])))]))


(defn component []
  [:div
   [:h1.title "Accounts"]
   [accounts]
   [:h1.title "Tags"]
   [tags]])
