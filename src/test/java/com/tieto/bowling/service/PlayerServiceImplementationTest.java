package com.tieto.bowling.service;

import com.tieto.bowling.model.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PlayerServiceImplementationTest {

    @Autowired
    GameService gameService;

    @Test
    void shouldSaveTwoGames() {
        gameService.createGame();
        gameService.createGame();
        assertEquals(2, gameService.getAllGames().size());
    }

    @Test
    void shouldCreatePlayerWithAllRolls() {
        gameService.addPlayerToGame("Stan", 1);
        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(21, player.getRolls().size()));
    }

    @Test
    void shouldMakeOneRoll() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 5);
        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(5, player.getGameScore()));
    }

    @Test
    void shouldReturnCorrectFrameStateAndFrameScoreFromOpenFrame() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 2);
        gameService.submitPlayerRoll(1, 4);
        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(6, player.getFrames()[0].getFrameScore()));
        optionalPlayer.ifPresent(player -> assertEquals("2 4", player.getFrames()[0].getFrameState()));
    }

    @Test
    void shouldMakeOneStrike() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 10);
        gameService.submitPlayerRoll(1, 2);
        gameService.submitPlayerRoll(1, 3);
        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(20, player.getGameScore()));
    }

    @Test
    void shouldReturnCorrectFrameStateAndFrameScoreFromOneStrike() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 10);
        gameService.submitPlayerRoll(1, 5);
        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(15, player.getFrames()[0].getFrameScore()));
        optionalPlayer.ifPresent(player -> assertEquals("X", player.getFrames()[0].getFrameState()));
    }

    @Test
    void shouldMakeOneSpare() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 3);
        gameService.submitPlayerRoll(1, 7);
        gameService.submitPlayerRoll(1, 3);

        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(16, player.getGameScore()));
    }

    @Test
    void shouldReturnCorrectFrameStateAndFrameScoreFromOneSpare() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 3);
        gameService.submitPlayerRoll(1, 7);
        gameService.submitPlayerRoll(1, 3);

        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(13, player.getFrames()[0].getFrameScore()));
        optionalPlayer.ifPresent(player -> assertEquals("3 / ", player.getFrames()[0].getFrameState()));
    }

    @Test
    void shouldMakePerfectGame() {
        gameService.addPlayerToGame("John", 1);
        for (int i = 0; i < 21; i++) gameService.submitPlayerRoll(1, 10);
        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(300, player.getGameScore()));
    }

    @Test
    void shouldMakeFullRandomGame() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 10);
        gameService.submitPlayerRoll(1, 7);
        gameService.submitPlayerRoll(1, 3);
        gameService.submitPlayerRoll(1, 7);
        gameService.submitPlayerRoll(1, 2);
        gameService.submitPlayerRoll(1, 9);
        gameService.submitPlayerRoll(1, 1);
        gameService.submitPlayerRoll(1, 10);
        gameService.submitPlayerRoll(1, 10);
        gameService.submitPlayerRoll(1, 10);
        gameService.submitPlayerRoll(1, 2);
        gameService.submitPlayerRoll(1, 3);
        gameService.submitPlayerRoll(1, 6);
        gameService.submitPlayerRoll(1, 4);
        gameService.submitPlayerRoll(1, 7);
        gameService.submitPlayerRoll(1, 3);
        gameService.submitPlayerRoll(1, 3);

        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertEquals(168, player.getGameScore()));
    }

    @Test
    void shouldNotMakeIllegalRoll() {
        gameService.addPlayerToGame("John", 1);
        gameService.submitPlayerRoll(1, 5);
        gameService.submitPlayerRoll(1, 6);
        Optional<Player> optionalPlayer = gameService.getPlayerById(1);
        optionalPlayer.ifPresent(player -> assertNotEquals(11, player.getGameScore()));
    }
}
