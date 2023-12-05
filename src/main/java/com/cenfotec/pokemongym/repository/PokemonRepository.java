package com.cenfotec.pokemongym.repository;

import com.cenfotec.pokemongym.Domain.PokemonDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<PokemonDomain, Long> {
    Optional<PokemonDomain> findByPlayerReference(String playerReference);
}
