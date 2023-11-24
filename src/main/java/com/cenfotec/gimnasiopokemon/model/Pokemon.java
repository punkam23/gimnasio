package com.cenfotec.gimnasiopokemon.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pokemons")
@Data
public class Pokemon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int vida;
}
