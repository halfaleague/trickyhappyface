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

(defn upload-java [sid albumId filename]
(let [
    bytes (read-bytes filename)
    len (count bytes)
    md5 (md5-sum-bytes bytes)
    url (new java.net.URL API_RAW_UPLOAD_URL)
    conn (.openConnection url)
    ]
    (.setDoOutput conn true)
    (.setRequestProperty conn "Content-Length" (str len))
    (.setRequestProperty conn "Content-MD5" md5)
    (.setRequestProperty conn "Content-Type" "none")
    (.setRequestProperty conn "X-Smug-AlbumID" (str albumId))
    (.setRequestProperty conn "X-Smug-ResponseType" "JSON")
    (.setRequestProperty conn "X-Smug-SessionID" sid)
    (.setRequestProperty conn "X-Smug-Version" API_VERSION)
    (.setRequestProperty conn "X-Smug-FileName" filename)
    (.write (.getOutputStream conn) bytes)
    (.connect conn)
    (.getResponseCode conn)
    len)
)

(defn formatSize [size totalSize]
  (format "%3.2f" (* 100. (/ size (float totalSize)))))

(defn -size-pct [size totalSize]
  (int (* 100. (/ size (float totalSize)))))

(defn -print-bar [pcnt]
  (if (= (mod @pcnt 5) 0) 
    (print @pcnt)
    (print "."))
  (swap! pcnt inc))

(defn make-byte-counter-stream [filename byteCount totalSize pcntIn]
  (let [cnt (atom byteCount) pcnt (atom pcntIn)]
    (proxy [java.io.FileInputStream] [filename]
      (read
        ([] 
         (swap! cnt inc)
         (println "read[]" @cnt) 
         (proxy-super read) )
        ([b] 
          (swap! cnt inc)
          (println "read[b]" @cnt) 
          (proxy-super read))
        ([b off len] 
          (swap! cnt #(+ len %1))

          ;(print (formatSize @cnt totalSize) " ")

          (if (= (-size-pct @cnt totalSize) @pcnt)
            (-print-bar pcnt))

          (if (= @pcnt 100)
             [(println) (reset! pcnt 0)]
            )
          (flush)
          (proxy-super read b off len))
        )
      )))

(defn upload [sid albumId filename]
  (let [
    md5 (md5-sum filename)
    headers {
            ; "Content-Length" (str len)
             "Content-MD5" md5
             "Content-Type" "none"
             "X-Smug-AlbumID" (str albumId)
             "X-Smug-ResponseType" "JSON"
             "X-Smug-SessionID" sid
             "X-Smug-Version" API_VERSION
             "X-Smug-FileName" filename}
      length (.length (File. filename))
      body (input-stream filename)
      ;pcnt 0
      ;body (make-byte-counter-stream filename 0 length pcnt)
      ]
    ;(println "uploading of size: " (/ length 1000000.) "Mb" filename)
    (client/post API_RAW_UPLOAD_URL {:body body :headers headers :length length})
    length))

;example
;(client/post "http://site.com/resources"
;             {:body (clojure.java.io/input-stream "/tmp/foo") :length 1000})
