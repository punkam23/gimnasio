package com.cenfotec.gimnasiopokemon.repository;

import com.cenfotec.gimnasiopokemon.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
}
