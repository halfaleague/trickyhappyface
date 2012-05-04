(ns trickyhappyface.test.core
  (:use [trickyhappyface.core])
  (:use [clojure.test])
  (:use midje.sweet) )

;you need a config.clj to define email password apikey to test
(load-file "config.clj") 
(defn login [] (login-with-password email password apikey))

(against-background 
  [(around :contents (let [sid (login)] ?form ))]

  (fact (not= sid nil))

  (fact ((albums-get sid) :method) => "smugmug.albums.get")

) ;end against-background

