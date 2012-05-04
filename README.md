# trickyhappyface

Clojure API for smugmug.com [1.2.2] (https://wiki.smugmug.net/display/API/API+1.2.2) JSON interface.

## Usage

Here's how you use Tricky Happy Face with [Leiningen] (https://github.com/technomancy/leiningen)

Install `lein` as described and then:

    $ lein new grin
    $ cd grin

Add Tricky Happy Face to `project.clj`

    (defproject grin "1.0.0-SNAPSHOT"
      :description "FIXME: write"
      :dependencies [[org.clojure/clojure "1.3.0"]
                    [trickyhappyface "0.0.1"]])

Edit src/trickyhappyface/core.clj:

    (ns grin.core (:require [trickyhappyface.core :as thf]))

    (defn -main [& args]
      (let [ email "<your-smugmug-email>"
             apikey "<your-smugmug-api-key-here>" ;you can get this from the smugmug control panel once logged in
             password "<your-smugmug-password-here>"
             sid (thf/login-with-password email password apikey)
             album-resp (thf/albums-get sid)
             albums (album-resp :Albums)
             titles (map #(get %1 :Title) albums)]
        (println "sid" sid "method" (album-resp :method) "first album" (first albums))
        (println titles)))

Now run it:

    $ lein run -m grin.core

Tricky Happy Face translates the [smugmug API] (https://wiki.smugmug.net/display/API/API+1.2.2) into a more clojure friendly naming scheme.
It removes camelCase/periods and replaces them with dashes.

The call smugmug.images.get is:

    (images-get sid {:AlbumID "an-album-id" :AlbumKey})

The call smugmug.subcategories.getAll is:

    (smugmug-subcategories-get-all sid) 

The input of all calls is a the sid returned from login-with-password and possibly a clojure map if needed.

The only exception to this are login-with-password and upload:
    
    (login-with-password email password apikey) ;returns sid
    (upload sid album-id filename) ;returns length of file uploaded

The return of all calls is just a clojure map directly from the JSON that smugmug returns.

## License

Copyright (C) 2012 halfaleague

Distributed under the MIT License.
