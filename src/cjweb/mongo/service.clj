(ns cjweb.mongo.service
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.walk :as walk]
            [monger.query :refer [with-collection paginate find]])
  (:import (java.util UUID) ))
;;todo allow for external configuration of the mongo db server address
;;todo allow for setting mongo username and password
(def conn (atom (mg/connect)))

(defn get-doc-by-id [database collection id]
  "retrieve a document from the mongo database whose _id == id"
  (let [ db (mg/get-db @conn database)]
    (mc/find-one-as-map db collection { :_id id })))

(defn update-doc [database collection id document]
  "set the key values of a mongo document whose _id == id,
  with the incoming document key values "
  (let [ db (mg/get-db @conn database)
        existing-doc (walk/stringify-keys (get-doc-by-id database collection id))
        merged-doc (merge existing-doc document)]
  (mc/update-by-id db collection id merged-doc) merged-doc))

(defn save-update-doc [database collection document]
  "if the incoming document contains the _id we pass the document
  to the update function otherwise we save a new document to the
  mongo database and assign a generated random _id to it"
  (let [id (get document "_id")]
    (if id
      (update-doc database collection id document)
      (let [ db (mg/get-db @conn database)
            doc (merge document {:_id (str (UUID/randomUUID))})]
        (mc/insert-and-return db collection doc)))))

(defn delete-doc-by-id [database collection id]
  "remove a document from the mongo database whose _id == id"
  (let [ db (mg/get-db @conn database)]
   (mc/remove-by-id db collection id)) id)

;;todo add sort order , sort index and selected fields
(defn find-docs
  "This is a paginated query. To query all keys named tags that have the values functional or object-oriented, your query
  might look like this {:tags {$all [\"functional\" \"object-oriented\"]}} more info can be found here
  http://clojuremongodb.info/articles/querying.html"
  ([database collection] (find-docs database collection {}))
  ([database collection search-criteria]
   (let [db (mg/get-db @conn database)
         {:keys [query page rows]
          :or {page 1
               rows 100
               query {}}} search-criteria
         num-records (mc/count db collection query)]
     {:totalrecords num-records
      :currpage     page
      :totalpages   (inc (quot num-records rows))
      :rows         (with-collection db collection
                                     (find query)
                                     (paginate :page page
                                               :per-page rows))})))
