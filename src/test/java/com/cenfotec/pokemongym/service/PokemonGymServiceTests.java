package com.cenfotec.pokemongym.service;

import com.cenfotec.pokemongym.BackgroundTaskService;
import com.cenfotec.pokemongym.DTO.BattleResponse;
import com.cenfotec.pokemongym.Domain.*;
import com.cenfotec.pokemongym.model.Attack;
import com.cenfotec.pokemongym.model.PlayerInformation;
import com.cenfotec.pokemongym.model.Pokemon;
import com.cenfotec.pokemongym.model.PokemonType;
import com.cenfotec.pokemongym.repository.BattleRepository;
import com.cenfotec.pokemongym.repository.PlayerRepository;
import com.cenfotec.pokemongym.repository.PokemonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static com.cenfotec.pokemongym.utils.CommonUtils.checkBattleState;
import static com.cenfotec.pokemongym.utils.CommonUtils.checkPlayerState;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@SpringBootTest
class PokemonGymServiceTests {

    @InjectMocks
    private PokemonGymService pokemonGymService;

    @Mock
    private BattleRepository battleRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private BackgroundTaskService backgroundTaskService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void testJoinNewBattleSuccess() {
        // GIVEN
        PlayerDomain mockAttackingPlayer = new PlayerDomain();
        mockAttackingPlayer.setName("Ash");
        mockAttackingPlayer.setId(1L);
        mockAttackingPlayer.setState(PlayerStateEnum.EN_BATALLA.name());
        mockAttackingPlayer.setBattleReference("1");

        Attack attack = new Attack();
        attack.setPower(50);
        attack.setType(PokemonType.fuego);

        Pokemon pokemon = new Pokemon();
        pokemon.setName("Pikachu");
        pokemon.setLife(1000);
        pokemon.setType(PokemonType.fuego);
        pokemon.setAttacks(List.of(attack));

        PlayerInformation playerInformation = new PlayerInformation();
        playerInformation.setPlayerName("Ash");
        playerInformation.setPokemon(pokemon);

        when(battleRepository.findAll()).thenReturn(List.of());
        when(playerRepository.save(any(PlayerDomain.class))).thenReturn(mockAttackingPlayer);
        when(pokemonGymService.addNewBattle()).thenReturn(mock(BattleDomain.class));

        // WHEN
        ResponseEntity<Object> result = pokemonGymService.joinBattle(playerInformation);

        // THEN
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testJoinExistingBattleSuccess() {
        // GIVEN
        PlayerDomain mockAttackingPlayer = new PlayerDomain();
        mockAttackingPlayer.setName("Ash");
        mockAttackingPlayer.setId(1L);
        mockAttackingPlayer.setState(PlayerStateEnum.EN_BATALLA.name());
        mockAttackingPlayer.setBattleReference("1");

        BattleDomain mockBattle = new BattleDomain();
        mockBattle.setState(BattleStateEnum.LOBBY.name());
        mockBattle.setId(1L);

        Attack attack = new Attack();
        attack.setPower(50);
        attack.setType(PokemonType.fuego);

        Pokemon pokemon = new Pokemon();
        pokemon.setName("Pikachu");
        pokemon.setLife(1000);
        pokemon.setType(PokemonType.fuego);
        pokemon.setAttacks(List.of(attack));

        PlayerInformation playerInformation = new PlayerInformation();
        playerInformation.setPlayerName("Ash");
        playerInformation.setPokemon(pokemon);

        when(battleRepository.findAll()).thenReturn(List.of(mockBattle));
        when(playerRepository.findByNameAndBattleReference("Ash","1")).thenReturn(null);
        when(playerRepository.save(any(PlayerDomain.class))).thenReturn(mockAttackingPlayer);

        // WHEN
        ResponseEntity<Object> result = pokemonGymService.joinBattle(playerInformation);

        // THEN
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testAttackPokemon() {
        // GIVEN
        String sourcePlayerName = "Ash";
        String targetPlayerName = "Gary";
        int attackId = 1;

        BattleDomain mockBattle = new BattleDomain();
        mockBattle.setState(BattleStateEnum.EN_BATALLA.name());
        mockBattle.setId(1L);

        PlayerDomain mockAttackingPlayer = new PlayerDomain();
        mockAttackingPlayer.setName("Ash");
        mockAttackingPlayer.setId(1L);
        mockAttackingPlayer.setState(PlayerStateEnum.ATACANDO.name());
        mockAttackingPlayer.setBattleReference("1");

        PokemonDomain mockAttackingPokemon = new PokemonDomain();
        mockAttackingPokemon.setName("Pikachu");
        mockAttackingPokemon.setId(1L);
        mockAttackingPokemon.setType(PokemonType.fuego.name());
        mockAttackingPokemon.setPlayerReference("1");
        mockAttackingPokemon.setLife(100);
        mockAttackingPokemon.setAttackList("[{\"type\":\"fuego\",\"power\":100}]");

        PlayerDomain mockAttackedPlayer = new PlayerDomain();
        mockAttackedPlayer.setName("Gary");
        mockAttackedPlayer.setId(2L);
        mockAttackedPlayer.setState(PlayerStateEnum.EN_BATALLA.name());
        mockAttackedPlayer.setBattleReference("1");

        PokemonDomain mockModifiedPokemon = new PokemonDomain();
        mockModifiedPokemon.setName("Pikachu");
        mockModifiedPokemon.setId(2L);
        mockModifiedPokemon.setType(PokemonType.agua.name());
        mockModifiedPokemon.setPlayerReference("2");
        mockModifiedPokemon.setLife(100);
        mockModifiedPokemon.setAttackList("[{\"type\":\"agua\",\"power\":100}]");

        Attack mockAttack = new Attack();
        mockAttack.setType(PokemonType.planta);
        mockAttack.setPower(40);

        when(playerService.getCurrentBattle()).thenReturn(Optional.of(mockBattle));
        when(battleRepository.findAll()).thenReturn(List.of(mockBattle));
        when(playerRepository.findAllByBattleReference(any(String.class))).thenReturn(new ArrayList<>(List.of(mockAttackingPlayer, mockAttackedPlayer)));
        when(pokemonRepository.findByPlayerReference("1")).thenReturn(Optional.of(mockAttackingPokemon));
        when(pokemonRepository.findByPlayerReference("2")).thenReturn(Optional.of(mockModifiedPokemon));
        when(playerRepository.findByNameAndBattleReference(sourcePlayerName, mockBattle.getId().toString()))
                .thenReturn(mockAttackingPlayer);
        when(playerRepository.findByNameAndBattleReference(targetPlayerName, mockBattle.getId().toString()))
                .thenReturn(mockAttackedPlayer);
        when(playerRepository.save(any(PlayerDomain.class))).thenReturn(mockAttackingPlayer, mockAttackedPlayer);
        when(pokemonRepository.save(any(PokemonDomain.class))).thenReturn(mockModifiedPokemon);
        doNothing().when(backgroundTaskService).resetScheduler();
        // WHEN
        ResponseEntity<Object> result = pokemonGymService.attackPokemon(sourcePlayerName, targetPlayerName, attackId);

        // THEN
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testGetBattleInfo() {
        // GIVEN
        BattleDomain mockBattle = new BattleDomain();
        mockBattle.setId(1L);
        mockBattle.setState(BattleStateEnum.LOBBY.name());

        PlayerDomain mockPlayer = new PlayerDomain();
        mockPlayer.setId(1L);
        mockPlayer.setName("Ash");
        mockPlayer.setState(PlayerStateEnum.EN_BATALLA.name());
        mockPlayer.setBattleReference(mockBattle.getId().toString());

        PokemonDomain mockPokemon = new PokemonDomain();
        mockPokemon.setId(1L);
        mockPokemon.setName("Pikachu");
        mockPokemon.setLife(100);
        mockPokemon.setType(PokemonType.planta.name());
        mockPokemon.setPlayerReference("1");

        mockPokemon.setAttackList("[{\"type\":\"fuego\",\"power\":12}]");

        when(battleRepository.findAll()).thenReturn(List.of(mockBattle));
        when(playerRepository.findAllByBattleReference("1")).thenReturn(List.of(mockPlayer));
        when(pokemonRepository.findByPlayerReference("1")).thenReturn(Optional.of(mockPokemon));
        when(playerService.getCurrentBattle()).thenReturn(Optional.of(mockBattle));

        // WHEN
        BattleResponse result = pokemonGymService.getBattleInfo();

        // THEN
        assertEquals(mockBattle.getId(), result.getId());
        assertEquals(mockBattle.getState(), result.getState());

        List<PlayerInformation> playerInformationList = result.getPlayerInformationList();
        assertEquals(1, playerInformationList.size());

        PlayerInformation playerInformation = playerInformationList.get(0);
        assertEquals(mockPlayer.getName(), playerInformation.getPlayerName());
        assertEquals(mockPlayer.getState(), playerInformation.getState());

        Pokemon pokemonDTO = playerInformation.getPokemon();
        assertEquals(mockPokemon.getName(), pokemonDTO.getName());
        assertEquals(mockPokemon.getLife(), pokemonDTO.getLife());
        assertEquals(PokemonType.planta, pokemonDTO.getType());
    }

    @Test
    public void testAddPlayer() throws JsonProcessingException {
        // GIVEN
        PlayerInformation playerInformation = new PlayerInformation();
        playerInformation.setPlayerName("Ash");
        Pokemon pokemon = new Pokemon();
        pokemon.setName("Pikachu");
        pokemon.setLife(100);
        pokemon.setType(PokemonType.fuego);
        Attack attack = new Attack();
        attack.setPower(50);
        attack.setType(PokemonType.fuego);
        pokemon.setAttacks(List.of(attack));
        playerInformation.setPokemon(pokemon);

        BattleDomain battleDomain = new BattleDomain();
        battleDomain.setId(1L);

        PlayerDomain newPlayer = new PlayerDomain();
        newPlayer.setId(2L);
        newPlayer.setName(playerInformation.getPlayerName());
        newPlayer.setBattleReference(battleDomain.getId().toString());
        newPlayer.setState(PlayerStateEnum.EN_BATALLA.name());

        when(playerRepository.save(any(PlayerDomain.class))).thenReturn(newPlayer);

        PokemonDomain newPokemon = new PokemonDomain();
        newPokemon.setName(playerInformation.getPokemon().getName());
        newPokemon.setLife(playerInformation.getPokemon().getLife());
        newPokemon.setType(playerInformation.getPokemon().getType().name());
        newPokemon.setPlayerReference(newPlayer.getId().toString());
        String jsonString = objectMapper.writeValueAsString(playerInformation.getPokemon().getAttacks());
        newPokemon.setAttackList(jsonString);

        when(pokemonRepository.save(any(PokemonDomain.class))).thenReturn(newPokemon);

        // WHEN
        pokemonGymService.addPlayer(playerInformation, battleDomain);

        // THEN
        assertEquals("Ash", newPlayer.getName());
        assertEquals("Pikachu", newPokemon.getName());
        assertEquals(100, newPokemon.getLife());
        assertEquals(PokemonType.fuego.name(), newPokemon.getType());
    }

    @Test
    public void testStartBattleSuccess() {
        // GIVEN
        BattleDomain lobbyBattle = new BattleDomain();
        lobbyBattle.setState(BattleStateEnum.LOBBY.name());
        lobbyBattle.setId(1L);
        List<PlayerDomain> playersInLobby = new ArrayList<>();
        PlayerDomain playerInLobby = new PlayerDomain();
        playerInLobby.setState(PlayerStateEnum.EN_BATALLA.name());
        playersInLobby.add(playerInLobby);

        when(battleRepository.findAll()).thenReturn(List.of(lobbyBattle));
        when(playerRepository.findAllByBattleReference(any(String.class))).thenReturn(playersInLobby);
        when(playerService.getCurrentBattle()).thenReturn(Optional.of(lobbyBattle));
        doNothing().when(backgroundTaskService).resetScheduler();

        // WHEN
        ResponseEntity<Object> result = pokemonGymService.startBattle();

        // THEN
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testStartBattleError() {
        // GIVEN
        BattleDomain inProgressBattle = new BattleDomain();
        inProgressBattle.setState(BattleStateEnum.EN_BATALLA.name());

        when(battleRepository.findAll()).thenReturn(List.of(inProgressBattle));

        // WHEN
        ResponseEntity<Object> result = pokemonGymService.startBattle();

        // THEN
        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void testAddNewBattle() {
        // GIVEN
        BattleDomain newBattle = new BattleDomain();
        newBattle.setState(BattleStateEnum.LOBBY.name());

        when(battleRepository.save(Mockito.any())).thenReturn(newBattle);

        // WHEN
        BattleDomain result = pokemonGymService.addNewBattle();

        // THEN
        assertEquals(BattleStateEnum.LOBBY.name(), result.getState());
    }



    @Test
    public void testCheckBattleState() {
        // GIVEN
        BattleDomain battleDomain = new BattleDomain();
        battleDomain.setState(BattleStateEnum.LOBBY.name());

        // WHEN
        boolean result = checkBattleState(battleDomain, BattleStateEnum.LOBBY);

        // THEN
        assertTrue(result);
    }

    @Test
    public void testCheckPlayerState() {
        // GIVEN
        PlayerDomain player = new PlayerDomain();
        player.setState(PlayerStateEnum.EN_BATALLA.name());

        // WHEN
        boolean result = checkPlayerState(player, PlayerStateEnum.EN_BATALLA);

        // THEN
        assertTrue(result);
    }

    @Test
    public void testCalculateAttack() {
        // GIVEN
        String pokemonType = PokemonType.fuego.name();
        Attack attack = new Attack();
        attack.setPower(100);
        attack.setType(PokemonType.agua);

        // WHEN
        double result = pokemonGymService.calculateAttack(pokemonType, attack);

        // THEN
        assertEquals(150, result);
    }

    @Test
    public void testCreateResponseSuccess() {
        // GIVEN
        String message = "Operation succeeded";
        boolean success = true;

        // WHEN
        ResponseEntity<Object> responseEntity = pokemonGymService.createResponse(message, success);

        // THEN
        assertEquals(200, responseEntity.getStatusCode().value());
    }
}
