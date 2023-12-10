package com.cenfotec.pokemongym.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "battles")
@Setter
@Getter
public class BattleDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BattleDomain)) return false;
        final BattleDomain other = (BattleDomain) o;
        if (!other.canEqual((Object) this)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BattleDomain;
    }
}
