(ns cjweb.redis.service
  (:require [taoensso.carmine :as car]
            [clojure.walk :as walk])

  (:import (java.util UUID) ))
;;todo allow for external configuration of the redis server
(defn conn [] {:pool {} :spec {:host   "master.rn-dev-tls.tr1knz.use2.cache.amazonaws.com"
                                 :port   6379
                                 :db     1
                                 :ssl-fn :default}})
(defn put-key-value [k,v]
  (car/wcar (conn) (car/set k v)) v)

(defn get-by-key [k]
  (car/wcar (conn) (car/get k)))

(defn delete-by-key [k]
  (car/wcar (conn) (car/del k)))
