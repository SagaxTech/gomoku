(ns ui.configurable
  (:require [ai.game :as g]
            [ai.gomoku :as gomoku]
            [ai.random-player :as random-player]
            [ui.clock :refer [clock seconds-passed]]
            [ui.match :as match]
            [rum.core :as rum]))

(defn render-square [match-atom position]
  (let [pieces (:pieces (:board @match-atom))]
    [:img {:style {:width "100%" :height "100%"}
           :on-click #(swap! match-atom match/click-position position)
           :src (str "images/"
                     (cond
                       ((:x pieces) position) "x"
                       ((:o pieces) position) "o"
                       :else "empty")
                     ".png")}]))

(defn render-board [match-atom]
  (let [size (:board-size (:board @match-atom))]
    [:table
     (for [row (range size)]
       [:tr (for [col (range size)]
              [:td (render-square match-atom [row col])])])]))

(defn render-game-result [board]
  (when (g/finished? board)
    [:p [:b (if-let [winner (g/who-won board)]
              (str ({:x "X" :o "O"} winner)
                   " won!")
              "Tie Game")]]))

(defn render [match-atom]
  [:div
   (render-board match-atom)
   (render-game-result (:board @match-atom))])

(rum/defcs gomoku-app
  < (rum/local (match/new-random-ai-match))
  rum/reactive
  [{match :rum/local}]
  (rum/react clock)
  (when (match/time-to-update? @match)
    (swap! match match/update))
  (render match))
