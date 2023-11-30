package com.cenfotec.gimnasiopokemon.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Attack {

    @JsonProperty("type")
    PokemonType type;

    @JsonProperty("power")
    int power;
}
