(ns meo.electron.renderer.ui.charts.time.twenty-four-hour
  (:require [reagent.core :as rc]
            [meo.electron.renderer.ui.charts.common :as cc]
            [meo.common.utils.misc :as u]
            [re-frame.core :refer [subscribe]]
            [reagent.ratom :refer-macros [reaction]]
            [clojure.pprint :as pp]
            [moment]
            [meo.electron.renderer.charts.data :as cd]))

(defn ts-bars
  "Renders group with rects for all stories of the particular day."
  [day-stats local item-name-k idx chart-h y-scale put-fn]
  (let [stories (subscribe [:stories])
        sagas (subscribe [:sagas])
        stacked-reducer (fn [acc [k v]]
                          (let [total (get acc :total 0)]
                            (-> acc
                                (assoc-in [:total] (+ total v))
                                (assoc-in [:items k :v] v)
                                (assoc-in [:items k :y] total))))]
    (fn [day-stats local item-name-k idx chart-h y-scale put-fn]
      (let [day (moment (:date-string day-stats))
            day-millis (.valueOf day)
            mouse-enter-fn (cc/mouse-enter-fn local day-stats)
            mouse-leave-fn (cc/mouse-leave-fn local day-stats)
            w 9
            x-step 10
            midnight (* 26 60 60 y-scale)
            midnight-s (* 2 60 60 y-scale)
            time-by-ts (:time-by-ts day-stats)
            time-by-h (map (fn [[ts v]]
                             (let [h (/ (- ts day) 1000 60 60)]
                               [h v])) time-by-ts)
            sagas @sagas
            stories @stories]
        [:g {:on-mouse-enter mouse-enter-fn
             :on-mouse-leave mouse-leave-fn}
         (for [[hh {:keys [summed manual] :as data}] time-by-h]
           (let [item-name (if (= item-name-k :story-name)
                             (get-in stories [(:story data) :story-name])
                             (get-in sagas [(:saga data) :saga-name]))
                 h (* y-scale summed)
                 y (* y-scale (+ hh 2) 60 60)
                 y (if (pos? manual) (- y h) y)]
             ^{:key (str item-name hh)}
             [:g
              (let [h (min h (- midnight y))
                    h (if (< y midnight-s)
                        (- h (- midnight-s y))
                        h)
                    y (max midnight-s y)]
                [:rect {:fill           (cc/item-color item-name)
                        :on-mouse-enter #(prn item-name hh summed)
                        :x              (+ 20 (* x-step idx))
                        :y              y
                        :width          w
                        :height         h}])
              (when (> (+ y h) midnight)
                (let [h (- (+ y h) midnight)
                      y midnight-s]
                  [:rect {:fill           (cc/item-color item-name)
                          :on-mouse-enter #(prn item-name hh summed)
                          :x              (+ 20 (* x-step (inc idx)))
                          :y              y
                          :width          w
                          :height         h}]))
              (when (< y midnight-s)
                (let [h (- midnight-s y)
                      y (- midnight h)]
                  [:rect {:fill           (cc/item-color item-name)
                          :on-mouse-enter #(prn item-name hh summed)
                          :x              (+ 20 (* x-step (dec idx)))
                          :y              y
                          :width          w
                          :height         h}]))]))]))))

(defn legend [text x y]
  [:text {:x           x
          :y           y
          :stroke      "none"
          :fill        "#333"
          :text-anchor :middle
          :style       {:font-size 6}}
   text])

(defn earlybird-nightowl
  "Renders chart with daily recorded times, split up by story."
  [indexed local item-name-k chart-h y-scale put-fn]
  [:svg.earlybird
   {:shape-rendering "crispEdges"
    :style           {:height chart-h}}
   [:g
    (for [h (range 28)]
      (let [y (* chart-h (/ h 28))
            stroke-w (if (zero? (mod (- h 2) 6)) 1 0.5)
            stroke-w (if (or (< h 2) (> h 26)) 0 stroke-w)]
        ^{:key h}
        [:line {:x1           17
                :x2           500
                :y1           y
                :y2           y
                :stroke-width stroke-w
                :stroke       "#666"}]))
    [legend "00:00" 8 18]
    [legend "06:00" 8 66]
    [legend "12:00" 8 113]
    [legend "18:00" 8 160]
    [legend "24:00" 8 208]
    [:g
     (for [[idx v] indexed]
       (let [h (* y-scale (:total-time v))
             mouse-enter-fn (cc/mouse-enter-fn local v)
             mouse-leave-fn (cc/mouse-leave-fn local v)]
         ^{:key (str idx)}
         [ts-bars v local item-name-k idx chart-h y-scale put-fn]))]]])
