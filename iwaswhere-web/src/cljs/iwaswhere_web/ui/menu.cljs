(ns iwaswhere-web.ui.menu
  (:require [iwaswhere-web.helpers :as h]
            [matthiasn.systems-toolbox-ui.reagent :as r]))

(defn cfg-view
  "Renders component for toggling display of maps, comments, ..."
  [store-snapshot put-fn]
  (let [cfg (:cfg store-snapshot)
        show-all-maps? (:show-all-maps cfg)
        toggle-all-maps #(put-fn [:cmd/toggle-key {:path [:cfg :show-all-maps]}])
        show-tags? (:show-hashtags cfg)
        toggle-tags #(put-fn [:cmd/toggle-key {:path [:cfg :show-hashtags]}])
        show-redacted? (:redacted cfg)
        toggle-redacted #(put-fn [:cmd/toggle-key {:path [:cfg :redacted]}])
        comments-w-entries? (:comments-w-entries cfg)
        toggle-comments-w-entries #(put-fn [:cmd/toggle-key
                                            {:path [:cfg :comments-w-entries]}])
        show-pvt? (:show-pvt cfg)
        toggle-pvt #(put-fn [:cmd/toggle-key {:path [:cfg :show-pvt]}])
        show-thumbnails? (:thumbnails cfg)
        toggle-thumbnails #(put-fn [:cmd/toggle-key {:path [:cfg :thumbnails]}])
        mute? (:mute cfg)
        toggle-mute #(put-fn [:cmd/toggle-key {:path [:cfg :mute]}])
        sort-by-upvotes? (:sort-by-upvotes cfg)
        toggle-upvotes
        #(let [query (merge (:current-query store-snapshot)
                            {:sort-by-upvotes (not sort-by-upvotes?)})]
          (put-fn [:cmd/toggle-key {:path [:cfg :sort-by-upvotes]}])
          (put-fn [:state/get query]))]
    [:div
     [:span.fa.fa-thumbs-up.toggle
      {:class (when-not sort-by-upvotes? "inactive") :on-click toggle-upvotes}]
     [:span.fa.fa-user-secret.toggle
      {:class (when-not show-pvt? "inactive") :on-click toggle-pvt}]
     [:span.fa.fa-eye.toggle
      {:class (when show-redacted? "inactive") :on-click toggle-redacted}]
     [:span.fa.fa-comments.toggle
      {:class (when-not comments-w-entries? "inactive")
       :on-click toggle-comments-w-entries}]
     [:span.fa.fa-volume-off.toggle
      {:class (when-not mute? "inactive") :on-click toggle-mute}]
     [:span.fa.fa-hashtag.toggle
      {:class (when-not show-tags? "inactive") :on-click toggle-tags}]
     [:span.fa.toggle
      {:class    (if show-all-maps? "fa-map" "fa-map-o")
       :on-click toggle-all-maps}]
     [:span.fa.fa-ellipsis-h.toggle
      {:on-click #(put-fn [:cmd/toggle-lines])}]
     [:span.fa.fa-photo.toggle
      {:class    (when-not show-thumbnails? "inactive")
       :on-click toggle-thumbnails}]]))

(defn new-import-view
  "Renders component for rendering new and import buttons."
  [{:keys [observed put-fn]}]
  [:div.menu-header
   [:div
    [:button.menu-new {:on-click (h/new-entry-fn put-fn {})}
     [:span.fa.fa-plus-square] " new"]
    [:button {:on-click #(do (put-fn [:import/photos])
                             (put-fn [:import/geo])
                             (put-fn [:import/weight])
                             (put-fn [:import/phone]))}
     [:span.fa.fa-map] " import"]]
   [:h1 "iWasWhere?"]
   [cfg-view @observed put-fn]
   [:img {:src "/upload-address.png"}]])

(defn cmp-map
  [cmp-id]
  (r/cmp-map {:cmp-id  cmp-id
              :view-fn new-import-view
              :dom-id  "header"}))
