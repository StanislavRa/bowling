package com.tieto.bowling.repository;

import com.tieto.bowling.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
