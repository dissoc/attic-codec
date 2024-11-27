(defproject attic-codec "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[buddy/buddy-core "1.10.413"]
                 [com.cognitect/transit-clj "1.0.333"]
                 [com.taoensso/nippy "3.4.2"]
                 [org.clojure/clojure "1.10.3"]]
  :test-paths ["test"]
  :profiles {:dev
             {:dependencies [[org.cloboss/core "2.2.0-SNAPSHOT"]]}
             :test          [:profiles/test]
             :profiles/test {:dependencies [[org.cloboss/core "2.2.0-SNAPSHOT"]
                                            [org.cloboss/caching "2.2.0-SNAPSHOT"]
                                            [top.atticboss/atticboss-caching "0.14.0-SNAPSHOT"]
                                            [org.cloboss/messaging "2.2.0-SNAPSHOT"]]}})
