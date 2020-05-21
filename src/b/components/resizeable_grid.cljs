(ns b.components.resizeable-grid
  (:require [goog.object]
            [reagent.core :as r]
            [b.utils :as u]))

(defn- width-provider []
  (goog.object/get js/ReactGridLayout "WidthProvider"))


(defn- react-grid-layout []
  (r/adapt-react-class ((width-provider) js/ReactGridLayout)))


(defn- responsive-react-grid-layout []
  (r/adapt-react-class ((width-provider) (goog.object/get js/ReactGridLayout "Responsive"))))


(defn- layout->clj [l]
  (->> l
       js->clj
       (map (fn [i]
              (->> i
                   (map (fn [[k v]]
                          [(keyword k) v]))
                   (into {}))))
       (u/index-by :i)))


(defn- find-next-row
  [layout-map]
  (if (empty? layout-map)
    0
    (->> layout-map
         vals
         (map :y)
         (remove nil?)
         (apply max)
         inc)))


(defn new-component-layout-config
  [layout-map {:keys [id w h]
               :or {w 4 h 2}}]
  {:i id
   :x 0
   :y (find-next-row layout-map)
   :w w
   :h h})


(defn component [{:keys [layout-map changed]
                  :or {layout-map {}
                       changed prn}}
                 & children]
  (let [grid (react-grid-layout)]
     [grid {:class "layout"
            :layout (vals layout-map)
            :cols 12
            :row-height 30
            :onLayoutChange #(changed (layout->clj %))}
      children]))


(def ^:private example-item-layout {:y 0,
                                    :maxH nil,
                                    :moved false,
                                    :minW nil,
                                    :w 1,
                                    :static true,
                                    :isDraggable nil,
                                    :isResizable nil,
                                    :h 2,
                                    :minH nil,
                                    :x 0,
                                    :maxW nil,
                                    :i "a"})
