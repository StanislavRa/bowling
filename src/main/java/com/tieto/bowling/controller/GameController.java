package com.tieto.bowling.controller;

import com.tieto.bowling.model.Game;
import com.tieto.bowling.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/allGames")
    public String showAllGames(Model model) {
        model.addAttribute("allGames", gameService.getAllGames());
        return "allGames";
    }

    @PostMapping("/allGames")
    public String createGame() {
        gameService.createGame();
        return "redirect:/allGames";
    }

    @GetMapping("/allGames/delete/{id}")
    public String deleteGame(@PathVariable("id") long id) {
        gameService.deleteGame(id);
        return "redirect:/allGames";
    }

    @GetMapping("/allGames/{id}")
    public String showGame(@PathVariable("id") long id, Model model) {
        Optional<Game> gameOptional = gameService.getGameById(id);
        if (gameOptional.isPresent()) {
            model.addAttribute("allPlayersInGame", gameService.getAllPlayersByGameId(id));
            model.addAttribute("gameId", id);
            return "game";
        }
        return "allGames";
    }

    @PostMapping("/allGames/{id}/addPlayer")
    public String addPlayer(@PathVariable("id") long id,  @RequestParam(value = "playerName") String playerName) {
        Optional<Game> gameOptional = gameService.getGameById(id);
        if (gameOptional.isPresent()) {
            gameService.addPlayerToGame(playerName, id);
        }
        return "redirect:/allGames/{id}";
    }

    @PostMapping("/allGames/{gameId}/{playerId}")
    public String submitPlayerRoll(@PathVariable("gameId") long gameId,
                                   @PathVariable("playerId") long playerId,
                              @RequestParam(value = "pins") int pins) {
        Optional<Game> gameOptional = gameService.getGameById(gameId);
        if (gameOptional.isPresent()) {
            gameService.submitPlayerRoll(playerId, pins);
        }
        return "redirect:/allGames/{gameId}";
    }
}
