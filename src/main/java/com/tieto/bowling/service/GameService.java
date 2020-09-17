package com.tieto.bowling.service;

import com.tieto.bowling.model.Game;
import com.tieto.bowling.model.Player;

import java.util.List;
import java.util.Optional;

public interface GameService {
    List<Game> getAllGames();

    List<Player> getAllPlayersByGameId(long id);

    Optional<Game> getGameById(long id);

    Game createGame();

    void deleteGame(long id);

    Optional<Player> getPlayerById(long id);

    void addPlayerToGame(String playerName, long gameManagerId);

    void submitPlayerRoll(long playerId, int pins);
}
