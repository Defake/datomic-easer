(ns datomic-easer.core
  (:require [datomic.api :as d]))

(defn has-param? [args-list param-keyword]
  "Finds if there's a needed keyword param in the args-list"
  (if (= 0 (count args-list))
    false
    (loop [param (first args-list)
           args (drop 1 args-list)]
      (if (= param param-keyword)
        true
        (if (< 0 (count args))
          (recur (first args) (drop 1 args))
          false)))))

(defn get-attribute-name [entity-ns attr-name]
  (keyword (clojure.string/replace (str entity-ns "/" attr-name) ":" "")))

(defn define-entity [entity-ns [doc entity-id entity-type & attributes]]
  (let [entity-code `{:db/doc ~doc
                      :db/ident ~(get-attribute-name entity-ns entity-id)
                      :db/valueType ~(get-attribute-name :db.type entity-type)
                      :db/cardinality ~(get-attribute-name :db.cardinality (if (has-param? attributes :many) :many :one))
                      :db/id (d/tempid :db.part/db)
                      :db.install/_attribute :db.part/db}]
    (reduce #(if (has-param? attributes (first %2))
               (assoc %1 (second %2) (last %2))
               %1)
            entity-code
            [[:unique-value :db/unique :db.unique/value]
             [:unique-identity :db/unique :db.unique/identity]
             [:index :db/index true]
             [:component :db/isComponent true]])))

(defmacro defschema [schema-name & entities]
  `(def ~schema-name
     ~(vec (for [entity entities
                 :let [entity-ns (first entity)
                       properties (drop 1 entity)]
                 property properties]
             (define-entity entity-ns property)))))

(defmacro defdbfunc [record-func [func-name requires params func] db-url]
  `(defn ~record-func [db-url#]
     (let [f# (d/function {:lang "clojure"}
                          :requires ~requires
                          :params ~params
                          :code (str ~func))]
       @(d/transact (d/connect db-url#)
                    [{ :db/id (d/tempid :db.part/user)
                       :db/ident (keyword ~func-name)
                       :db/fn f#}]))))

(defmacro define-db [db-url schema & funcs]
  `(do
    (d/delete-database ~db-url)
    (d/create-database ~db-url)
    @(d/transact (d/connect ~db-url) ~schema)
    (doseq [f# [~@funcs]]
      (f# ~db-url))))







