package com.tieto.bowling.repository;

import com.tieto.bowling.model.Roll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RollRepository extends JpaRepository<Roll, Long> {
}
