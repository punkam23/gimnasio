package com.cenfotec.pokemongym.utils;

import com.cenfotec.pokemongym.Domain.BattleDomain;
import com.cenfotec.pokemongym.Domain.BattleStateEnum;
import com.cenfotec.pokemongym.Domain.PlayerDomain;
import com.cenfotec.pokemongym.Domain.PlayerStateEnum;

public class CommonUtils {
    private CommonUtils() {}

    public static boolean checkBattleState(BattleDomain battle, BattleStateEnum state) {
        return BattleStateEnum.valueOf(battle.getState()).equals(state);
    }

    public static boolean checkPlayerState(PlayerDomain player, PlayerStateEnum state) {
        return PlayerStateEnum.valueOf(player.getState()).equals(state);
    }
}
