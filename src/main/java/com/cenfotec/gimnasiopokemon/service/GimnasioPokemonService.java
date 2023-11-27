package com.cenfotec.gimnasiopokemon.service;

import com.cenfotec.gimnasiopokemon.model.Pokemon;
import com.cenfotec.gimnasiopokemon.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GimnasioPokemonService {

    @Autowired
    private PokemonRepository pokemonRepository;
    public void agregarPokemon(String name) {
        //comentario
        Pokemon newPokemon = new Pokemon();
        newPokemon.setName(name);
        newPokemon.setVida(200);
        this.pokemonRepository.save(newPokemon);

    }
    public void atacarPokemon(int pokemonId, int cantidadAtaque) {
        // Logic to retrieve YourEntity from the database or another data source
        // For simplicity, a mock object is returned here
    }
}
