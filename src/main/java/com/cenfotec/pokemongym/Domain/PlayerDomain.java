package com.cenfotec.pokemongym.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "players")
@Setter
@Getter
public class PlayerDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String battleReference;
    private String state;

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerDomain playerDomain = (PlayerDomain) o;
        return id == playerDomain.id && Objects.equals(name, playerDomain.name) && Objects.equals(battleReference, playerDomain.battleReference);
    }
}
