(ns cjweb.mongo.api (:require [compojure.core :refer [routes POST GET DELETE ANY]]
                              [cheshire.core :refer :all]
                              [cjweb.mongo.service :as mongo_service]
                              [ring.middleware.params :refer [assoc-query-params]]))

(def ^:const json  {"Content-Type" "application/json"})
(def ^:const html  {"Content-Type" "text/html"})

(defn getQueryParameterMap [params]
  {:page (if (nil? (:page params)) 1 (:page params))
   :rows (if (nil? (:rows params)) 100 (:rows params))
   :query (if (nil? (:q params)) {} (parse-string (:q params)))})

(defn getQueryParameters [req]
     (getQueryParameterMap (into {} (:params (assoc-query-params req "UTF-8")))  ))

(defn getJson [req]
   (slurp (:body req)))

(defn mongo_routes [] "CRUD Routes for the Mongo database.
 db refers to the name of the mongo database and col refers to the collection name"
  (routes
    (GET "/mongo" []
      {:status 200 :headers html
       :body  "<H1>Welcome to the Mongo API</H1>"})
    (POST "/mongo/:db/:col" [db col :as req]
      {:status 200 :headers json
       :body (generate-string (mongo_service/save_update_doc db col (parse-string (getJson req)) ))})
    (POST "/mongo/:db/:col/:id" [db col id :as req]
      {:status 200 :headers json
       :body (generate-string (mongo_service/update_doc db col id req))})
    (GET "/mongo/:db/:col" [db col :as req]
      {:status 200 :headers json
       :body (generate-string (mongo_service/find_docs db col (getQueryParameters req)))})
    (GET "/mongo/:db/:col/:id" [db col id]
      {:status 200 :headers json
       :body (generate-string (mongo_service/get_doc_by_id db col id))})
    (DELETE "/mongo/:db/:col/:id" [db col id]
      {:status 200 :headers json
       :body (generate-string (mongo_service/delete_doc_by_id db col id))})
    ))

