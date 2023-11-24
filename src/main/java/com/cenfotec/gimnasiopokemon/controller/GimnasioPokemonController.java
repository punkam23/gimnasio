package com.cenfotec.gimnasiopokemon.controller;

import com.cenfotec.gimnasiopokemon.service.GimnasioPokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GimnasioPokemonController {
    @Autowired
    public GimnasioPokemonService gimnasioPokemonService;

    @PostMapping("/api/gimnasioPokemon/atacar")
    public void atacarPokemon() {
        gimnasioPokemonService.atacarPokemon(1, 100);
    }

    @PostMapping("/api/gimnasioPokemon/agregarPokemon/{name}")
    public void agregarPokemon(@PathVariable String name) {
        gimnasioPokemonService.agregarPokemon(name);
    }
}
