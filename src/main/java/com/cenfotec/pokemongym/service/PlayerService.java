package com.cenfotec.pokemongym.service;

import com.cenfotec.pokemongym.Domain.BattleDomain;
import com.cenfotec.pokemongym.Domain.BattleStateEnum;
import com.cenfotec.pokemongym.Domain.PlayerDomain;
import com.cenfotec.pokemongym.Domain.PlayerStateEnum;
import com.cenfotec.pokemongym.repository.BattleRepository;
import com.cenfotec.pokemongym.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.cenfotec.pokemongym.utils.CommonUtils.checkPlayerState;

@Service
public class PlayerService {

    @Autowired
    private  BattleRepository battleRepository;

    @Autowired
    private  PlayerRepository playerRepository;

    public Optional<BattleDomain> getCurrentBattle() {
        List<BattleDomain> battles = this.battleRepository.findAll();
        List<String> states = List.of(BattleStateEnum.EN_BATALLA.name(), BattleStateEnum.LOBBY.name());
        return battles.stream().filter(battleDomain -> states.contains(battleDomain.getState())).findFirst();
    }

    public PlayerDomain setNextPlayer(PlayerDomain attackingPlayer) {
        // Search a current battle
        Optional<BattleDomain> currentBattle = getCurrentBattle();
        if (currentBattle.isPresent()) {
            // If there is a current battle, search the waiting players and look for the current player index
            BattleDomain currentBattleInstance = currentBattle.get();
            List<PlayerDomain> playerDomainList = this.playerRepository
                    .findAllByBattleReference(currentBattleInstance.getId().toString());
            playerDomainList.sort(Comparator.comparingLong(PlayerDomain::getId));
            playerDomainList = playerDomainList.stream().filter(playerDomain -> checkPlayerState(playerDomain, PlayerStateEnum.EN_BATALLA)).toList();
            int attackingPlayerIndex = playerDomainList.indexOf(attackingPlayer);
            int nextPlayerIndex = attackingPlayerIndex + 1;
            // Check if the next the player is the first player again
            if (nextPlayerIndex > playerDomainList.size() - 1) {
                nextPlayerIndex = 0;
            }
            PlayerDomain nextPlayer = playerDomainList.get(nextPlayerIndex);
            // Check if the battles is over
            if (nextPlayer.getName().equals(attackingPlayer.getName())) {
                nextPlayer.setState(PlayerStateEnum.GANADOR.name());
                currentBattleInstance.setState(BattleStateEnum.TERMINADA.name());
                this.battleRepository.save(currentBattleInstance);
                return null;
            } else {
                nextPlayer.setState(PlayerStateEnum.ATACANDO.name());
            }
            // Save the next player in the database
            this.playerRepository.save(nextPlayer);
            return nextPlayer;
        }
        return null;
    }
}
