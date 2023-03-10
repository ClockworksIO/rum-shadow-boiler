(ns docs
  "Babashka related to the documentation of the project."
  (:require
    [babashka.process :refer [sh]]
    [clci.term :refer [with-c]]))


(defmulti docs! (fn [& args] (first args)))


;; Build the documentation
(defmethod docs! "build" [& _]
  (-> (sh "mkdocs build") :out println))


;; Run a server on localhost to serve the documentation
(defmethod docs! "serve" [& _]
  (-> (sh "mkdocs serve") :out println))


;; Default handler to catch invalid hooks
(defmethod docs! :default [& args]
  (println (with-c :yellow "Unknown command: ") (with-c :red (first args))))
