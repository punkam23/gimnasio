package com.cenfotec.pokemongym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerInformation {
    @JsonProperty("playerName")
    String playerName;

    @JsonProperty("state")
    String state;

    @JsonProperty("pokemon")
    Pokemon pokemon;
}
