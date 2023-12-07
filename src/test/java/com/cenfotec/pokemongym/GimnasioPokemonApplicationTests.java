package com.cenfotec.pokemongym;

import com.cenfotec.pokemongym.DTO.*;
import com.cenfotec.pokemongym.Domain.*;
import com.cenfotec.pokemongym.controller.*;
import com.cenfotec.pokemongym.model.*;
import com.cenfotec.pokemongym.repository.*;
import com.cenfotec.pokemongym.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@SpringBootTest
class GimnasioPokemonApplicationTests {


    @InjectMocks
    private PokemonGymController pokemonController;

    @Mock
    private PokemonRepository pokemonRepository;
    @Mock
    private BattleRepository battleRepository;
    @Mock
    private  PlayerRepository playerRepository;

    @Mock
    private PokemonGymService pokemonService=
            new PokemonGymService(pokemonRepository,battleRepository,playerRepository);




    public void testAttackPokemon() {
        // Arrange
        String sourcePlayerName = "Ash";
        String targetPlayerName = "Gary";
        int attackId = 1;

        // Mock the behavior of the PokemonService
        when(pokemonService.attackPokemon(sourcePlayerName, targetPlayerName, attackId))
                .thenReturn(new ResponseEntity<>("Attack successful", HttpStatus.OK));

        AttackInformation information=new AttackInformation();
        information.setSourcePlayerName(sourcePlayerName);
        information.setTargetPlayerName(targetPlayerName);
        information.setAttackId(attackId);

        // Act
        ResponseEntity<Object> response = pokemonController.attackPokemon(information);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Attack successful", response.getBody());

        // Optionally, verify that the PokemonService method was called with the correct arguments
        Mockito.verify(pokemonService).attackPokemon(sourcePlayerName, targetPlayerName, attackId);
    }


    public void testJoinBattle() {
        // Arrange
        PlayerInformation playerInformation = new PlayerInformation();
        Pokemon pokemon=new Pokemon();
        pokemon.setLife(400);
        pokemon.setName("Picachu");
        Attack attackPrueba=new Attack();
        attackPrueba.setPower(200);
        attackPrueba.setType(PokemonType.planta);
        List<Attack> ataques=new ArrayList<>(1);
        ataques.add(attackPrueba);


        playerInformation.setPlayerName("Ulises");
        playerInformation.setPokemon(pokemon);
        playerInformation.setState("Lobby");

        // Mock the behavior of the BattleService
        when(pokemonService.joinBattle(playerInformation))
                .thenReturn(new ResponseEntity<>("Joined the battle", HttpStatus.OK));

        // Act
        ResponseEntity<Object> response = pokemonController.joinBattle(playerInformation);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Joined the battle", response.getBody());

        // Optionally, verify that the BattleService method was called with the correct arguments
        Mockito.verify(pokemonService).joinBattle(playerInformation);
    }



    public void testGetBattleInfo() {
        // Mock the behavior of the BattleService
        when(pokemonService.getBattleInfo()).thenReturn(new BattleResponse());

        // Act
        ResponseEntity<BattleResponse> responseEntity = pokemonController.getBattleInfo();

        // Assert
        assertEquals("Expecting 'Battle in progress'", "Battle in progress", responseEntity.getBody().getState());


    }



    public void testStartBattle() {
        // Arrange
        BattleDomain mockBattle = new BattleDomain();
        mockBattle.setId(1L);
        mockBattle.setState(BattleStateEnum.LOBBY.name());

        PlayerDomain player1 = new PlayerDomain();
        player1.setId(1L);
        player1.setState(BattleStateEnum.EN_BATALLA.name());

        PlayerDomain player2 = new PlayerDomain();
        player2.setId(2L);
        player2.setState(BattleStateEnum.EN_BATALLA.name());

        //when(pokemonService.getCurrentBattle()).thenReturn(Optional.of(mockBattle));
        //when(pokemonService.checkBattleState(mockBattle, BattleStateEnum.LOBBY)).thenReturn(true);
        //when(playerRepository.findAllByBattleReference("1")).thenReturn(List.of(player1, player2));

        // Act
        ResponseEntity<Object> responseEntity = pokemonController.startBattle();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("The Battle has been initialized, to Battle!.", responseEntity.getBody());
        // Optionally, you might want to verify specific interactions or states of the repositories if needed
    }

}
