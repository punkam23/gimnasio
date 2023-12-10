package com.cenfotec.pokemongym.controller;

import com.cenfotec.pokemongym.DTO.*;
import com.cenfotec.pokemongym.Domain.BattleStateEnum;
import com.cenfotec.pokemongym.Domain.PlayerStateEnum;
import com.cenfotec.pokemongym.model.PlayerInformation;
import com.cenfotec.pokemongym.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class PokemonGymControllerTests {

    @InjectMocks
    private PokemonGymController pokemonGymController;

    @Mock
    private PokemonGymService pokemonGymService;

    @Test
    public void startBattle() {
        // GIVEN

        // WHEN
        when(pokemonGymService.startBattle()).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Object> response = pokemonGymController.startBattle();

        // THEN
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testAttackPokemon() {
        // GIVEN
        AttackInformation attackInformation = new AttackInformation();
        attackInformation.setAttackId(1);
        attackInformation.setTargetPlayerName("targetPlayer");
        attackInformation.setSourcePlayerName("sourcePlayer");

        // WHEN
        when(pokemonGymService.attackPokemon(any(String.class), any(String.class), any(Integer.class))).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Object> response = pokemonGymController.attackPokemon(attackInformation);

        // THEN
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testJoinBattle() {
        // GIVEN
        PlayerInformation playerInformation = new PlayerInformation();
        playerInformation.setPlayerName("Ash");
        playerInformation.setState(PlayerStateEnum.DISPONIBLE.name());

        // WHEN
        when(pokemonGymService.joinBattle(any(PlayerInformation.class))).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Object> response = pokemonGymController.joinBattle(playerInformation);

        // THEN
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void getBattleInfo() {
        // GIVEN
        BattleResponse battleResponse = new BattleResponse();
        battleResponse.setState(BattleStateEnum.LOBBY.name());

        // WHEN
        when(pokemonGymService.getBattleInfo()).thenReturn(battleResponse);
        ResponseEntity<BattleResponse> response = pokemonGymController.getBattleInfo();

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals(battleResponse, response.getBody());
    }
}
