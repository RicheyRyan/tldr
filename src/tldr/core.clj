(ns tldr.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [pantomime.extract :as extract]
            [opennlp.nlp :as nlp]))

(def page "http://www.fastcodesign.com/3056415/ideo-silicon-valleys-most-influential-design-firm-sells-a-minority-stake")
(def get-sentences (nlp/make-sentence-detector "models/en-sent.bin"))
(def tokenize (nlp/make-tokenizer "models/en-token.bin"))
(def tmp-file "/tmp/tldr/page.html")

(defn fetch-page
  [page]
  (println "Fetching external HTML page")
  (->
   (client/get page {:as :stream})
   (:body)))

(defn save-tmp-file
  [content]
  (println "Saving HTML in tmp file")
  (clojure.java.io/make-parents tmp-file)
  (clojure.java.io/copy content (java.io.File. tmp-file)))

(defn strip-newlines
  [body]
  (apply str (filter (fn [c] (not= c \newline)) body)))

(defn trim-extra-whitespace
  [body]
  (clojure.string/replace (clojure.string/trim body) #"\s{2,}" " "))

(defn save-page
  [page]
  (println "Starting to fetch HTML")
  (->
   (fetch-page page)
   (save-tmp-file)))

(defn extract-content
  [file]
  (println "Extracting content from file")
  (->
   (extract/parse file)
   (:text)
   (strip-newlines)
   (trim-extra-whitespace)
   (get-sentences)))

(defn -main
  "I don't do a whole lot."
  []
  (save-page page)
  (clojure.pprint/pprint (extract-content tmp-file)))

(frequencies (clojure.string/split (first sentences) #"\s"))
(merge-with + (map #(frequencies (clojure.string/split % #"\s")) sentences))
(reduce conj (map #(frequencies (clojure.string/split % #"\s")) sentences))
