(ns cjweb.mongo.api (:require [compojure.core :refer [routes POST GET DELETE ANY]]
                              [cheshire.core :refer  [parse-string generate-string]]
                              [cjweb.mongo.service :as mongo_service]
                              [ring.middleware.params :refer [assoc-query-params]]))

(defn get-query-parameter-map [params]
  {:page (read-string (get params "page" "1"))
   :rows (read-string (get params "rows" "100"))
   :query (if (nil? (get params "q")) {} (parse-string (get params "q")))})

(defn get-query-parameters [req]
     (get-query-parameter-map  (:params (assoc-query-params req "UTF-8"))))

(defn get-json [req]
   (slurp (:body req)))

;; Mongo CRUD Functions
(defn mongo-save-update [db col req]
  "Create or Update - returns the created or updated record as json"
  (generate-string (mongo_service/save-update-doc db col (parse-string (get-json req)))))
(defn mongo-search [db col req]
  "Read / Search Many - returns a list of records as json"
  (generate-string (mongo_service/find-docs db col (get-query-parameters req))))
(defn mongo-get-by-id [db col id]
  "Read One - returns one json record"
  (generate-string (mongo_service/get-doc-by-id db col id)))
(defn mongo-update-by-id [db col id req]
  "Update - returns the updated record as json"
  (generate-string (mongo_service/update-doc db col id (parse-string (get-json req)))))
(defn mongo-delete-by-id [db col id]
  "Delete One - returns the id of the deleted record as json"
  (generate-string (mongo_service/delete-doc-by-id db col id)))

(def ^:const json-content  {"Content-Type" "application/json"})
(def ^:const html-content  {"Content-Type" "text/html"})

(defn mongo-routes [] "CRUD Routes for the Mongo database.
 db refers to the name of the mongo database,
 col refers to the collection name,
 id refers the document in question"
  (routes
    (ANY "/mongo" []
      {:status 200 :headers html-content
       :body  "<H1>Welcome to the Mongo API</H1>"})
    (POST "/mongo/:db/:col" [db col :as req]
      {:status 200 :headers json-content
       :body (mongo-save-update db col req)})
    (POST "/mongo/:db/:col/:id" [db col id :as req]
      {:status 200 :headers json-content
       :body (mongo-update-by-id db col id req)})
    (GET "/mongo/:db/:col" [db col :as req]
      {:status 200 :headers json-content
       :body (mongo-search db col req)})
    (GET "/mongo/:db/:col/:id" [db col id]
      {:status 200 :headers json-content
       :body (mongo-get-by-id db col id) })
    (DELETE "/mongo/:db/:col/:id" [db col id]
      {:status 200 :headers json-content
       :body (mongo-delete-by-id db col id)})
    ))

