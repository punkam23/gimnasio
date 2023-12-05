package com.cenfotec.pokemongym.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "players")
@Data
public class PlayerDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String battleReference;
    private String state;
}
