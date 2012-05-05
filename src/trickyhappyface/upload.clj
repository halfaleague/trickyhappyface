(ns trickyhappyface.upload
  (:use [trickyhappyface.constants :only (API_RAW_UPLOAD_URL API_VERSION)])
  (:use [trickyhappyface.util :only (read-bytes md5-sum-bytes md5-sum)])
  (:use [clojure.java.io :only (input-stream)])
  (:require [clj-http.client :as client])
  (:import (java.io File))
  (:import (java.security 
       NoSuchAlgorithmException
       MessageDigest)
     (java.math BigInteger))
)

(defn -read-callback [filename length callback]
  "cnt is cumsum of bytes read so far, 
   length is total length of filename"
  (let [rcnt 0 cnt (atom rcnt)]
    (proxy [java.io.FileInputStream] [filename]
      (read
        ([b off len] 
          (swap! cnt #(+ len %1))
          (callback b off len @cnt length)
          (proxy-super read b off len))))))


(defn upload 
  ([sid album-id filename callback]
   (let [md5 (md5-sum filename)
        headers {"Content-MD5" md5
                 "Content-Type" "none"
                 "X-Smug-AlbumID" (str album-id)
                 "X-Smug-ResponseType" "JSON"
                 "X-Smug-SessionID" sid
                 "X-Smug-Version" API_VERSION
                 "X-Smug-FileName" filename}
        length (.length (File. filename))
        body (input-stream filename)
        body (if (= nil callback) 
                 (input-stream filename) 
                 (-read-callback filename length callback))
      ]
    (client/post API_RAW_UPLOAD_URL {:body body :headers headers :length length})
    {:length length :md5 md5})))
  
;example
;(client/post "http://site.com/resources"
;             {:body (clojure.java.io/input-stream "/tmp/foo") :length 1000})
