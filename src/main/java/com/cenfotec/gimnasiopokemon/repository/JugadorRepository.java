package com.cenfotec.gimnasiopokemon.repository;

import com.cenfotec.gimnasiopokemon.Domain.JugadorDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JugadorRepository extends JpaRepository<JugadorDomain, Long> {
    JugadorDomain findByNameAndBatallaReference(String name, String batallaReference);
}
