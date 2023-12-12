package com.cenfotec.pokemongym;

import com.cenfotec.pokemongym.Domain.*;
import com.cenfotec.pokemongym.model.Attack;
import com.cenfotec.pokemongym.model.PokemonType;
import com.cenfotec.pokemongym.repository.BattleRepository;
import com.cenfotec.pokemongym.repository.PlayerRepository;
import com.cenfotec.pokemongym.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BackgroundTaskServiceTest {

    @InjectMocks
    private BackgroundTaskService backgroundTaskService;

    @Mock
    private BattleRepository battleRepository;

    @Mock
    private ScheduledExecutorService scheduler;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test method
        backgroundTaskService = spy(backgroundTaskService);
    }

    @Test
    public void testResetScheduler() {
        doNothing().when(scheduler).shutdown();
        backgroundTaskService.resetScheduler();
        assertEquals("", backgroundTaskService.getCurrentAttackingPlayerName());
    }

    @Test
    public void testRunScheduledTaskFirstTime() {
        // GIVEN

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

        when(playerRepository.findAllByBattleReference(any(String.class))).thenReturn(new ArrayList<>(List.of(mockAttackingPlayer, mockAttackedPlayer)));
        when(battleRepository.findAll()).thenReturn(List.of(mockBattle));

        // WHEN
        backgroundTaskService.runScheduledTask();

        // THEN
        assertNotNull(backgroundTaskService.getCurrentAttackingPlayerName());
        assertEquals("Ash", backgroundTaskService.getCurrentAttackingPlayerName());
    }

    @Test
    public void testRunScheduledTaskSetNextPlayer() {
        // GIVEN

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

        when(playerRepository.findAllByBattleReference(any(String.class))).thenReturn(new ArrayList<>(List.of(mockAttackingPlayer, mockAttackedPlayer)));
        when(battleRepository.findAll()).thenReturn(List.of(mockBattle));
        when(backgroundTaskService.getCurrentAttackingPlayerName()).thenReturn("Ash");
        when(playerService.setNextPlayer(any(PlayerDomain.class))).thenReturn(mockAttackedPlayer);

        // WHEN
        backgroundTaskService.runScheduledTask();

        // THEN
        when(backgroundTaskService.getCurrentAttackingPlayerName()).thenCallRealMethod();
        assertNotNull(backgroundTaskService.getCurrentAttackingPlayerName());
        assertEquals("Gary", backgroundTaskService.getCurrentAttackingPlayerName());
    }
}
