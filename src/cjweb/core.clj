(ns cjweb.core (:require [org.httpkit.server :as s]
                         [clojure.tools.logging :as log]
                         [cjweb.mongo.api :as mongo_api]))
;;todo allow for external configuration of the port possibly this library https://github.com/clojure/tools.cli
;;todo add security
(defonce server (atom nil))


(defn remove-slash
  "Remove trailing slash from string"
  [uri] (if (and (not (= "/" uri))
        (.endsWith uri "/"))
          (subs uri 0 (dec (count uri)))
                           uri))

(defn ignore-trailing-slash
  "Modifies the request uri before calling the handler.
  Removes a single trailing slash from the end of the uri if present."
  [handler]
  (fn [request]
    (let [uri (:uri request)]
      (handler (update request :uri remove-slash)))))

(defn app "Attach API routes to our web application" []
    (ignore-trailing-slash  (mongo_api/mongo-routes )))

(defn start-server "Start the web server" []
  (reset! server (s/run-server (#'app) {:port 8080}))
  (log/info (str "Start a server on port 8080 " "http://localhost:8080/mongo")))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main "Start the app" []
  (start-server ))

(comment (start-server))
(comment (stop-server))

