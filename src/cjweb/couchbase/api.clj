(ns cjweb.couchbase.api (:require [compojure.core :refer [routes POST GET DELETE ANY]]
                              [cheshire.core :refer  [parse-string generate-string]]
                              [cjweb.mongo.service :as couchbase-service]
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
(defn couchbase-save-update
  "Create or Update - returns the created or updated record as json"
  [db col req]
  (generate-string (couchbase-service/save-update-doc db col (parse-string (get-json req)))))

(defn couchbase-search
  "Read / Search Many - returns a list of records as json"
  [db col req]
  (generate-string (couchbase-service/find-docs db col (get-query-parameters req))))

(defn couchbase-get-by-id
  "Read One - returns one json record"
  [db col id]
  (generate-string (couchbase-service/get-doc-by-id db col id)))

(defn couchbase-update-by-id
  "Update - returns the updated record as json"
  [db col id req]
  (generate-string (couchbase-service/update-doc db col id (parse-string (get-json req)))))

(defn couchbase-delete-by-id [db col id]
  "Delete One - returns the id of the deleted record as json"
  (generate-string (couchbase-service/delete-doc-by-id db col id)))

(defonce ^:const json-content  {"Content-Type" "application/json"})
(defonce ^:const html-content  {"Content-Type" "text/html"})

(defn couchbase-routes [] "CRUD Routes for the Couch db database.
 db refers to the name of the Couch db database,
 col refers to the collection name,
 id refers the document in question"
  (routes
    (ANY "/couchbase" []
      {:status 200 :headers html-content
       :body  "<H1>Welcome to the Couch DB API</H1>"})
    (POST "/couchbase/:db/:col" [db col :as req]
      {:status 200 :headers json-content
       :body (couchbase-save-update db col req)})
    (POST "/couchbase/:db/:col/:id" [db col id :as req]
      {:status 200 :headers json-content
       :body (couchbase-update-by-id db col id req)})
    (GET "/couchbase/:db/:col" [db col :as req]
      {:status 200 :headers json-content
       :body (couchbase-search db col req)})
    (GET "/couchbase/:db/:col/:id" [db col id]
      {:status 200 :headers json-content
       :body (couchbase-get-by-id db col id) })
    (DELETE "/couchbase/:db/:col/:id" [db col id]
      {:status 200 :headers json-content
       :body (couchbase-delete-by-id db col id)})
    ))
