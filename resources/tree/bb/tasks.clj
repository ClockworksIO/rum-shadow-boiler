(ns tasks
  "Arbitrary tasks."
  (:require
    [babashka.process :refer [shell]]
    [clci.term :refer [with-c]]
    [git-hooks :as gh]
    [proc-wrapper :refer [wrap]]))


(defn prepare!
  "Prepare the workspace.
  This includes:
  - setup git hooks in local repo
  - download required node packages
  - fetch all bb dependencies"
  []
  (shell {:inherit true} "yarn")
  (gh/hooks "install"))


;; Multimethod to handle all tasks to run a local development setup
;; for the webapp.
;; Requires that the workspace was correctly initialized with the `prepare` task!
(defmulti dev (fn [& args] (first args)))


;; Run a shadow-cljs development server with hot reloading
(defmethod dev "shadow-cljs" [& _]
  (println (with-c :blue "[dev:shadow-cljs]") "starting ...")
  (wrap ["[shadow-cljs]" :green] ["clojure -M -m shadow.cljs.devtools.cli --force-spawn watch dev"]))


;; Run a postcss development server with hot reloading
(defmethod dev "postcss" [& _]
  (println (with-c :blue  "[dev:postcss]") "starting ...")
  (wrap ["[postcss]" :blue] [{:extra-env {"TAILWIND_MODE" "dev"}} "npx postcss src/css/tailwind.css -o ./resources/public/css/main.css --verbose -w"]))


;; Default handler to catch invalid hooks
(defmethod dev :default [& args]
  (println (with-c :yellow "Unknown command: ") (with-c :red (first args))))


;; Multimethod to handle all tasks to run a local instance of storybook.
;; This includes tasks to start a storybook server, build the story files from source
;; and build CSS. Including hot reloading.
(defmulti storybook (fn [& args] (first args)))


(defmethod storybook "shadow-cljs" [& _]
  (println (with-c :blue "[storybook:shadow-cljs]") "starting ...")
  (wrap ["[shadow-cljs]" :green] ["clojure -M -m shadow.cljs.devtools.cli --force-spawn watch stories"]))


(defmethod storybook "postcss" [& _]
  (println (with-c :blue "[storybook:postcss]") "starting ...")
  (wrap ["[postcss]" :blue] [{:extra-env {"TAILWIND_MODE" "dev"}} "npx postcss src/css/tailwind.css -o ./resources/public/css/main.css --verbose -w"]))


(defmethod storybook "server" [& _]
  (println (with-c :blue "[storybook:server]") "starting ...")
  (wrap ["[storybook]" :yellow] ["npx start-storybook -p 6006 --no-open"]))


;; Default handler to catch invalid hooks
(defmethod storybook :default [& args]
  (println (with-c :yellow "Unknown command: ") (with-c :red (first args))))


;; Multimethod to handle all tasks to run a local instance of Cypress for component tests.
;; This includes tasks to start a cypress test server, build the component tests from source
;; and build CSS. Including hot reloading.
(defmulti cypress (fn [& args] (first args)))


(defmethod cypress "shadow-cljs" [& _]
  (println (with-c :blue "[cypress:shadow-cljs]") "starting ...")
  (wrap ["[shadow-cljs]" :green] ["clojure -M -m shadow.cljs.devtools.cli --force-spawn watch cy-component"]))


(defmethod cypress "postcss" [& _]
  (println (with-c :blue "[cypress:postcss]") "starting. ..")
  (wrap ["[postcss]" :blue] [{:extra-env {"TAILWIND_MODE" "cy-component"}} "npx postcss src/css/tailwind.css -o ./resources/cy-component/compiled/main.css --verbose -w"]))


(defmethod cypress "server" [& _]
  (println (with-c :blue "[cypress:server]") "starting...")
  (wrap ["[cy]" :yellow] ["npx cypress open --component"]))


;; Default handler to catch invalid hooks
(defmethod cypress :default [& args]
  (println (with-c :yellow "Unknown command: ") (with-c :red (first args))))

