package com.cenfotec.pokemongym.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pokemons")
@Data
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
