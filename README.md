# trickyhappyface

Clojure API for smugmug.com [1.2.2] (https://wiki.smugmug.net/display/API/API+1.2.2) JSON interface.

## Example Usage

Here's how you use Tricky Happy Face with [Leiningen] (https://github.com/technomancy/leiningen)

Install `lein` as described and then:

    $ lein new grin
    $ cd grin

Add Tricky Happy Face to `project.clj`

    (defproject grin "1.0.0-SNAPSHOT"
      :description "FIXME: write"
      :dependencies [[org.clojure/clojure "1.3.0"]
                    [trickyhappyface "0.0.3"]])

Edit src/trickyhappyface/core.clj:

    (ns grin.core (:require [trickyhappyface.core :as thf]))

    (defn -main [& args]
      (let [ email "<your-smugmug-email>"
             apikey "<your-smugmug-api-key-here>" ;you can get this from the smugmug control panel once logged in
             password "<your-smugmug-password-here>"
             sid (thf/session-id (thf/login-with-password email password apikey)) ; session-id is a helper function to extract the sid
             album-resp (thf/albums-get sid)
             albums (album-resp :Albums)
             titles (map #(get %1 :Title) albums)]
        (println "sid" sid "method" (album-resp :method) "first album" (first albums))
        (println titles)))

Now run it:

    $ lein run -m grin.core

## API Notes

Tricky Happy Face translates the [smugmug API] (https://wiki.smugmug.net/display/API/API+1.2.2) into a more clojure friendly naming scheme.
It removes camelCase/periods and replaces them with dashes.

The call smugmug.images.get is:

    (images-get sid {:AlbumID "an-album-id" :AlbumKey})

The call smugmug.subcategories.getAll is:

    (subcategories-get-all sid) 

The input of all calls is a the sid returned from login-with-password and possibly a clojure map if needed.

The return of all calls is just a clojure map directly from the JSON that smugmug returns.

The only exception to sid/param-map as input and output are login-with-password, login-with-hash, login-anonymously and upload:
    
    ;returns login map (containing userid/password hash needed for login-with-hash)
    (login-with-password email password apikey) 

    ;returns login map 
    (login-with-hash email password apikey) 

    ;returns login map (with only sid)
    (login-anonymously email password apikey) 

    ;returns {:length :md5}
    (upload sid album-id filename) 

    ;upload returns {:length :md5}
    (upload sid album-id filename) 
    ;or
    (upload sid album-id filename -callback) 

    ;upload provide optional callback function like:
    ;  (defn -callback [b off len cnt file-length] ... )
    ;       b is bytes read is segment of bytes just read, 
    ;       off is offset of bytes read 
    ;       len is length of bytes read 
    ;       cnt is count of bytes read so far
    ;       file-length is total filelength

## TODO

* Add tests.
* Smugmug 1.3.0 (oauth)

## License

Copyright (C) 2012 halfaleague

Distributed under the MIT License.
