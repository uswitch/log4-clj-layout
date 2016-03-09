
(set-env! :dependencies
          '[[clj-time "0.11.0" :scope "provided"]
            [cheshire "5.5.0" :scope "provided"]
            [org.slf4j/slf4j-api "1.7.18"]
            [log4j/log4j "1.2.17"
             :scope "provided"
             :exclusions [javax.mail/mail javax.jms/jms
                          com.sun.jmdk/jmxtools com.sun.jmx/jmxri]]]
          :source-paths #{"src"})

(require '[boot.core :as boot])

(task-options!
 pom {:project 'com.uswitch/log4-clj-layout
      :version "0.1.0-SNAPSHOT"})

(boot/deftask build []
  (comp (aot :namespace '#{log4-clj-layout.layout}) (pom) (jar)))
