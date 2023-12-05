package com.cenfotec.pokemongym.repository;

import com.cenfotec.pokemongym.Domain.BattleDomain;
import com.cenfotec.pokemongym.Domain.BattleStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleRepository extends JpaRepository<BattleDomain, Long> {
    BattleDomain findByState(BattleStateEnum state);
}
