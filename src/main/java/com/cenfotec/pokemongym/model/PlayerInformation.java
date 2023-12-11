package com.cenfotec.pokemongym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerInformation {

    @NotNull
    @JsonProperty("playerName")
    String playerName;

    @JsonProperty("state")
    String state;

    @NotNull
    @Valid
    @JsonProperty("pokemon")
    Pokemon pokemon;
}
