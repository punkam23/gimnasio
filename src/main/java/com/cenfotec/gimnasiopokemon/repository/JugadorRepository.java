package com.cenfotec.gimnasiopokemon.repository;

import com.cenfotec.gimnasiopokemon.Domain.JugadorDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JugadorRepository extends JpaRepository<JugadorDomain, Long> {
    JugadorDomain findByNameAndBatallaReference(String name, String batallaReference);
    List<JugadorDomain> findAllByBatallaReference(String batallaReference);
}
