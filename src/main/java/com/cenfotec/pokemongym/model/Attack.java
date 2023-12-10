package com.cenfotec.pokemongym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Attack {

    @JsonProperty("type")
    PokemonType type;

    @JsonProperty("power")
    int power;
}
