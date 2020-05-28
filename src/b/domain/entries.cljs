(ns b.domain.entries
  (:require [clojure.string :as string]))


(def ^:private account-group
  {:name "Account Entries"
   :description "Entries that affect account balance."
   :pos 1})
(def ^:private account-non-balance-group
  {:name "Non balance affecting entries"
   :description "These entries do not affect account balance but when captured can offer insight into performance."
   :pos 2})
(def ^:private independency-group
  {:name "Financial independen"
   :description "These entries are used in calculating financial independence metrics. They do not affect account balance."
   :pos 3})


(def groups (->> [account-group account-non-balance-group independency-group]
                 (map (juxt :name identity))
                 (into {})))


(def entry-config
  (->> [[:deposit "Money you put into the account"]
        [:withdrawal "Money you withdraw from the account"]
        [:interest "Interest (positive or negative)"]
        [:dividends-reinvested "Dividends received and re-invested in the account"]
        [:dividends-payed-out "Dividends payed out and not re-invested in the account"
         {:affects-balance? false
          :group (:name account-non-balance-group)}]
        [:fees "Account fees deducted from the account balance"]
        [:fees-not-affecting-balance "Account fees deducted elsewhere"
         {:affects-balance? false
          :group (:name account-non-balance-group)}]
        [:income-before-tax "Gross monthly income before tax"
         {:affects-balance? false
          :group (:name independency-group)}]
        [:income-after-tax "Take home income after tax"
         {:affects-balance? false
          :group (:name independency-group)}]
        [:expenses "Monthly Expenses (Excluding investment deposits)"
         {:affects-balance? false
          :group (:name independency-group)}]]
       (map-indexed
        (fn [i [k description {:keys [affects-balance? group]
                             :or {affects-balance? true
                                  group (:name account-group)}}]]
          [k {:pos i
              :entry-type k
              :name (-> (name k)
                        (string/replace #"-" " ")
                        (string/capitalize))
              :import-key (-> (name k)
                              (string/upper-case))
              :description description
              :affects-balance? affects-balance?
              :group group}]))
       (into {})))
