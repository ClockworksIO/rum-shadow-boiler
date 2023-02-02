(ns git-hooks
  "This module defines several funtions that are invoked by Git hooks."
  (:require
    [babashka.process :refer [sh]]
    [clci.conventional-commit :refer [valid-commit-msg?]]
    [clci.git-hooks-utils :refer [spit-hook changed-files]]
    [clojure.term.colors :as c]))