(ns cjweb.core-test
  (:require [clojure.test :refer :all]
            [cjweb.core :refer :all]
            [cjweb.mongo.service :as mongo_service]))

(defn mongodb_crud_test [db col doc]
  (def rec (mongo_service/save_update_doc db col doc))
  (println "Doc Before Save" doc)
  (println "Doc After Save" rec)
  (def rec  (mongo_service/save_update_doc db col (assoc rec :name "fred" :age 25)))
  (println "Doc Get By ID After Update" (mongo_service/get_doc_by_id db col (:_id rec)))
  (println "Doc List Before Delete" (mongo_service/find_docs db col {:query {:name "fred"}}))
  (println "ID of deleted doc" (mongo_service/delete_doc_by_id db col (:_id  rec)))
  (println "Doc List After Delete" (mongo_service/find_docs db col)))

(deftest mongo_service_test
  (mongodb_crud_test "mycooldb" "info" {:name "alex" :age 45}))
