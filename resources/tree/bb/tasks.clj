(ns tasks
  "Arbitrary tasks."
  (:require
    [babashka.process :refer [shell]]
    [clojure.term.colors :as c]
    [git-hooks :as gh]))


(defn prepare!
  "Prepare the workspace.
  This includes:
  - setup git hooks in local repo
  - download required node packages
  - fetch all bb dependencies"
  []
  (shell {:inherit true} "yarn")
  (shell {:inherit true} "clojure -Ttools install nvd-clojure/nvd-clojure '{:mvn/version \"RELEASE\"}' :as nvd")
  (gh/hooks "install"))


;; Multimethod to handle all tasks to run a local development setup
;; for the webapp.
;; Requires that the workspace was correctly initialized with the `prepare` task!
(defmulti dev (fn [& args] (first args)))


;; Run a shadow-cljs development server with hot reloading
(defmethod dev "shadow-cljs" [& args]
  (println (c/blue "[dev:shadow-cljs]" "starting..."))
  (shell {:inherit true} "npx shadow-cljs watch dev -A :dev"))


;; Run a postcss development server with hot reloading
(defmethod dev "postcss" [& args]
  (println (c/blue "[dev:postcss]" "starting..."))
  (shell {:inherit true :extra-env {"TAILWIND_MODE" "dev"}} "npx postcss src/css/tailwind.css -o ./resources/public/css/main.css --verbose -w"))


;; Default handler to catch invalid hooks
(defmethod dev :default [& args]
  (println (c/yellow "Unknown command: ") (c/red (first args))))


;; Multimethod to handle all tasks to run a local instance of storybook.
;; This includes tasks to start a storybook server, build the story files from source
;; and build CSS. Including hot reloading.
(defmulti storybook (fn [& args] (first args)))


(defmethod storybook "shadow-cljs" [& args]
  (println (c/blue "[storybook:shadow-cljs]" "starting..."))
  (shell {:inherit true} "npx shadow-cljs watch stories"))


(defmethod storybook "postcss" [& args]
  (println (c/blue "[storybook:postcss]" "starting..."))
  (shell {:inherit true :extra-env {"TAILWIND_MODE" "dev"}} "npx postcss src/css/tailwind.css -o ./resources/public/css/main.css --verbose -w"))


(defmethod storybook "server" [& args]
  (println (c/blue "[storybook:server]" "starting..."))
  (shell {:inherit true} "npx start-storybook -p 6006 --no-open"))


;; Default handler to catch invalid hooks
(defmethod storybook :default [& args]
  (println (c/yellow "Unknown command: ") (c/red (first args))))


;; Multimethod to handle all tasks to run a local instance of Cypress for component tests.
;; This includes tasks to start a cypress test server, build the component tests from source
;; and build CSS. Including hot reloading.
(defmulti cypress (fn [& args] (first args)))


(defmethod cypress "shadow-cljs" [& args]
  (println (c/blue "[cypress:shadow-cljs]" "starting..."))
  (shell {:inherit true} "npx shadow-cljs watch cy-component -A :cy-component"))


(defmethod cypress "postcss" [& args]
  (println (c/blue "[cypress:postcss]" "starting..."))
  (shell {:inherit true :extra-env {"TAILWIND_MODE" "cy-component"}} "npx postcss src/css/tailwind.css -o ./resources/cy-component/compiled/main.css --verbose -w"))


(defmethod cypress "server" [& args]
  (println (c/blue "[cypress:server]" "starting..."))
  (shell {:inherit true} "npx cypress open --component"))


;; Default handler to catch invalid hooks
(defmethod cypress :default [& args]
  (println (c/yellow "Unknown command: ") (c/red (first args))))

