#`log4-clj-layout`

A log4j layout for Clojure.

# Artifacts
Leiningen/boot

``` clojure
[com.uswitch/log4-clj-layout "VERSION"]
```

# Usage

Put a `log4j.properties` on your classpath (`resources/log4j.properties`)
```ini
log4j.rootCategory=INFO,STDOUT
log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=log4_clj_layout.layout.Layout
log4j.appender.STDOUT.layout.UserFields=field1:val1,field2:val2
log4j.appender.STDOUT.layout.FormatFn=pprint-format
```

An example of Logging Hell™ dependencies:
``` clojure
:dependencies [;; SLF4J is a common interface to any logger implementation.
               [org.slf4j/slf4j-api "SLF4J-VERSION"] ; The interface
               ;; Redirect logs into SLF4J from various places
               [org.slf4j/jul-to-slf4j "SLF4J-VERSION"]
               [org.slf4j/jcl-over-slf4j "SLF4J-VERSION"]
               ;; Use Log4J as the implementation for the SLF4J interface.
               [org.slf4j/slf4j-log4j12 "SLF4J-VERSION"]
               ;; Actually include Log4J
               ;; Log4J is configured separatly via resources/log4j.properties
               [log4j/log4j "LOG4J-VERSION"
                :exclusions [javax.mail/mail javax.jms/jms
                             com.sun.jmdk/jmxtools com.sun.jmx/jmxri]]
               ;; log-config for easy(er) clojure.tools.logging & slf4j config
               [com.palletops/log-config "LOG-CONFIG-VERSION"
                :exclusions [org.clojure/tools.logging]]
               ;; Include this layout
               [com.uswitch/log4-clj-layout "VERSION"]]
```
