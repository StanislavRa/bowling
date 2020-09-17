package com.tieto.bowling.repository;

import com.tieto.bowling.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> getAllByGame_Id(long gameId);
}
