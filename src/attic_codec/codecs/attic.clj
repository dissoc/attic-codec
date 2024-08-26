(ns attic-codec.codecs.attic
  "Provides support for using Transit as an Immutant codec."
  (:require [buddy.core.crypto :as crypto]
            [buddy.core.codecs :as codecs]
            [buddy.core.nonce :as nonce]
            [cloboss.codecs           :refer [decode-error
                                              make-codec register-codec]]
            [cloboss.internal.util    :refer [kwargs-or-map->raw-map
                                              try-resolve
                                              try-resolve-throw]]
            [cloboss.internal.options :refer [set-valid-options!
                                              validate-options]]
            [taoensso.nippy :as nippy])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(defn ^:internal ^:no-doc attic-codec
  "attic-codec uses nippy for the serialization and for the encryption
  it uses buddy with chahcha"
  [{:keys [codec-name content-type type read-handlers write-handlers key32 iv8]
    :or   {codec-name :transit type :msgpack}}]
  (let [content-type (or content-type (str "application/nippy+" (name codec-name)))]
    (make-codec
      :name codec-name
      :content-type content-type
      :type :bytes
      :encode (fn [data]
                (let [eng               (crypto/stream-cipher :chacha)
                      _                 (crypto/init! eng {:key key32
                                                           :iv  iv8
                                                           :op  :encrypt})
                      serialized-data   (nippy/freeze data {:encryptor  nil
                                                            :compressor nil})
                      cipher-bytes      (crypto/process-bytes! eng serialized-data)
                      serialized-packet (nippy/freeze {:iv          iv8
                                                       :cipher-data cipher-bytes}
                                                      {:encryptor  nil
                                                       :compressor nil})]
                  serialized-packet))

      :decode (fn [data]
                (when data
                  (let [{iv8 :iv, cipher-data :cipher-data} (nippy/thaw data {:encryptor  nil
                                                                              :compressor nil})
                        eng                                 (crypto/stream-cipher :chacha)
                        eng-conf                            {:key key32
                                                             :iv  iv8
                                                             :op  :decrypt}
                        _                                   (crypto/init! eng eng-conf)
                        clear-data                          (crypto/process-bytes! eng cipher-data)
                        orignal-data-structure              (nippy/thaw clear-data {:encryptor  nil
                                                                                    :compressor nil})]
                    orignal-data-structure))))))

(defn register-attic-codec
  "register attic codec"
  [& options]
  (-> options
      kwargs-or-map->raw-map
      (validate-options register-attic-codec)
      attic-codec
      register-codec))
(set-valid-options! register-attic-codec
                    #{:codec-name :content-type :type :key32 :iv8
                      :read-handlers :write-handlers})
