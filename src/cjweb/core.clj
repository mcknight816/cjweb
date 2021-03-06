(ns cjweb.core (:require [org.httpkit.server :as s]
                         [clojure.tools.logging :as log]
                         [com.unbounce.dogstatsd.core :as statsd]
                         [com.unbounce.dogstatsd.ring :as dogstatsd.ring]
                         [cjweb.mongo.service :as mongo_service]
                         [cjweb.mongo.api :as mongo_api]
                         [cjweb.couchbase.api :as couchbase_api]
                         [cjweb.redis.service :as redis]
                         [datadog-apm-clj-wrapper.core :as apm])
  (:import
    ))

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

(defn allow-cross-origin
  "Middleware function to allow cross origin requests from browsers.
  When a browser attempts to call an API from a different domain, it makes an OPTIONS request first to see the server's
  cross origin policy.  So, in this method we return that when an OPTIONs request is made.
  Additionally, for non OPTIONS requests, we need to just returm the 'Access-Control-Allow-Origin' header or else
  the browser won't read the data properly.
  The above notes are all based on how Chrome works. "
  ([handler]
   (allow-cross-origin handler "*"))
  ([handler allowed-origins]
   (fn [request]
     (if (= (request :request-method) :options)
       (-> (handler request)
           (assoc-in [:headers "access-control-allow-origin"] allowed-origins)
           (assoc-in [:headers "access-control-allow-methods"] "GET,POST,DELETE,OPTIONS")
           (assoc-in [:headers "access-control-allow-headers"] "access-control-allow-origin,x-requested-with,content-type,cache-control,origin,accept,authorization")
           (assoc-in [:headers "Access-Control-Allow-Credentials"] true)
           (assoc :status 200))
       (-> (handler request)
           (assoc-in [:headers "access-control-allow-origin"] allowed-origins))))))


(defn app "Attach API routes to our web application" []
  (-> (do (mongo_api/mongo-routes)
          (couchbase_api/couchbase-routes))
      (dogstatsd.ring/wrap-http-metrics {:tags #{"CJWEBHTTP"} :sample-rate 0.3})
      (allow-cross-origin)
      (ignore-trailing-slash)))

(defn start-server "Start the web server" []
  (statsd/setup! :host "127.0.0.1" :port 8125 :prefix "cjweb.app")
  (reset! server (s/run-server (#'app) {:port 8081}))
  (log/info (str "Start a server on port 8080 " "http://localhost:8080/mongo")))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main "Start the app" []
  (start-server))

(comment

  (redis/put-key-value " 12345-123344-231213-434234 " ,
             {:name "Alex"
              :description "a list of properties"
              :port 1234
              :host "localhost"
              :password "someEncryptedData"})

  (redis/get-by-key " 12345-123344-231213-434234 ")

  (stop-server)
  (start-server)

  (statsd/setup! :host "localhost" :port 8125 :prefix "cjweb.app")
  (statsd/event {:title "foo" :text "things are bad\nfoo"} nil)
  (statsd/increment "foo.bar")

  (statsd/service-check  {:name "hi" :status :warning} nil)
  (statsd/service-check {:name "hi" :status :ok :timestamp 10 :check-run-id 123 :message "foo" :hostname "blah"} nil)
  (statsd/shutdown!)
  (apm/instrumentation "sum-two-numbers" #(+ 4 1)))

