(ns {{ project-name }}.core
	"Application core namespace. Used to setup the application."
	(:require-macros 
  [rum.core :refer [defc]])
	(:require
	 [rum.core :as rum]))

(defc App
  "Base component of your app.
  Use this as an entrypoint to setup a router, translations, etc."
  < rum/static
  []
  [:div
   [:h1 "Hello React"]])


;;; APP SETUP AND TEARDOWN

(defn setup
  "Setup the App."
  []
  ;; your code to setup the app goes here
)



(defn teardown
  "Teardown the App."
  []
  ;; use this function to cleanup. I.e. unregister event handlers.
)


(defn on-js-reload
  "App Reload Bevaviour.

  Gets called when the js app is reloaded. Properly tears down the app and
  (re-) initialized the app to a consistent state."
  []
  (teardown)
  (setup))

;; Mount the app
(rum/mount (App) (.getElementById js/document "app"))