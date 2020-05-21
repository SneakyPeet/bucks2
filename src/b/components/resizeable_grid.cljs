(ns b.components.resizeable-grid
  (:require [goog.object]
            [reagent.core :as r]))

(defn- react-grid-layout []
  (r/adapt-react-class js/ReactGridLayout))


(defn- responsive-react-grid-layout []
  (r/adapt-react-class (goog.object/get js/ReactGridLayout "Responsive")))


(defn component []
  (let [grid (react-grid-layout)]
     [grid {:class "layout"
            :layout [{:i "a" :x 0 :y 0 :w 1 :h 2 :static true}
                     {:i "b" :x 1 :y 0 :w 3 :h 2 :minW 2 :maxW 4}
                     {:i "c" :x 4 :y 0 :w 1 :h 2}]
            :cols 12
            :row-height 30
            :width 1200}
      [:div.box {:key "a"} "a"]
      [:div.box {:key "b"} "b"]
      [:div.box {:key "c"} "c"]]))
