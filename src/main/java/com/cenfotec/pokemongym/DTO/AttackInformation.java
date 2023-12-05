package com.cenfotec.pokemongym.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AttackInformation {
    @JsonProperty("attackId")
    Integer attackId;
    @JsonProperty("targetPlayerName")
    String targetPlayerName;
    @JsonProperty("sourcePlayerName")
    String sourcePlayerName;
}
