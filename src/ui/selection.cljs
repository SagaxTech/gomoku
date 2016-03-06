(ns ui.selection
  (:require [rum.core :as rum]))

(rum/defc single < rum/reactive [selected selectables render-item]
  (rum/react selected)
  [:span (for [selectable selectables]
           [:span {:on-click #(reset! selected selectable)}
            (render-item selectable
                         (= @selected selectable))])])
  
  (defn render-as-horizontal-buttons
    ([] render-as-horizontal-buttons str)
    ([item-to-text]
      (fn [item selected]
        [:button
         (when selected
           {:style {:text-decoration "underline"}})
         (item-to-text item)])))
  