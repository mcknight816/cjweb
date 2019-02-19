# cjweb

A Clojure application designed to get you quickly ramped up with a running Web Server that exposes a dynamic CRUD rest api to your mongo database.

This is a great tool for prototyping backend json data for front end web applications.

This application does not contain any security and runs on http://localhost:8080 it also uses the default mongo
connection properties  "127.0.0.1" port 27017
 
## Dependencies 
  1. [Java JRE 8][java] 
  2. [Mongo DB][mongo]
  3. [Clojure][clojure]
  3. [Leiningen][leiningen]

## Usage
 from the command line
 ````
 lein repl
 user=> (load "cjweb/core")
 user=> (ns cjweb.core)
 cjweb.core=> (create-server)
 ````
 
 ## Example
 The below curl will create a database called library and a collection called book.
 
 It will also store a book record represented by the json in the book collection of your mongo db.
 
 from the command line
 ```
 curl --header "Content-Type: application/json" \
   --request POST \
   --data '{"title":"Learning clojure","isbn":"342-323-4","author":"Alex Mcknight"}' \
   http://localhost:8080/mongo/library/book
```
 
You can now browse to [your application][appurl]

## Testing
 from the command line
 ````
 lein test 
  ````
## License

Copyright © 2019

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

[java]:http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
[mongo]:https://www.mongodb.com/download-center#community
[appurl]:http://localhost:8080/mongo
[leiningen]:https://leiningen.org/
[clojure]:https://clojure.org/guides/getting_started