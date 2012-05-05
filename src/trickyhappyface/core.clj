(ns trickyhappyface.core
  (:use [trickyhappyface.constants :only (SECURE_API_END_POINT)])
  (:use [trickyhappyface.util :only (read-url dasherize)])
  (:use [trickyhappyface.constants :only (API_END_POINT)])
  (:use [clojure.string :only (split)])
  (:require [trickyhappyface.smugmugmethods :as m])
  (:require [trickyhappyface.upload :as thfu])
  )

(defn upload [sid albumId filename]
  (thfu/upload sid albumId filename))

(defn -login-secure [params]
  (let [data (read-url SECURE_API_END_POINT params)
        sid (if data (-> data :body)) ]
    ;(println data)
    sid))

(defn session-id [data] (-> data :Login :Session :id))

(defn login-with-password [apikey email password]
  (-login-secure {:APIKey apikey
                  :EmailAddress email
                  :Password   password
                  :method "smugmug.login.withPassword"}))

(defn login-anonymously [apikey]
  (-login-secure {:APIKey apikey
                  :method "smugmug.login.anonymously"}))

(defn login-with-hash [apikey userid phash]
  (-login-secure {:APIKey apikey
                  :UserID userid
                  :PasswordHash phash
                  :method "smugmug.login.withHash"}))

(defn -smugmug
  "Method nor sid can be nil"
  ([method sid] (-smugmug method sid {}))
  ([method sid inparams]
   {:pre [(and (not (nil? method)) (not (nil? sid)))]}
   (let [method (str "smugmug." method)
         params (merge {:method method :SessionID sid} inparams)
         data (read-url API_END_POINT params)
         out (if data (-> data :body)) ]
     out)))

(defn -create-smugmug-method-name [smugmug-name] 
  (symbol (dasherize (clojure.string/replace smugmug-name "." "-"))))

(defn -create-smugmug-method [smugmug-name]
  (fn ([sid] (-smugmug smugmug-name sid))
    ([sid inparams]
     (-smugmug smugmug-name sid inparams))))

; smugmug.login.withPassword is defined above
; since it requires a https connection
(def -smugmug-functions (split m/METHODS #"\s+"))

;; Meta-program all the functions
(doall
  (map #(intern *ns* 
                (-create-smugmug-method-name %1)
                (-create-smugmug-method %1)) -smugmug-functions))

