(ns meo.ui.photos
  (:require [meo.ui.shared :refer [view text touchable-highlight cam-roll
                                   scroll map-view mapbox-style-url image]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [re-frame.core :refer [reg-sub subscribe]]))

(def defaults {:background-color "lightgreen"
               :padding-left     15
               :padding-right    15
               :padding-top      10
               :padding-bottom   10
               :margin-right     10})

(defn photos-page [local put-fn]
  [scroll {:style {:flex-direction "column"
                   :padding-top    10
                   :padding-bottom 10}}

   [view {:style {:flex-direction "row"
                  :padding-top    10
                  :padding-bottom 10
                  :padding-left   10
                  :padding-right  10}}
    [touchable-highlight
     {:style    defaults
      :on-press #(let [params (clj->js {:first     50
                                        :assetType "All"})
                       photos-promise (.getPhotos cam-roll params)]
                   (.then photos-promise
                          (fn [r]
                            (let [parsed (js->clj r :keywordize-keys true)]
                              (swap! local assoc-in [:photos] parsed)))))}
     [text {:style {:color       "white"
                    :text-align  "center"
                    :font-weight "bold"}}
      "get photos"]]]

   (for [photo (:edges (:photos @local))]
     (let [node (:node photo)
           loc (:location node)
           img (:image node)]
       [view {:style {:padding-top    10
                      :padding-bottom 10
                      :margin-bottom  10
                      :width          "100%"
                      :display        :flex
                      :flex-direction :row}}
        [image {:style  {:width      160
                         :height     160
                         :max-width  160
                         :max-height 160}
                :source {:uri (:uri img)}}]
        (when (:latitude loc)
          [map-view {:showUserLocation true
                     :centerCoordinate [(:longitude loc) (:latitude loc)]
                     :scrollEnabled    false
                     :rotateEnabled    false
                     :styleURL         (get mapbox-style-url (:map-style @local))
                     :style            {:width  200
                                        :flex   2
                                        :height 160}
                     :zoomLevel        15}])]))

   [text {:style {:color       "#777"
                  :text-align  "center"
                  :font-size   10
                  :font-weight "bold"}}
    (str (dissoc (:photos @local) :edges))]])

(defn photos-wrapper [local put-fn]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [photos-page local put-fn])))

(defn photos-tab [local put-fn]
  (stack-navigator
    {:photos {:screen (stack-screen (photos-wrapper local put-fn)
                                    {:title "Photos"})}}))
