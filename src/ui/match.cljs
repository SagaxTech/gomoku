(ns ui.match
  (:require [ai.game :as g]
            [ai.gomoku :as gomoku]
            [ai.random-player :as random-player]
            [ui.clock :refer [clock seconds-passed]]
            [rum.core :as rum])
  (:refer-clojure :exclude [update]))

(defrecord Match
  [board
   board-name
   starting-board
   players ; {:x {:name "Human"  :move-fn :human}
           ;  :o {:name "Random" :move-fn random-player/play}}
   last-move-time])

(def human-player  {:name "Human"  :move-fn :human})
(def random-player {:name "Random" :move-fn random-player/play})

(def available-players
  [human-player
   random-player
   ])

(def available-boards
  [{:name "Tic-Tac-Toe"       :board (gomoku/empty-board 3 3)}
   {:name "4-in-a-row on 9x9" :board (gomoku/empty-board 9 4)}
   {:name "Gomoku (15x15)"    :board (gomoku/empty-board 15 5)}])

(defn new-random-ai-match []
  (let [tic-tac-toe (first available-boards)]
    (map->Match {:board (:board tic-tac-toe)
                 :starting-board (:board tic-tac-toe)
                 :board-name (:name tic-tac-toe)
                 :players {:x random-player
                           :o random-player}
                 :last-move-time @clock})))

(defn pause-seconds [match]
  (if (g/finished? (:board match))
    3 1))

(defn human-turn? [match]
  (= :human
     (get-in match
             [:players
              (g/next-player (:board match))
              :move-fn])))

(defn time-to-update? [match]
  (and (not (human-turn? match))
       (< (pause-seconds match)
          (seconds-passed (:last-move-time match)
                          @clock))))

(defn play-move [match position]
  (-> match
    (update-in [:board] g/play position)
    (assoc :last-move-time @clock)))

(defn update [match]
  (let [board (:board match)
        new-board (cond
                    (g/finished? board) (:starting-board match)
                    (human-turn? match) board
                    :else (let [move-fn (get-in match
                                                [:players
                                                 (g/next-player board)
                                                 :move-fn])]
                            (g/play board (move-fn board))))]
    (-> match
      (assoc :board new-board)
      (assoc :last-move-time @clock))))

(defn click-position [match position]
  (if (and (human-turn? match)
           ((g/available-moves (:board match)) position))
    (play-move match position)
    match))
