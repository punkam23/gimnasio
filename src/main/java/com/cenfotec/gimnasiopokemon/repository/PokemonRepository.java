package com.cenfotec.gimnasiopokemon.repository;

import com.cenfotec.gimnasiopokemon.Domain.PokemonDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonRepository extends JpaRepository<PokemonDomain, Long> {
}
