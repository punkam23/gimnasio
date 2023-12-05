package com.cenfotec.pokemongym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Pokemon {
    @JsonProperty("name")
    String name;

    @JsonProperty("type")
    PokemonType type;

    @JsonProperty("life")
    double life;

    @JsonProperty("attacks")
    List<Attack> attacks;
}
