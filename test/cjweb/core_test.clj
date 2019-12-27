(ns cjweb.core-test
  (:require [clojure.test :refer :all]
            [cjweb.core :refer :all]
            [cjweb.mongo.service :as mongo_service]))

(def debug-mode (atom {:debug false}))
(defn set-debug-mode [] (swap! debug-mode assoc :debug true))
(defn set-default-mode [] (swap! debug-mode assoc :debug false))

(deftest mongo-test
  (println "Running Mongo Tests")
  (let [db "mycooldb"
        col "info"
        doc {:name "alex" :age 30}
        rec (mongo_service/save-update-doc db col doc)
        updated_doc (assoc rec :name "fred" :age 25)
        update (mongo_service/save-update-doc db col updated_doc)
        updated_rec (mongo_service/get-doc-by-id db col (:_id rec))
        list (mongo_service/find-docs db col {:query {:name "fred"}})
        delete-id (mongo_service/delete-doc-by-id db col (:_id  rec))
        after-delete (mongo_service/find-docs db col)]

    (testing "New record has an _id"
      (is (not (nil? (:_id rec)))))

    (testing "Updated record has the same _id as original record"
      (is (= [(:_id updated_rec) (:_id rec)])))

    (testing "Updated record has our new values"
      (is (= [(:name updated_rec) (:name updated_doc)
              (:age updated_rec) (:age updated_doc)])))

    (testing "list of records with the same name is > 0"
      (is (> [(:totalrecords list) 0])))

    (testing "list of records after delete is one less then the previous total"
      (is (= [(:totalrecords after-delete) (- (:totalrecords list) 1)])))

    (when (:debug @debug-mode)
      (println "Doc Before Save" doc)
      (println "Doc After Save" rec)
      (println "Doc After Update" update)
      (println "Doc Get By ID After Update" updated_rec)
      (println "Doc List Before Delete where name is fred" list)
      (println "ID of deleted doc" delete-id)
      (println "Doc List After Delete" after-delete))))

(comment
  (set-default-mode)
  (set-debug-mode)
  (run-tests))
