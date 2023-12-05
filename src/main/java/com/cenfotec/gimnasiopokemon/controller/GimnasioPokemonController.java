package com.cenfotec.gimnasiopokemon.controller;

import com.cenfotec.gimnasiopokemon.DTO.AttackInformation;
import com.cenfotec.gimnasiopokemon.DTO.BatallaResponse;
import com.cenfotec.gimnasiopokemon.Domain.BatallaDomain;
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

    @PostMapping("/iniciar-batalla")
    public ResponseEntity<String> iniciarBatalla() {
        String response = gimnasioPokemonService.iniciarBatalla();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/atacar")
    public ResponseEntity<String> atacarPokemon(@RequestBody AttackInformation attackInformation) {
        String response = gimnasioPokemonService.atacarPokemon(attackInformation.getSourcePlayerName(), attackInformation.getTargetPlayerName(), attackInformation.getAttackId());
        return ResponseEntity.ok(response);
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

    @GetMapping("/obtener-informacion-batalla")
    public ResponseEntity<BatallaResponse> obtenerInformacionBatalla() {
        BatallaResponse batallaResponse = gimnasioPokemonService.obtenerBatalla();
        return ResponseEntity.ok(batallaResponse);
    }
}
