package com.cenfotec.gimnasiopokemon.controller;

import com.cenfotec.gimnasiopokemon.model.PlayerInformation;
import com.cenfotec.gimnasiopokemon.service.GimnasioPokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gimnasioPokemon")
public class GimnasioPokemonController {
    @Autowired
    public GimnasioPokemonService gimnasioPokemonService;

    @PostMapping("/atacar")
    public void atacarPokemon() {
        gimnasioPokemonService.atacarPokemon(1, 100);
    }

    @PostMapping("/unirse-batalla")
    public ResponseEntity<String> unirseBatalla(@RequestBody PlayerInformation playerInformation) {
        String respuesta = gimnasioPokemonService.unirseBatalla(playerInformation);
        return ResponseEntity.ok(respuesta);

    }

    @PostMapping("/agregarPokemon/{name}")
    public void agregarPokemon(@PathVariable String name) {
        gimnasioPokemonService.agregarPokemon(name);
    }
}
