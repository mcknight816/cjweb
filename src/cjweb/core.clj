(ns cjweb.core (:require [org.httpkit.server :as s]
                         [clojure.tools.logging :as log]
                         [cjweb.mongo.api :as mongo_api]))

(defn app "Attach API routes to our web application" []
    (mongo_api/mongo_routes))
;;todo allow for external configuration of the port
;;todo add security
(defn create-server "Start the web server" []
  (s/run-server (app) {:port 8080})
  (log/info (str "Create a server on port 8080 " "http://localhost:8080/mongo")))

(defn stop-server "Stop the web server" [server]
  (server :timeout 100))

(defn -main "Start the app" []
  (create-server ))

