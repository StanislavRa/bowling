package com.tieto.bowling.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class Roll extends BaseEntity {

    private int pins;

    @ManyToOne
    private Player player;

    public Roll(Player player) {
        this.player = player;
    }
}
