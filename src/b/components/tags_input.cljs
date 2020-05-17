(ns b.components.tags-input
  (:require [reagent.core :as r]
            [goog.object]))


(defn component [& {:keys [source selected-items select de-select can-add? placeholder]
                    :or {source []
                         selected-items []
                         select #(prn "select " %)
                         de-select #(prn "de-select " %)
                         can-add? false}}]
  (let [add (fn [i] (select (goog.object/get i "item")))
        remove (fn [i] (de-select i))
        opts {:source (if (fn? source)
                        (fn [a] (clj->js (source a)))
                        source)
              :freeInput can-add?
              :selectable false
              :placeholder (if can-add?
                             "Select and/or create tags"
                             "Select tags")
              :searchMinChars 1
              :noResultsLabel (if can-add?
                                "No tags found - Hit enter to add your own"
                                "No tags found")}]
    (r/create-class
     {:component-did-mount
      (fn [comp]
        (let [node (r/dom-node comp)
              elem (js/BulmaTagsInput. node (clj->js opts))]
          (.on elem "after.add" add)
          (.on elem "after.remove" remove)
          ))
      :reagent-render
      (fn [& {:keys [selected-items]
              :or {selected-items []}}]
        [:input {:type "text"
                 :default-value (clj->js selected-items)}])})))
