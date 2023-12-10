package com.cenfotec.pokemongym.DTO;

import com.cenfotec.pokemongym.model.PlayerInformation;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class BattleResponse {
    private Long id;
    private String state;
    private List<PlayerInformation> playerInformationList;
}
