
(set-env! :dependencies
          '[[org.clojure/clojure "1.7.0" :scope "provided"]
            [clj-time "0.11.0" :scope "provided"]
            [cheshire "5.5.0" :scope "provided"]
            [org.slf4j/slf4j-api "1.7.18" :scope "provided"]
            [log4j/log4j "1.2.17"
             :scope "provided"
             :exclusions [javax.mail/mail javax.jms/jms
                          com.sun.jmdk/jmxtools com.sun.jmx/jmxri]]]
          :source-paths #{"src"})

(require '[boot.core :as boot])

(set-env! :repositories 
          [["clojars" {:url "https://clojars.org/repo/"
                       :username (System/getenv "CLOJARS_USER")
                       :password (System/getenv "CLOJARS_PASS")}]])

(task-options!
 pom {:project 'log4-clj-layout :version "0.1.7"}
 push {:repo "clojars"})

(boot/deftask build []
  (comp (aot :namespace '#{log4-clj-layout.layout}) (pom) (jar)))

(boot/deftask deploy []
  (comp (build) (install) (push)))
