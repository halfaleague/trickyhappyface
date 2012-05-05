(ns trickyhappyface.test.core
  (:use [trickyhappyface.core])
  (:use [trickyhappyface.constants])
  (:use [trickyhappyface.util :only (md5-sum-bytes)])
  (:use [clojure.test])
  (:use midje.sweet) 
  )

(defn -size-pct [size total-size]
  (int (* 100. (/ size (float total-size)))))

(defn -print-bar [pcnt]
  (if (= (mod @pcnt 5) 0) 
    (print @pcnt)
    (print "."))
  (swap! pcnt inc))

(defn -callback [b off len
                 cnt file-length]
  (let [
        rpct 0
        pct (atom rpct)
        ]

  (println "-callback" cnt @pct)
    (if (= (-size-pct cnt file-length) @pct)
      (-print-bar pct))

    (if (= @pct 100)
      [(println) (reset! pct 0)]
      )
    (flush)
    ))

(defn -verify-session [info]
  (and (= (info :stat) OK) 
       (not= (session-id info) nil)
       (= (count (session-id info)) 32)))

;you need a config.clj to define email password apikey to test
(load-file "config.clj") 
(defn login [] (login-with-password apikey email password))

(def filename "data/macke_lady.jpg")
(def album-name "When can their glory fade?
O the wild charge they made!
  All the world wonder'd.
Honour the charge they made!")

(against-background 
  [(around :contents (let [login-info (login)
                           userid (-> login-info :Login :User :id)
                           phash (-> login-info :Login :PasswordHash)
                           sid (session-id login-info)] ?form ))]

  (fact (-verify-session login-info))
  (fact (-verify-session (login-anonymously apikey)))
  (fact (-verify-session (login-with-hash apikey userid phash)))
 
  (fact (let [album (albums-create sid {:Title album-name})
               aid (-> album :Album :id)
               {length :length md5 :md5} (upload sid aid filename)
               {length :length md5 :md5} (upload sid aid filename -callback)
               dresp (albums-delete sid {:AlbumID aid}) ]
           (dresp :stat)) => OK)

) ;end against-background

