(ns cjweb.couchdb.api (:require [compojure.core :refer [routes POST GET DELETE ANY]]
                              [cheshire.core :refer  [parse-string generate-string]]
                              [cjweb.mongo.service :as couch-db-service]
                              [ring.middleware.params :refer [assoc-query-params]]))

(defn get-query-parameter-map [params]
  {:page (read-string (get params "page" "1"))
   :rows (read-string (get params "rows" "100"))
   :query (if (nil? (get params "q")) {} (parse-string (get params "q")))})

(defn get-query-parameters [req]
  (get-query-parameter-map  (:params (assoc-query-params req "UTF-8"))))

(defn get-json [req]
  (slurp (:body req)))

;; Couch DB CRUD Functions
(defn couch-db-save-update
  "Create or Update - returns the created or updated record as json"
  [db col req]
  (generate-string (couch-db-service/save-update-doc db col (parse-string (get-json req)))))

(defn couch-db-search
  "Read / Search Many - returns a list of records as json"
  [db col req]
  (generate-string (couch-db-service/find-docs db col (get-query-parameters req))))

(defn couch-db-get-by-id
  "Read One - returns one json record"
  [db col id]
  (generate-string (couch-db-service/get-doc-by-id db col id)))

(defn couch-db-update-by-id
  "Update - returns the updated record as json"
  [db col id req]
  (generate-string (couch-db-service/update-doc db col id (parse-string (get-json req)))))

(defn couch-db-delete-by-id [db col id]
  "Delete One - returns the id of the deleted record as json"
  (generate-string (couch-db-service/delete-doc-by-id db col id)))

(defonce ^:const json-content  {"Content-Type" "application/json"})
(defonce ^:const html-content  {"Content-Type" "text/html"})

(defn couch-db-routes [] "CRUD Routes for the Mongo database.
 db refers to the name of the mongo database,
 col refers to the collection name,
 id refers the document in question"
  (routes
    (ANY "/couchdb" []
      {:status 200 :headers html-content
       :body  "<H1>Welcome to the Couch DB API</H1>"})
    (POST "/couchdb/:db/:col" [db col :as req]
      {:status 200 :headers json-content
       :body (couch-db-save-update db col req)})
    (POST "/couchdb/:db/:col/:id" [db col id :as req]
      {:status 200 :headers json-content
       :body (couch-db-update-by-id db col id req)})
    (GET "/couchdb/:db/:col" [db col :as req]
      {:status 200 :headers json-content
       :body (couch-db-search db col req)})
    (GET "/couchdb/:db/:col/:id" [db col id]
      {:status 200 :headers json-content
       :body (couch-db-get-by-id db col id) })
    (DELETE "/couchdb/:db/:col/:id" [db col id]
      {:status 200 :headers json-content
       :body (couch-db-delete-by-id db col id)})
    ))
