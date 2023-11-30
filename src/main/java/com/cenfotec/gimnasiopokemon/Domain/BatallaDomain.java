package com.cenfotec.gimnasiopokemon.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "batallas")
@Data
public class BatallaDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String estadoBatalla;
}
