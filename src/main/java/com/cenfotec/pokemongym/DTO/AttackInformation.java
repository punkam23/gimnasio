package com.cenfotec.pokemongym.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttackInformation {

    @NotNull
    @JsonProperty("attackId")
    Integer attackId;

    @NotNull
    @JsonProperty("targetPlayerName")
    String targetPlayerName;

    @NotNull
    @JsonProperty("sourcePlayerName")
    String sourcePlayerName;
}
