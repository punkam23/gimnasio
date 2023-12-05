package com.cenfotec.pokemongym.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "battles")
@Data
public class BattleDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;
}
