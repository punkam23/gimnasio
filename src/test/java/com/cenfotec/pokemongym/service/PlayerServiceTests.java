package com.cenfotec.pokemongym.service;

import com.cenfotec.pokemongym.Domain.*;
import com.cenfotec.pokemongym.repository.BattleRepository;
import com.cenfotec.pokemongym.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlayerServiceTests {

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private BattleRepository battleRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Test
    public void testSetNextPlayer() {
        // GIVEN
        BattleDomain mockBattle = new BattleDomain();
        mockBattle.setId(1L);
        mockBattle.setState(BattleStateEnum.LOBBY.name());

        PlayerDomain player1 = new PlayerDomain();
        player1.setId(1L);
        player1.setState(PlayerStateEnum.EN_BATALLA.name());
        player1.setName("Ash");
        player1.setBattleReference("1");

        PlayerDomain player2 = new PlayerDomain();
        player2.setId(2L);
        player2.setState(PlayerStateEnum.DERROTADO.name());
        player2.setName("Gary");
        player2.setBattleReference("1");

        when(battleRepository.findAll()).thenReturn(List.of(mockBattle));
        when(playerRepository.findAllByBattleReference(any(String.class))).thenReturn(new ArrayList<>(List.of(player1, player2)));

        // WHEN
        PlayerDomain result = playerService.setNextPlayer(player1);

        // THEN
        assertNull(result);
        assertEquals(PlayerStateEnum.DERROTADO.name(), player2.getState());
        assertEquals(BattleStateEnum.TERMINADA.name(), mockBattle.getState());
    }

    @Test
    public void testGetCurrentBattle() {
        // GIVEN
        BattleDomain battleInLobby = new BattleDomain();
        battleInLobby.setState(BattleStateEnum.LOBBY.name());
        battleInLobby.setId(1L);
        List<BattleDomain> battles = List.of(battleInLobby);

        when(battleRepository.findAll()).thenReturn(battles);

        // WHEN
        Optional<BattleDomain> result = playerService.getCurrentBattle();

        // THEN
        assertTrue(result.isPresent());
        assertEquals(battleInLobby, result.get());
    }



    @Test
    public void testEqualOnPlayerDomain() {
        PlayerDomain obj1 = new PlayerDomain();
        obj1.setBattleReference("1");
        obj1.setId(1L);
        obj1.setName("playerDomain1");

        PlayerDomain obj2 = new PlayerDomain();
        obj2.setBattleReference("1");
        obj2.setId(1L);
        obj2.setName("playerDomain1");

        PlayerDomain obj3 = new PlayerDomain();
        obj3.setBattleReference("1");
        obj3.setId(3L);
        obj3.setName("playerDomain3");

        assertTrue(obj1.equals(obj1));

        // Symmetric: if obj1 equals obj2, then obj2 equals obj1
        assertTrue(obj1.equals(obj2));
        assertTrue(obj2.equals(obj1));

        // Transitive: if obj1 equals obj2 and obj2 equals obj3, then obj1 equals obj3
        assertTrue(obj1.equals(obj2));
        assertFalse(obj2.equals(obj3));
        assertFalse(obj1.equals(obj3));

        // Consistent: if the objects haven't changed, multiple calls should return the same result
        assertTrue(obj1.equals(obj2));
        assertTrue(obj1.equals(obj2));
        assertTrue(obj1.equals(obj2));

        // Non-nullity: an object must not be equal to null
        assertFalse(obj1.equals(null));
    }
}
