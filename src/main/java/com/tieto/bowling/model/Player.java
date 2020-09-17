package com.tieto.bowling.model;

import com.tieto.bowling.utility.Frame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class Player extends BaseEntity {

    @NotBlank(message = "Name is mandatory")
    private String playerName;

    private int rollCounter = 0;
    private int gameScore;

    @ManyToOne
    Game game;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Roll> rolls = new ArrayList<>();

    @Transient
    Frame[] frames;

    public Player(@NotBlank(message = "Name is mandatory") String playerName) {
        this.playerName = playerName;
    }

    public void addToRoll(Roll roll) {
        this.getRolls().add(roll);
        roll.setPlayer(this);
    }
}
