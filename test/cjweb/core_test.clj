(ns cjweb.core-test
  (:require [clojure.test :refer :all]
            [cjweb.core :refer :all]
            [cjweb.mongo.service :as mongo_service]))

(defn mongodb-crud-test [db col doc]
  (def rec (mongo_service/save-update-doc db col doc))
  (println "Doc Before Save" doc)
  (println "Doc After Save" rec)
  (def rec  (mongo_service/save-update-doc db col (assoc rec :name "fred" :age 25)))
  (println "Doc Get By ID After Update" (mongo_service/get-doc-by-id db col (:_id rec)))
  (println "Doc List Before Delete" (mongo_service/find-docs db col {:query {:name "fred"}}))
  (println "ID of deleted doc" (mongo_service/delete-doc-by-id db col (:_id  rec)))
  (println "Doc List After Delete" (mongo_service/find-docs db col)))

(deftest mongo-service-test
  (mongodb-crud-test "mycooldb" "info" {:name "alex" :age 45}))
