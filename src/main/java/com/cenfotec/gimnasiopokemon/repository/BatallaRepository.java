package com.cenfotec.gimnasiopokemon.repository;

import com.cenfotec.gimnasiopokemon.Domain.BatallaDomain;
import com.cenfotec.gimnasiopokemon.Domain.EstadoBatallaEnum;
import com.cenfotec.gimnasiopokemon.Domain.PokemonDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatallaRepository extends JpaRepository<BatallaDomain, Long> {
    BatallaDomain findByEstadoBatalla(EstadoBatallaEnum estadoBatalla);
}
