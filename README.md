# cjweb

A Clojure application designed to get you quickly ramped up with a running Web Server that exposes a dynamic CRUD rest api to your mongo database.

This is a great tool for prototyping backend json data for front end web applications.

This application does not contain any security and runs on http://localhost:8080 it also uses the default mongo
connections properties  "127.0.0.1" port 27017
 
## Dependencies 
  1. [Java JRE 8][java] 
  2. [Mongo DB][mongo]

## Usage
 ````
 lein repl
 user=>
 ````

## License

Copyright © 2019

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

[java]:http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
[mongo]:https://www.mongodb.com/download-center#community
[appurl]:http://localhost:8080/mongo