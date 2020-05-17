(ns ^:figwheel-hooks
    b.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.events :as events]
            [b.utils :as u]
            [b.pages.registry :as pages]
            [b.pages.core :as page]
            [b.domain.core :as d]
            [akiroz.re-frame.storage :refer [reg-co-fx!]]))


(reg-co-fx! :bucks-2
            {:fx :store
             :cofx :store})


(rf/reg-event-db
 ::initialize
 (fn [_ _]
   (pages/init {})))


(rf/reg-sub
 ::db
 (fn [db _] db))


(defn app []
  [:div [page/component]])


(defn mount []
  (r/render (app) (js/document.getElementById "app")))


(defn ^:export run
  []
  (u/set-if-dev)
  (rf/dispatch-sync [::initialize])
  (rf/dispatch-sync [::d/init])
  (mount))


(defn ^:after-load re-render
  []
  (mount))


(events/listen js/window "load" #(run))
