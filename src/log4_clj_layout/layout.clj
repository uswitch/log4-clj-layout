(ns log4-clj-layout.layout
  (:require
   [cheshire.core :as json]
   [clj-time.coerce :as coerce]
   [clj-time.format :as format]
   [clojure.edn :as edn]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string])
  (:import [org.apache.log4j.spi LoggingEvent])
  (:gen-class :extends org.apache.log4j.Layout
              :name log4_clj_layout.layout.Layout
              :methods [[getUserFields [] String]
                        [setUserFields [String] void]
                        [getFormatFn [] String]
                        [setFormatFn [String] void]
                        [getRootKeyPrefix [] String]
                        [setRootKeyPrefix [String] void]]
              :exposes-methods {activateOptions -activateOptions
                                ignoresThrowable -ignoresThrowable}))

(def ns-str (str *ns*))

(defn property [name]
  (format "%s.Layout.%s" (munge ns-str) name))

(def user-fields nil)
(def USER_FIELDS_PROPERTY (property "UserFields"))

(defn user-fields-str->map [s]
  (some->> (string/split s #",")
           (map #(some-> % (string/split #":")))
           (map (juxt (comp keyword first) second))
           (into {})))

(def root-key-prefix nil)
(def ROOT_KEY_PREFIX_PROPERTY (property "RootKeyPrefix"))

(defn root-key [k]
  (if root-key-prefix
    (keyword (str root-key-prefix "_" (name k)))
    k))

(defn render-ts [ts]
  (format/unparse (format/formatters :date-time) (coerce/from-long ts)))

(defn get-user-fields []
  (let [application (System/getenv "APPLICATION")
        team (System/getenv "TEAM")]
    (cond-> (merge (some-> USER_FIELDS_PROPERTY
                           System/getProperty
                           user-fields-str->map)
                   user-fields)
      application (assoc :application application)
      team (assoc :team team))))

(def get-hostname
  (memoize (fn []
             (try
               (.getHostName (java.net.InetAddress/getLocalHost))
               (catch java.net.UnknownHostException _
                 "unknown-host")))))

(defn safe-read-edn
  [s]
  (try
    (clojure.edn/read-string s)
    (catch RuntimeException e s)))

(defn event->edn [^LoggingEvent event]
  (let [event-info (.getLocationInformation event)]
    (->> {(root-key :file)        (.getFileName event-info)
          (root-key :line-number) (.getLineNumber event-info)
          (root-key :class)       (.getClassName event-info)
          (root-key :method)      (.getMethodName event-info)
          (root-key :thread)      (.getThreadName event)
          (root-key :ndc)         (.getNDC event)
          :timestamp              (.getTimeStamp event)
          :level                  (.getLevel event)
          :message                (.getRenderedMessage event)
          :err                    (some-> event .getThrowableInformation .getThrowable)
          :raw-event              event}
         (merge (->> (.getProperties event)
                     (map (juxt (comp keyword key) (comp safe-read-edn val)))
                     (into {}))))))

(defn common-format [{:keys [err message] :as event}]
  (let [payload (->> (get-user-fields)
                     (merge event {:host (get-hostname)})
                     (filter val)
                     (into {}))]
    (cond-> payload
      err (assoc :message (if-let [msg (.getMessage err)]
                            (str message "\n" msg)
                            message)
                 :exception true
                 :exception-type (str (type err))
                 :stacktrace (string/join "\n" (map str (.getStackTrace err))))
      :always (->
               (update :timestamp render-ts)
               (update :level (comp string/lower-case str))
               (dissoc :raw-event)
               (dissoc :err)))))

(defn json-format [event]
  (-> event common-format json/generate-string (str \newline)))

(defn pprint-format [event]
  (-> event common-format pprint with-out-str))

(defn pprint-stacktrace-format [event]
  (-> event
      common-format
      pprint
      with-out-str
      (string/replace #"\\n" "\n")
      (string/replace #"\\t" "\t")))

(defn prn-format [event]
  (-> event common-format prn-str))

(def format-fn pprint-format)
(def FORMAT_FN_PROPERTY (property "FormatFn"))
(defn set-format-fn [s]
  (let [sym (some-> s edn/read-string)
        sym (if (namespace sym) sym (symbol ns-str s))]
    (println "Setting format-fn:" sym)
    (if-let [v (some-> sym resolve)]
      (alter-var-root #'format-fn (constantly v))
      (throw (java.lang.IllegalArgumentException.
              (format "Cannot find fn: %s" (pr-str sym)))))))

(defn -format [this ^LoggingEvent event]
  (format-fn (event->edn event)))

(defn -activateOptions [this])
(defn -ignoresThrowable [this] false)

(defn -getUserFields [this] (System/getProperty USER_FIELDS_PROPERTY))
(defn -setUserFields [this s]
  (let [parsed (user-fields-str->map s)]
    (println "Setting user fields:" (pr-str parsed))
    (alter-var-root #'user-fields (constantly parsed))))

(defn -getRootKeyPrefix [this] (System/getProperty ROOT_KEY_PREFIX_PROPERTY))
(defn -setRootKeyPrefix [this s]
  (println "Setting root key prefix:" s)
  (alter-var-root #'root-key-prefix (constantly s)))

(defn -getFormatFn [this] (System/getProperty FORMAT_FN_PROPERTY))
(defn -setFormatFn [this s] (set-format-fn s))
