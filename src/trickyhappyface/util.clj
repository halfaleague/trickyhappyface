(ns trickyhappyface.util
  (:use [clojure.java.io :only (input-stream)])
  (:import (java.security 
       NoSuchAlgorithmException
       MessageDigest 
       DigestInputStream)
     (java.math BigInteger)
     (java.io FileInputStream)
  )
  (:import (java.io File))
  (:require [clj-http.client :as client])
  (:require [clojure.string :as s])
  (:import (java.net URLEncoder))
)

(defn md5-sum
  "Returns the md5 hash of the contents of file (where file is a File,
   a FileDescriptor or a String with the file's path"
  [^String file]
  (let [input (FileInputStream. (File. file))
        digest (MessageDigest/getInstance "MD5")
        stream (DigestInputStream. input digest)
        bufsize (* 1024 1024)
        buf (byte-array bufsize)]
    (while (not= -1 (.read stream buf 0 bufsize)))
    (apply str (map (partial format "%02x") (.digest digest)))))

(defn read-bytes [filename]
 (let [ baos (java.io.ByteArrayOutputStream.) ] 
   (with-open [is (input-stream filename)]
     (clojure.java.io/copy is baos) 
     (.toByteArray baos)))
)

(defn md5-sum-bytes 
  [#^bytes bytes]
    (let [alg (doto (MessageDigest/getInstance "MD5")
              (.reset)
              (.update bytes))]
      (try
        (format "%1$032x" (new BigInteger 1 (.digest alg)))
        (catch NoSuchAlgorithmException e
          (throw (new RuntimeException e)))))
)

;        (format "%1$032x" (new BigInteger 1 (.digest alg)))

(defn urlenc [item] (URLEncoder/encode (str item) "UTF-8"))

(defn urlwithparams
    "takes a map and makes a legal url out of it"
    [url params]
    (let [
     ks (map #(-> %1 name urlenc) (keys params))
     vs (map urlenc (vals params))
     joinIt #(s/join "=" %1)
     pairs (map joinIt (map vector ks vs))
     joinedparams (s/join "&" pairs)
     outurl (str url "?" joinedparams)
    ]
    (println outurl)
    outurl)
)

(defn read-url 
  "takes a url and get params and returns json content as a map"
  [baseurl getparams]
  (let [url (urlwithparams baseurl getparams)]
    (client/get url {:as :json})
  )
)

;FIXME make required fields required in macro?
;FIXME make inparams optional w/ multiple arity ...
(defmacro create-smugmug-method [func-name-str]
  (let [
        func-name (symbol func-name-str)
        method (s/replace func-name-str "-" ".")
        ]
  `(defn ~func-name
      ([sid#] (~func-name sid# {}))
      ([sid# inparams#]
        {:pre [(not (nil? sid#))]}
        (println "calling" ~method (map? inparams#))
        (read-url API_END_POINT 
         (merge {:method ~method :SessionID sid#} inparams#))
     )
   )
 )
)
;(println (macroexpand-1 '(create-api-method smaugmug.albums.get)))

;from https://raw.github.com/flatland/useful/develop/src/useful/string.clj
(letfn [(from-camel-fn [separator]
          (fn [string]
            (-> string
                (s/replace #"^[A-Z]+" s/lower-case)
                (s/replace #"_?([A-Z]+)"
                           (comp (partial str separator)
                                 s/lower-case second))
                (s/replace #"-|_" separator))))]

  (def dasherize (from-camel-fn "-"))
  (def underscore (from-camel-fn "_")))


