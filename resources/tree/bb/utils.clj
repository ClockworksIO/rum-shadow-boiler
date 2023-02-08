(ns utils
  "Utilities"
  (:require
    [babashka.process :refer [shell]]
    [clj-kondo.core :as clj-kondo]
    [clojure.term.colors :as c]))


(defn kondo-lint
  "Run kondo to lint the code.
  Raises a non zero exit code if any errors are detected. Takes an optional
  argument `fail-on-warnings?`. If true the function also raises a non zero
  exit code when kondo detects any warnings."
  ([] (kondo-lint false))
  ([fail-on-warnings?]
   (let [{:keys [summary] :as results} (clj-kondo/run! {:lint ["src"]})]
     (clj-kondo/print! results)
     (when (or
             (if fail-on-warnings? (pos? (:warning summary)) false)
             (pos? (:error summary)))
       (throw (ex-info "Linter failed!" {:babashka/exit 1}))))))


(defn clj-nvd-audit
  ""
  []
  (let [path (-> (shell "clojure -Spath -A:any:aliases") :out)
        cmd  (format "clojure -J-Dclojure.main.report=stderr -Tnvd nvd.task/check :classpath \"\"%s\"\"" path)]
    (println (c/red path))
    (println (c/blue cmd))
    (shell
      {:inherit true}
      (format
        "clojure -J-Dclojure.main.report=stderr -Tnvd nvd.task/check :classpath \"\"%s\"\""
        path))))
