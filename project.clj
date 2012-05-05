(defproject trickyhappyface "0.0.3"
  :description "Clojure API for smugmug.com 1.2.2 JSON API"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.3.0"]
                ]
  :dev-dependencies [ [midje "1.3.1"] 
                      [com.stuartsierra/lazytest "1.2.3"]
                      [lein-marginalia "0.7.0"] 
                      [lein-clojars "0.6.0"]
                     ]
  :plugins [ [lein-marginalia "0.7.0"] ]
  :repositories {"stuart" "http://stuartsierra.com/maven2"}

   ;:warn-on-reflection true
;   :main gumgums.tools.main

  ; :run-aliases {:alias a.namespace/my-main
  ;                 :alias2 another.namespace}

;   :run-aliases {
;                 :create gumgums.tools.main/create
;                 :upload gumgums.tools.main/upload
;                 :delete gumgums.tools.main/delete
;                 :desc gumgums.tools.main/desc
;                 :test gumgums.tools.main/dtest
;                 }
  )
