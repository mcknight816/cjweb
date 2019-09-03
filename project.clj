(defproject cjweb "0.1.0-SNAPSHOT"
  :description "Creates a crud rest api to your mongo database"
  :url "https://github.com/mcknight816/cjweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [eureka-client "0.2.0"]
                 [http-kit "2.3.0"]
                 [compojure "1.6.0"]
                 [cheshire "5.8.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [com.novemberain/monger "3.5.0"]
                 [com.unbounce/clojure-dogstatsd-client "0.6.0"]
                 [datadog-apm-clj-wrapper "0.1.2"]; used to test data dog apm
                 ])
