package com.cenfotec.pokemongym.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pokemons")
@Setter
@Getter
public class PokemonDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String attackList;
    private double life;
    private String type;
    private String playerReference;
}
