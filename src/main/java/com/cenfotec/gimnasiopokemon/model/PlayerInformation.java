package com.cenfotec.gimnasiopokemon.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlayerInformation {
    @JsonProperty("playerName")
    String playerName;

    @JsonProperty("pokemon")
    Pokemon pokemon;
}
