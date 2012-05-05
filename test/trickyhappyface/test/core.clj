(ns trickyhappyface.test.core
  (:use [trickyhappyface.core])
  (:use [trickyhappyface.constants])
  (:use [trickyhappyface.util :only (md5-sum-bytes)])
  (:use [clojure.test])
  (:use midje.sweet) 
  
  )

(defn -verify-session [info]
  (and (= (info :stat) OK) 
       (not= (session-id info) nil)
       (= (count (session-id info)) 32)))

;you need a config.clj to define email password apikey to test
(load-file "config.clj") 
(defn login [] (login-with-password apikey email password))

(against-background 
  [(around :contents (let [loginInfo (login)
                           userid (-> loginInfo :Login :User :id)
                           phash (-> loginInfo :Login :PasswordHash)
                           sid (session-id loginInfo)] ?form ))]

  (fact (-verify-session loginInfo))
  (fact (-verify-session (login-anonymously apikey)))

  (fact (-verify-session (login-with-hash apikey userid phash)))

) ;end against-background

