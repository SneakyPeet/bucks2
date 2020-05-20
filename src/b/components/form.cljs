(ns b.components.form
  (:require [re-frame.core :as rf]
            [b.components.common :as c]))


(rf/reg-sub
 ::form-data
 (fn [db [_ path]]
   (get-in db path {})))


(rf/reg-event-db
 ::set-form-field
 (fn [db [_ path field transform default value]]
   (let [p (vec (conj (vec path) field))
         transform (or transform (fn [_ v] v))]
     (cond-> db
       (nil? (get-in db p))
       (assoc-in p default)

       true
       (update-in p #(transform % value))))))



(def example-form
  {:fields
   [{:type ::tags
     :id   :tags
     :label "Test Tags"
     :description "mini description"}
    {:type ::text
     :id   :title
     :label "Test Title"
     :description "mini description 2"}]})


(defmulti ^:private render-field (fn [answer f v]
                                   (:type f)))


(defmethod render-field :default [_ f _]
  [:div "No render defined for field type" (str (:type f))])


(defn form
  [data-path form-config & {:keys [debug?]}]
  (let [data @(rf/subscribe [::form-data data-path])
        answer (fn [field]
                 (fn [a & {:keys [transform default]
                                 :or {transform (fn [_ v] v)}}]
                   (rf/dispatch
                    [::set-form-field data-path (:id field) transform default a])))]
    [:div
     (when debug?
       [:p.has-text-centered (str data)])
     (->> (:fields form-config)
          (map-indexed
           (fn [i f]
             [:div.field.is-horizontal {:key i}
              [:div.field-label.is-normal
               [:label.label (:label f)]]
              [:div.field-body
               [:div.field
                (render-field (answer f) f (get data (:id f)))
                (when-let [d (:description f)]
                  [:p.help d])]]])))]))


(defmethod render-field ::tags
  [answer f v]
  (let [v (or v #{})]
    [:div.control
     [c/tags-select
      :select #(answer % :transform conj :default #{})
      :de-select #(answer % :transform disj :default #{})
      :selected-tag-ids (or v #{})]]))


(defmethod render-field ::text
  [answer f v]
  [:div.control
   [:input.input {:type "text" :value (or v "")
                  :on-change #(answer (.. % -target -value) :default "")}]
   ])
