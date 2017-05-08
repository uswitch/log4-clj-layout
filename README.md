#`log4-clj-layout`

A flexible log4j layout for Clojure via slf4j.

# Artifacts

## `leiningen / boot`

``` clojure
[log4-clj-layout "0.1.6"]
```

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
(defproject project-name "1.2.3"
  ;; ...
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
                 [log4-clj-layout "VERSION"]])
```

## License

Copyright © 2016 uSwitch Limited.

Distributed under the Eclipse Public License, the same as Clojure.
