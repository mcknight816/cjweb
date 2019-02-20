(ns cjweb.core (:require [org.httpkit.server :as s]
                         [clojure.tools.logging :as log]
                         [cjweb.mongo.api :as mongo_api]))
;;todo allow for external configuration of the port
;;todo add security
(defonce server (atom nil))

(defn ignore-trailing-slash
  "Modifies the request uri before calling the handler.
  Removes a single trailing slash from the end of the uri if present."
  [handler]
  (fn [request]
    (let [uri (:uri request)]
      (handler (assoc request :uri (if (and (not (= "/" uri))
                                            (.endsWith uri "/"))
                                     (subs uri 0 (dec (count uri)))
                                     uri))))))

(defn app "Attach API routes to our web application" []
    (ignore-trailing-slash  (mongo_api/mongo_routes )))

(defn create-server "Start the web server" []
  (reset! server (s/run-server (#'app) {:port 8080}))
  (log/info (str "Create a server on port 8080 " "http://localhost:8080/mongo")))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main "Start the app" []
  (create-server ))

(comment (create-server))
(comment (stop-server))

