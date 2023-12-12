package com.cenfotec.pokemongym;

import com.cenfotec.pokemongym.Domain.BattleDomain;
import com.cenfotec.pokemongym.Domain.BattleStateEnum;
import com.cenfotec.pokemongym.Domain.PlayerDomain;
import com.cenfotec.pokemongym.Domain.PlayerStateEnum;
import com.cenfotec.pokemongym.repository.BattleRepository;
import com.cenfotec.pokemongym.repository.PlayerRepository;
import com.cenfotec.pokemongym.service.PlayerService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.cenfotec.pokemongym.utils.CommonUtils.checkBattleState;

@Service
public class BackgroundTaskService implements SchedulingConfigurer {

    private ScheduledExecutorService scheduler;

    private static ScheduledFuture<?> scheduledFuture;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    private String currentAttackingPlayerName = Strings.EMPTY;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        startSchedule();
    }

    public String getCurrentAttackingPlayerName() {
        return currentAttackingPlayerName;
    }

    public void resetScheduler() {
        scheduler.shutdown();
        scheduledFuture.cancel(true);
        startSchedule();
    }

    private void startSchedule() {
        scheduler = new ScheduledThreadPoolExecutor(1);
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            runScheduledTask();
            System.out.println("Running background task...");
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void runScheduledTask() {
        List<BattleDomain> battleList = this.battleRepository.findAll();
        Optional<BattleDomain> currentBattle = battleList.stream().filter(battleDomain -> checkBattleState(battleDomain, BattleStateEnum.EN_BATALLA)).findFirst();
        if(currentBattle.isPresent() && Strings.isNotEmpty(getCurrentAttackingPlayerName())){
            List<PlayerDomain> playerDomainList = this.playerRepository
                    .findAllByBattleReference(currentBattle.get().getId().toString());
            Optional<PlayerDomain> attackingPlayer = playerDomainList.stream().filter(playerDomain ->
                    playerDomain.getState().equals(PlayerStateEnum.ATACANDO.name())).findFirst();
            if (attackingPlayer.isPresent()){
                if(attackingPlayer.get().getName().equals(getCurrentAttackingPlayerName())){
                    // calculate new turn
                    attackingPlayer.get().setState(PlayerStateEnum.EN_BATALLA.name());
                    this.playerRepository.save(attackingPlayer.get());
                    PlayerDomain isNextPlayerAvailable = this.playerService.setNextPlayer(attackingPlayer.get());
                    if(Objects.isNull(isNextPlayerAvailable)){
                        currentAttackingPlayerName = Strings.EMPTY;
                    }else {
                        currentAttackingPlayerName = isNextPlayerAvailable.getName();
                    }
                }else{
                    currentAttackingPlayerName = attackingPlayer.get().getName();
                }
            }
        }else if(Strings.isEmpty(getCurrentAttackingPlayerName()) && currentBattle.isPresent()){
            List<PlayerDomain> playerDomainList = this.playerRepository
                    .findAllByBattleReference(currentBattle.get().getId().toString());
            Optional<PlayerDomain> attackingPlayer = playerDomainList.stream().filter(playerDomain -> playerDomain.getState().equals(PlayerStateEnum.ATACANDO.name())).findFirst();
            if(attackingPlayer.isPresent()){
                currentAttackingPlayerName = attackingPlayer.get().getName();
            }
        } else if (currentBattle.isEmpty()) {
            currentAttackingPlayerName = Strings.EMPTY;
        }
    }
}
