package com.cenfotec.gimnasiopokemon.Domain;

import com.cenfotec.gimnasiopokemon.model.Attack;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "pokemons")
@Data
public class PokemonDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String attackList;
    private int vida;
    private String jugadorReference;
}
