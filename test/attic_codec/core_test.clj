(ns attic-codec.core-test
  (:require [attic-codec.codecs.attic :refer :all]
            [buddy.core.codecs :as codecs]
            [buddy.core.nonce :as nonce]
            [cloboss.caching :refer [cache with-codec]]
            [cloboss.messaging :as msg]
            [clojure.test :refer :all]))

;; TODO: add common data types to be tested
;; including clj-time
(def iv8 (codecs/hex->bytes "0000017e6b6c8dcf"))

(register-attic-codec
 :codec-name :attic
 :iv8 iv8
 :key32 (nonce/random-nonce 32))

(def large-message {:yoyo    "dfsdfsd"
                    :fd      "Dfsdfds"
                    :sd      23232
                    :dfdfsdf {:DSFSD "sdfsdfsd"}})

(deftest message-attic-codec
  (let [q (msg/queue "attic-codec")
        _ (msg/publish q large-message :encoding :attic)
        recv-msg (msg/receive q)]
    (is (= large-message recv-msg))))

(deftest cache-attic-codec
  (let [ c (with-codec (cache "cache-codec") :attic)
        _ (.put c :a large-message)
        recv-val1 (:a c)
        _ (.put c :a "new")
        recv-val2 (:a c)]
    (is (= large-message recv-val1))
    (is (= recv-val2 "new"))))
