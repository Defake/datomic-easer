# Datomic-easer

Datomic-easer allows to create datomic schema more easy for improving readability and speeding up creating schema.

Below there're two equivalent pieces of code:

```clj
;; Standart Datomic usage
(def schema
  [ { :db/unique             :db.unique/value, 
      :db/valueType          :db.type/string, 
      :db.install/_attribute :db.part/db, 
      :db/cardinality        :db.cardinality/one, 
      :db/doc                "User login", 
      :db/id                 (d/tempid :db.part/db), 
      :db/ident              :user/login} 

    { :db/unique             :db.unique/value, 
      :db/valueType          :db.type/string, 
      :db.install/_attribute :db.part/db, 
      :db/cardinality        :db.cardinality/one, 
      :db/doc                "User E-mail", 
      :db/id                 (d/tempid :db.part/db), 
      :db/ident              :user/email} 

    { :db/valueType          :db.type/string, 
      :db.install/_attribute :db.part/db, 
      :db/cardinality        :db.cardinality/one, 
      :db/doc                "User password", 
      :db/id                 (d/tempid :db.part/db), 
      :db/ident              :user/password} 

    { :db/valueType          :db.type/bigdec, 
      :db.install/_attribute :db.part/db, 
      :db/cardinality        :db.cardinality/one, 
      :db/doc                "Account balance", 
      :db/id                 (d/tempid :db.part/db), 
      :db/ident              :user/balance} 

    { :db/valueType          :db.type/ref, 
      :db.install/_attribute :db.part/db, 
      :db/cardinality        :db.cardinality/many, 
      :db/doc                "User food cart", 
      :db/id                 (d/tempid :db.part/db), 
      :db/ident              :user/food} 

    { :db/valueType          :db.type/string, 
      :db.install/_attribute :db.part/db, 
      :db/cardinality        :db.cardinality/one, 
      :db/doc                "Food name", 
      :db/id                 (d/tempid :db.part/db), 
      :db/ident              :food/name} 

    { :db/valueType          :db.type/long, 
      :db.install/_attribute :db.part/db, 
      :db/cardinality        :db.cardinality/one, 
      :db/doc                "Food price", 
      :db/id                 (d/tempid :db.part/db), 
      :db/ident              :food/price}])
      
(def db-url "datomic:free://localhost:4335/db1")
(d/delete-database db-url)
(d/create-database db-url)
@(d/transact (d/connect db-url) schema)
```

```clj
;; Datomic-easer usage
(defschema schema
  [:user
   ["User login"      :login     :string :unique-value]
   ["User E-mail"     :email     :string :unique-value]
   ["User password"   :password  :string]
   ["Account balance" :balance   :bigdec]
   ["User food cart"  :food      :ref :many]]

  [:food
   ["Food name"  :name :string]
   ["Food price" :price :long]])
   
(define-db "datomic:free://localhost:4335/db1" schema)
```

#### Note that you will delete existing db by using define-db!

## Importing

Lein dependency
```clj
[defake/datomic-easer "0.1.0"]
```

Requiring
```clj
(ns your-project.db.create
  (:require [datomic-easer.core :refer :all]))
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
