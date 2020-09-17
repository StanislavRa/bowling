package com.tieto.bowling.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class Game extends BaseEntity {

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    List<Player> players;

}
