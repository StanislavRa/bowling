package com.tieto.bowling.service;

import com.tieto.bowling.model.Game;
import com.tieto.bowling.model.Player;
import com.tieto.bowling.model.Roll;
import com.tieto.bowling.repository.GameRepository;
import com.tieto.bowling.repository.PlayerRepository;
import com.tieto.bowling.repository.RollRepository;
import com.tieto.bowling.utility.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImplementation implements GameService {

    final PlayerRepository playerRepository;
    final RollRepository rollRepository;
    final GameRepository gameRepository;

    static final int MAX_FRAME_NUMBER = 10;
    static final int BALLS_IN_REGULAR_FRAME = 2;
    static final int BALLS_IN_LAST_FRAME = 3;
    static final int MAX_ROLL_NUMBER = (MAX_FRAME_NUMBER - 1) * BALLS_IN_REGULAR_FRAME + BALLS_IN_LAST_FRAME;
    static final int MAX_PINS = 10;


    @Autowired
    public GameServiceImplementation(PlayerRepository playerRepository,
                                     RollRepository rollRepository,
                                     GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.rollRepository = rollRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public List<Player> getAllPlayersByGameId(long id) {
        List<Player> foundPlayers = playerRepository.getAllByGame_Id(id);
        foundPlayers.forEach(this::calculateForPlayerScoreAndFrames);

        return foundPlayers;
    }

    @Override
    public Optional<Game> getGameById(long id) {
        return gameRepository.findById(id);
    }

    @Override
    public Game createGame() {
        return gameRepository.save(new Game());
    }

    @Override
    public void deleteGame(long id) {
        gameRepository.deleteById(id);
    }

    @Override
    public Optional<Player> getPlayerById(long id) {
        Optional<Player> foundPlayer = playerRepository.findById(id);
        foundPlayer.ifPresent(this::calculateForPlayerScoreAndFrames);
        return foundPlayer;
    }

    @Override
    public void addPlayerToGame(String playerName, long gameId) {
        Player player = new Player(playerName);
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        gameOptional.ifPresent(player::setGame);
        for (int i = 0; i < MAX_ROLL_NUMBER; i++) player.addToRoll(new Roll());
        playerRepository.save(player);
    }

    @Override
    public void submitPlayerRoll(long playerId, int pins) {
        Optional<Player> optionalPlayer = getPlayerById(playerId);
        if (optionalPlayer.isPresent() && pins >= 0) {
            Player player = optionalPlayer.get();
            int gameRollCounter = player.getRollCounter();
            player.getRolls().get(gameRollCounter).setPins(pins);
            List<Roll> updatedRolls = player.getRolls();

            if (validateRolls(updatedRolls)) {
                player.setRollCounter(gameRollCounter + 1);
                playerRepository.save(player);
            }
        }
    }

    private void calculateForPlayerScoreAndFrames(Player player) {
        List<Roll> gameRolls = player.getRolls();
        int rollCounter = player.getRollCounter();
        Frame[] gameFrames = new Frame[MAX_FRAME_NUMBER];

        int firstBallInFrame = 0;
        int score = 0;

        for (int i = 0; i < gameFrames.length; i++) {
            gameFrames[i] = new Frame();
            if (firstBallInFrame < rollCounter) {
                if (isStrike(gameRolls.get(firstBallInFrame).getPins())) {
                    gameFrames[i].setFrameScore(10 + nextTwoBallsAfterStrike(gameRolls, firstBallInFrame));
                    gameFrames[i].setFrameState(getStrikeFrameState(gameRolls,
                            firstBallInFrame,
                            isLastFrameRoll(i + 1, gameFrames.length)));
                    firstBallInFrame++;
                } else if (isSpare(gameRolls, firstBallInFrame)) {
                    gameFrames[i].setFrameScore(10 + nextBallAfterSpare(gameRolls, firstBallInFrame));
                    gameFrames[i].setFrameState(getSpareFrameState(gameRolls,
                            firstBallInFrame,
                            isLastFrameRoll(i + 1, gameFrames.length)));
                    firstBallInFrame += 2;

                } else {
                    gameFrames[i].setFrameScore(twoBallsInFrame(gameRolls, firstBallInFrame));
                    gameFrames[i].setFrameState(getOpenFrameState(gameRolls, firstBallInFrame));
                    firstBallInFrame += 2;
                }
                score += gameFrames[i].getFrameScore();
            }
        }
        player.setGameScore(score);
        player.setFrames(gameFrames);
    }

    private boolean validateRolls(List<Roll> rolls) {
        int firstBallInFrame = 0;
        for (int i = 0; i < MAX_FRAME_NUMBER; i++) {
            if (isStrike(rolls.get(firstBallInFrame).getPins())) {
                firstBallInFrame++;
            } else if (isSpare(rolls, firstBallInFrame)) {
                firstBallInFrame += 2;

            } else if (isViolating(rolls, firstBallInFrame)) {
                return false;
            } else {
                firstBallInFrame += 2;
            }
        }
        return true;
    }

    private String getOpenFrameState(List<Roll> rolls, int firstBallInFrame) {
        return rolls.get(firstBallInFrame).getPins() + " " + rolls.get(firstBallInFrame + 1).getPins();

    }

    private String getSpareFrameState(List<Roll> rolls, int firstBallInFrame, boolean isLastFrame) {
        return isLastFrame ?
                rolls.get(firstBallInFrame).getPins() + " / " + returnXIfNumberIs10(rolls.get(firstBallInFrame + 2)
                                                                                         .getPins()) :
                rolls.get(firstBallInFrame).getPins() + " / ";
    }

    private String getStrikeFrameState(List<Roll> rolls, int firstBallInFrame, boolean isLastFrame) {
        return isLastFrame ?
                "X " + returnXIfNumberIs10(rolls.get(firstBallInFrame + 1).getPins()) + " "
                        + returnXIfNumberIs10(rolls.get(firstBallInFrame + 2).getPins()) :
                "X";
    }

    private String returnXIfNumberIs10(int number) {
        return number == 10 ? "X" : String.valueOf(number);
    }

    private boolean isLastFrameRoll(int maxFrameSize, int currentFrame) {
        return maxFrameSize == currentFrame;
    }

    private int twoBallsInFrame(List<Roll> rolls, int firstBallInFrame) {
        return rolls.get(firstBallInFrame).getPins() + rolls.get(firstBallInFrame + 1).getPins();
    }

    private int nextBallAfterSpare(List<Roll> rolls, int firstBallInFrame) {
        return rolls.get(firstBallInFrame + 2).getPins();
    }

    private int nextTwoBallsAfterStrike(List<Roll> rolls, int firstBallInFrame) {
        return rolls.get(firstBallInFrame + 1).getPins() + rolls.get(firstBallInFrame + 2).getPins();
    }

    private boolean isStrike(int roll) {
        return roll == MAX_PINS;
    }

    private boolean isSpare(List<Roll> rolls, int firstBallInFrame) {
        return rolls.get(firstBallInFrame).getPins() + rolls.get(firstBallInFrame + 1).getPins() == MAX_PINS;
    }

    private boolean isViolating(List<Roll> rolls, int firstBallInFrame) {
        return rolls.get(firstBallInFrame).getPins() + rolls.get(firstBallInFrame + 1).getPins() > MAX_PINS;
    }
}
