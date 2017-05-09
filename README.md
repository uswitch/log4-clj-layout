#`log4-clj-layout`

A flexible log4j layout for Clojure via slf4j.

# Artifacts

## `leiningen / boot`

[![Clojars Project](https://img.shields.io/clojars/v/log4-clj-layout.svg)](https://clojars.org/log4-clj-layout)


# Usage

Put a `log4j.properties` on your classpath (`resources/log4j.properties`), add
any custom key-vals to the output, and set the log formatting function
(defaults to `#'log4-clj-layout.layout/pprint-format`).
```ini
log4j.rootCategory=INFO,STDOUT
log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=log4_clj_layout.layout.Layout
log4j.appender.STDOUT.layout.UserFields=field1:val1,field2:val2
log4j.appender.STDOUT.layout.FormatFn=pprint-format
```

Or you can write and use your own formatting function.
```ini
log4j.rootCategory=INFO,STDOUT
log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=log4_clj_layout.layout.Layout
log4j.appender.STDOUT.layout.UserFields=field1:val1,field2:val2
log4j.appender.STDOUT.layout.FormatFn=your-namespace.core/fancy-format
```

## Root Key Prefixes

Some keys emitted by default such as `:file`, `:method`, `:class`,
etc., may clash with keys you wish to emit from software. A prefix to
the default root keys can be set so as not to conflict with
application keys.

``` ini
log4j.appender.STDOUT.layout.RootKeyPrefix=java
```

## Env

You can set `$APPLICATION` and/or `$TEAM` in your environment. This
will override any properties set in log4j.properties.

## Dependencies

An example of Logging Hell™ dependencies:
``` clojure
(def slf4j-version "1.7.25")

(defproject testing "0.1.0-SNAPSHOT"
  :main testing.core
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]

                 [cheshire "5.5.0"]
                 [clj-time "0.11.0"]

                 ;; SLF4J is a common interface to any logger implementation.
                 [org.slf4j/slf4j-api ~slf4j-version] ; The interface
                 ;; Redirect logs into SLF4J from various places
                 [org.slf4j/jul-to-slf4j ~slf4j-version]
                 [org.slf4j/jcl-over-slf4j ~slf4j-version]
                 ;; Use Log4J as the implementation for the SLF4J interface.
                 [org.slf4j/slf4j-log4j12 ~slf4j-version]
                 ;; Actually include Log4J
                 ;; Log4J is configured separatly via resources/log4j.properties
                 [log4j/log4j "1.2.17"
                  :exclusions [javax.mail/mail javax.jms/jms
                               com.sun.jmdk/jmxtools com.sun.jmx/jmxri]]
                 [log4-clj-layout "0.2.0-SNAPSHOT"]])
```

## License

Copyright © 2016 uSwitch Limited.

Distributed under the Eclipse Public License, the same as Clojure.
