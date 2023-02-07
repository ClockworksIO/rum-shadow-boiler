(ns boiler
  "This module holds the functions to create a project boilerplate."
  (:require
    [babashka.fs :as fs]
    [clojure.string :as str]
    [clojure.term.colors :as c]
    [clojure.tools.cli :refer [parse-opts]])
  (:import
    java.time.LocalDateTime
    java.time.format.DateTimeFormatter))


(def cli-options
  "Options to create a new project."
  ;; An option with a required argument
  [["-p" "--path PATH" "Path where to create the new project."]
   ["-n" "--name PROJECT-NAME" "Name of the new project."]
   ["-d" "--description DESCRIPTION" "Description of the new project (optional)."
    :default "no project description available."]
   ["-w" "--website WEBSITE" "Link to the website of the new project (optional)."
    :default "https://www.example.com"]
   ["-r" "--repository REPOSITORY" "Url of the repository of the new project (optional)."
    :default "https://www.example.com"]
   ["-o" "--repository-owner REPOSITORY-OWNER" "Organization/User who owns the repository of the new project (optional)."
    :default "anonymous"]
   ["-N" "--repository-name REPOSITORY-NAME" "Name of the repository of the new project (optional)."
    :default "example"]
   ["-a" "--author AUTHOR" "Author of the project (optional)."
    :default ""]
   ["-l" "--license LICENSE" (str
                               "The license used for the project (optional). One of\n"
                               "- Apache-2.0\n"
                               "- EPL-2.0")
    :default "unknown"]
   ["-h" "--help"]])


(defn current-year
  "Get the current year as string in the format _yyyy_."
  []
  (let [date (LocalDateTime/now)
        formatter (DateTimeFormatter/ofPattern "yyyy")]
    (.format date formatter)))


(defn replace-in-file!
  "Replace several values in a text file.
  Takes the `path` of the file and a list of argument pairs with a regex pattern
  and a replacement. I.e. `(replace-in-file! \"./some/file.txt\" #\"a\" \"b\")`
  Source: https://stackoverflow.com/a/9569328/5841420"
  [path & replacements]
  (let [content (slurp path)
        replacement-list (partition 2 replacements)]
    (->> (reduce #(apply str/replace %1 %2) content replacement-list)
         (spit path))))


(defn get-license-url
  "Get the matching url for the specified license."
  [license]
  (case license
    "Apache-2.0" "https://www.apache.org/licenses/LICENSE-2.0"
    "EPL-2.0" "https://www.eclipse.org/legal/epl-2.0/"
    ;; defaults to unknown
    "unknown"))


(defn write-license-file
  "Get the matching url for the specified license.
  Takes the `options` map and the `base-path` of the new project."
  [base-path options]
  (let [resource-dir "./resources/licenses/"
        target-license-file (format "%sLICENSE" base-path)
        license (:license options)
        author (:author options)]
    (case license
      "Apache-2.0" (do
                     (fs/copy (format "%sApache-2.0.txt" resource-dir) target-license-file)
                     (replace-in-file!
                       target-license-file
                       #"\{\{\ copyright-year }\}" (current-year)
                       #"\{\{\ copyright-holder }\}" author))
      "EPL-2.0" (fs/copy (format "%sEPL-2.0.txt" resource-dir) target-license-file)
      ;; defaults to unknown
      "unknown")))


;; https://book.babashka.org/#_parsing_command_line_arguments

(defmulti new (fn [& args] (first args)))


;; Git 'commit-msg' hook.
;; Takes the commit message and validates it conforms to the Conventional Commit specification
(defmethod new "project" [& args]
  (let [opts        (parse-opts *command-line-args* cli-options)
        options     (:options opts)
        base-path   (:path options)]
    ;; if any mandatory arguments are missing, exit
    (when-not (and (some? (:path options)) (some? (:name options)))
      (println (c/red "Creating a new project requires at least the '--name' and '--path' arguments!")
               (System/exit 1)))
    ;; copy tree for project base
    (fs/copy-tree "./resources/tree" base-path)
    ;; setup license - if any known
    (write-license-file base-path options)
    ;; setup deps.edn file
    (replace-in-file!
      (format "%s/deps.edn" base-path)
      #"\{\{\ project-description }\}" (:description options)
      #"\{\{\ project-homepage }\}" (:website options)
      #"\{\{\ project-repository }\}" (:repository options)
      #"\{\{\ project-license-name }\}" (:license options)
      #"\{\{\ project-license-url }\}" (get-license-url (:license options)))
    ;; setup package.json
    (replace-in-file!
      (format "%s/package.json" base-path)
      #"\{\{\ project-name }\}" (:name options)
      #"\{\{\ project-description }\}" (:description options)
      #"\{\{\ project-homepage }\}" (:website options)
      #"\{\{\ project-repository }\}" (:repository options)
      #"\{\{\ project-license-name }\}" (:license options)
      #"\{\{\ project-author }\}" (:author options))
    ;;
    (replace-in-file!
      (format "%s/README.md" base-path)
      #"\{\{\ project-name }\}" (:name options)
      #"\{\{\ project-description }\}" (:description options)
      #"\{\{\ project-license-name }\}" (:license options))
    ;; setup webapp resource files
    (replace-in-file!
      (format "%s/resources/public/index.html" base-path) #"\{\{\ project-name }\}" (:name options))
    ;; setup webapp development and build
    (replace-in-file!
      (format "%s/shadow-cljs.edn" base-path) #"\{\{\ project-name }\}" (:name options))
    ;; setup mkdocs config
    (replace-in-file!
      (format "%s/mkdocs.yml" base-path)
      #"\{\{\ project-name }\}" (:description options)
      #"\{\{\ project-description }\}" (:description options)
      #"\{\{\ project-homepage }\}" (:website options)
      #"\{\{\ project-repository-url }\}" (:repository options)
      #"\{\{\ project-repository-name }\}" (format "%s/%s" (:repository-owner options) (:repository-name options)))
    (fs/create-dirs (format "%s/docs" base-path))
    ;; init core namespace
    (fs/create-dirs (format "%s/src/%s" base-path (:name options)))
    (fs/copy-tree "./resources/src/css" (format "%s/src/css" base-path))
    (fs/copy-tree "./resources/src/story" (format "%s/src/story" base-path))
    (fs/copy-tree "./resources/src/app" (format "%s/src/%s/" base-path (:name options)))
    (replace-in-file!
      (format "%s/src/%s/core.cljs" base-path (:name options))
      #"\{\{\ project-name }\}" (:name options))
    (replace-in-file!
      (format "%s/src/%s/constants.cljs" base-path (:name options))
      #"\{\{\ project-name }\}" (:name options))))


;; Default handler to catch invalid hooks
(defmethod new :default [& args]
  (println (c/yellow "Unknown command: ") (c/red (first args))))


