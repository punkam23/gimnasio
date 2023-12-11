package com.cenfotec.pokemongym.controller;

import com.cenfotec.pokemongym.DTO.AttackInformation;
import com.cenfotec.pokemongym.DTO.BattleResponse;
import com.cenfotec.pokemongym.model.PlayerInformation;
import com.cenfotec.pokemongym.service.PokemonGymService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gimnasio")
public class PokemonGymController {
    @Autowired
    public PokemonGymService pokemonGymService;

    @PostMapping("/iniciar")
    public ResponseEntity<Object> startBattle() {
        return pokemonGymService.startBattle();
    }

    @PostMapping("/atacar")
    public ResponseEntity<Object> attackPokemon(@Valid @RequestBody AttackInformation attackInformation) {
        return pokemonGymService.attackPokemon(attackInformation.getSourcePlayerName(), attackInformation.getTargetPlayerName(), attackInformation.getAttackId());
    }

    @PostMapping("/unirse")
    public ResponseEntity<Object> joinBattle(@Valid @RequestBody PlayerInformation playerInformation) {
        return pokemonGymService.joinBattle(playerInformation);
    }

    @GetMapping("/info")
    public ResponseEntity<BattleResponse> getBattleInfo() {
        BattleResponse battleResponse = pokemonGymService.getBattleInfo();
        return ResponseEntity.ok(battleResponse);
    }
}
