package com.cenfotec.pokemongym.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttackInformation {
    @JsonProperty("attackId")
    Integer attackId;
    @JsonProperty("targetPlayerName")
    String targetPlayerName;
    @JsonProperty("sourcePlayerName")
    String sourcePlayerName;
}
