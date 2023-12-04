package com.cenfotec.gimnasiopokemon.repository;

import com.cenfotec.gimnasiopokemon.Domain.PokemonDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<PokemonDomain, Long> {
    Optional<PokemonDomain> findByJugadorReference(String jugadorReference);
}
