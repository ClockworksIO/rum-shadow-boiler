(ns story.util
  "Utility module for storybook integration.")


(defn ->params
  "Prepare component params to be used by storybook."
  [^js args]
  (js->clj args :keywordize-keys true))


(defn ->component-meta
  "Derive parts of the storybook documentation from clojure component metadata."
  [mdata]
  {:title         (:name mdata "n.n.")
   :description   (:doc mdata)})
