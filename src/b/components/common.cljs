(ns b.components.common
  (:require [re-frame.core :as rf]
            [b.components.tags-input :as tags-input]
            [b.domain.core :as d]
            [reagent.core :as r]))


(def space-right [:span {:style {:margin-right "10px"}} ""])
(def space-left [:span {:style {:margin-left "10px"}} ""])


(defn- tag-name->tag-id [existing-tags-by-name tag-name]
  (let [tag (get existing-tags-by-name tag-name)]
    (if tag
      (:id tag)
      (let [new-tag (d/new-tag tag-name)]
        (rf/dispatch [::d/add-tag new-tag])
        (:id new-tag)))))


(defn- tags-base [& {:keys [can-add? selected-tag-ids select de-select]
                          :or {selected-tag-ids #{}
                               can-add? false}}]
  (let [*existing-tags-by-name (rf/subscribe [::d/tags-by-name])
        tags @(rf/subscribe [::d/tags])
        tag-source (fn [] (or (keys @*existing-tags-by-name) []))]
    [tags-input/component
     :can-add? can-add?
     :source tag-source
     :selected-items (map
                      #(get-in tags [% :name])
                      selected-tag-ids)
     :select (when select
               (fn [tag-name]
                 (select (tag-name->tag-id @*existing-tags-by-name tag-name))))
     :de-select (when de-select
                  (fn [tag-name]
                    (de-select (get-in @*existing-tags-by-name [tag-name :id]))))]))


(defn tags-input [& {:keys [selected-tag-ids select de-select]
                    :or {selected-tag-ids #{}}}]
  [tags-base
   :can-add? true
   :selected-tag-ids selected-tag-ids
   :select select
   :de-select de-select])


(defn tags-select [& {:keys [selected-tag-ids select de-select]
                    :or {selected-tag-ids #{}}}]
  [tags-base
   :can-add? false
   :selected-tag-ids selected-tag-ids
   :select select
   :de-select de-select])


(defn color-picker [& {:keys [selected-color pick]
                       :or {selected-color d/default-color
                            pick #(prn "selected -> " %)}}]
  (let [colors @(rf/subscribe [::d/colors])
        id (d/new-id)
        open-id (str "open" id)
        selector-id (str "select" id)
        *open? (r/atom true)]
    (r/create-class
     {:component-did-mount
      (fn [comp]
        (let [open-query (str "." open-id)
              selector-query (str "." selector-id)
              elem (js/Piklor.
                    selector-query
                    (clj->js colors)
                    (clj->js {:open open-query
                              :style {:display "flex"}}))]
          (js-invoke elem "set" selected-color false)
          (js-invoke elem "colorChosen" pick)))
      :reagent-render
      (fn [& {:keys [selected-color]
              :or {selected-color d/default-color}}]
        [:div.pick-container
         [:div.pick-button
          {:class open-id
           :style {:background-color selected-color}}]
         [:div.color-picker
          {:class  selector-id}]])})))


(defn tags-as-text [tag-ids]
  (let [tag-map @(rf/subscribe [::d/tags])]
    [:div
     (->> tag-ids
          (map #(get tag-map %))
          (map-indexed
           (fn [i tag]
             [:strong
              {:key i
               :style {:color (:color tag)}}
              (:name tag)]))
          (interpose ", ")
          (into [:div ]))]))
