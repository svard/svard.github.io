(ns rx.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:import [goog.net Jsonp]))

(enable-console-print!)

(def search-wikipedia
  (let [jsonp (Jsonp. "http://en.wikipedia.org/w/api.php")]
    (.fromCallback js/Rx.Observable (.-send jsonp) (.-timeout js/Rx.Scheduler) jsonp)))

(defn display
  [show]
  (if show
    #js {}
    #js {:display "none"}))

(defn autocomplete
  [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:results []
       :visible false
       :selected ""})
    om/IDidMount
    (did-mount [_]
      (let [input (om/get-node owner "input")]
        (-> (.fromEvent js/Rx.Observable input "keyup")
            (.map #(.. % -target -value))
            (.filter #(< 2 (.-length %)))
            (.throttle 500)
            (.distinctUntilChanged)
            (.flatMapLatest #(search-wikipedia #js {:action "opensearch"
                                                    :format "json"
                                                    :search %}))
            (.subscribe (fn [x]
                          (when (< 0 (count (nth (js->clj x) 1)))
                            (om/set-state! owner :results (nth (js->clj x) 1))
                            (om/set-state! owner :visible true)))))

        (-> (.fromEvent js/Rx.Observable input "blur")
            (.delay 250)
            (.subscribe #(om/set-state! owner :visible false)))))
    om/IRenderState
    (render-state [_ {:keys [results visible]}]
      (html [:div.autocomplete
             [:input {:id "input"
                      :type "text"
                      :ref "input"
                      :placeholder "Search wikipedia"
                      :value (om/get-state owner :selected)
                      :on-change #(om/set-state! owner :selected (.. % -target -value))}]
             [:ul {:style (display visible)}
              (for [result results]
                [:li {:on-click #(om/set-state! owner :selected result)} result])]]))))

(om/root autocomplete {} {:target (. js/document getElementById "app")})
