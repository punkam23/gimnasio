package com.cenfotec.pokemongym.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO {
    @JsonProperty("success")
    boolean success;
    @JsonProperty("message")
    Object message;
}
