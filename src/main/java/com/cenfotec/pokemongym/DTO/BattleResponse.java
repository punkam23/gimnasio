package com.cenfotec.pokemongym.DTO;

import com.cenfotec.pokemongym.model.PlayerInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleResponse {
    private Long id;
    private String state;
    private List<PlayerInformation> playerInformationList;
}
