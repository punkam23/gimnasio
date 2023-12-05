package com.cenfotec.pokemongym.repository;

import com.cenfotec.pokemongym.Domain.PlayerDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerDomain, Long> {
    PlayerDomain findByNameAndBattleReference(String name, String battleReference);
    List<PlayerDomain> findAllByBattleReference(String battleReference);
}
