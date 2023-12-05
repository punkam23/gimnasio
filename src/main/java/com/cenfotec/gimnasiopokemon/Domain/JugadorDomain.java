package com.cenfotec.gimnasiopokemon.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "jugadores")
@Data
public class JugadorDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String batallaReference;
    private String estado;
}
