(ns cjweb.mongo.service
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.query :refer :all])
  (:import (java.util UUID) ))
;;todo allow for external configuration of the mongo db server address
;;todo allow for setting mongo username and password
(def conn (atom (mg/connect)))

(defn update_doc [database collection id document]
  "set the key values of a mongo document whose _id == id,
  with the incoming document key values "
  (let [ db (mg/get-db @conn database)]
  (mc/update-by-id db collection id document) document))

(defn save_update_doc [database collection document]
  "if the incoming document contains the _id we pass the document
  to the update function otherwise we save a new document to the
  mongo database and assign a generated random _id to it"

  (get document "_id")

  (if (contains? document "_id")
    (update_doc database collection (get document "_id") document)
    (let [ db (mg/get-db @conn database)]
      (mc/insert-and-return db collection (merge document {:_id (str (UUID/randomUUID))})))))

(defn get_doc_by_id [database collection id]
  "retrieve a document from the mongo database whose _id == id"
  (let [ db (mg/get-db @conn database)]
    (mc/find-one-as-map db collection { :_id id })))

(defn delete_doc_by_id [database collection id]
  "remove a document from the mongo database whose _id == id"
  (let [ db (mg/get-db @conn database)]
   (mc/remove-by-id db collection id)) id)

(defn defaultSearchCriteria [search_criteria]
  "If a parameter of the search criteria does not exist then set it to a default value"
  { :page (if (nil? (:page search_criteria)) 1 (:page search_criteria))
    :rows (if (nil? (:rows search_criteria)) 100 (:rows search_criteria))
    :query (if (nil? (:query search_criteria)) {} (:query search_criteria))})
;;todo add sort order , sort index and selected fields
(defn find_docs
  "This is a paginated query. To query all keys named tags that have the values functional or object-oriented, your query
  might look like this {:tags {$all [\"functional\" \"object-oriented\"]}} more info can be found here
  http://clojuremongodb.info/articles/querying.html"
  ([database collection] (find_docs database collection {}))
  ([database collection search_criteria]
   (let [db (mg/get-db @conn database)]
     {:totalrecords (mc/count db collection (:query (defaultSearchCriteria search_criteria)))
      :currpage     (:page (defaultSearchCriteria search_criteria))
      :totalpages   (+ 1 (long (/ (mc/count db collection (:query (defaultSearchCriteria search_criteria))) (:rows (defaultSearchCriteria search_criteria)))))
      :rows         (with-collection db collection (find (:query (defaultSearchCriteria search_criteria)))
                           (paginate :page (:page (defaultSearchCriteria search_criteria))
                                     :per-page  (:rows (defaultSearchCriteria search_criteria))))
        }
     )))
