package com.cenfotec.gimnasiopokemon.service;

import com.cenfotec.gimnasiopokemon.DTO.BatallaResponse;
import com.cenfotec.gimnasiopokemon.Domain.BatallaDomain;
import com.cenfotec.gimnasiopokemon.Domain.EstadoBatallaEnum;
import com.cenfotec.gimnasiopokemon.Domain.JugadorDomain;
import com.cenfotec.gimnasiopokemon.Domain.PokemonDomain;
import com.cenfotec.gimnasiopokemon.model.Attack;
import com.cenfotec.gimnasiopokemon.model.PlayerInformation;
import com.cenfotec.gimnasiopokemon.model.Pokemon;
import com.cenfotec.gimnasiopokemon.model.PokemonType;
import com.cenfotec.gimnasiopokemon.repository.BatallaRepository;
import com.cenfotec.gimnasiopokemon.repository.JugadorRepository;
import com.cenfotec.gimnasiopokemon.repository.PokemonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GimnasioPokemonService {

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private BatallaRepository batallaRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void agregarPokemon(String name) {
        //comentario
        PokemonDomain newPokemon = new PokemonDomain();
        newPokemon.setName(name);
        newPokemon.setVida(400);
        this.pokemonRepository.save(newPokemon);

    }

    public void atacarPokemon(int pokemonId, int cantidadAtaque) {
        // Logic to retrieve YourEntity from the database or another data source
        // For simplicity, a mock object is returned here
    }

    public String unirseBatalla(PlayerInformation playerInformation) {
        String respuesta = Strings.EMPTY;
        List<BatallaDomain> batallas = this.batallaRepository.findAll();
        Optional<BatallaDomain> batallaEnLoby = batallas.stream().filter(batallaDomain -> batallaDomain.getEstadoBatalla().equals(EstadoBatallaEnum.LOBY.name())).findFirst();
        Optional<BatallaDomain> batallaEnCurso = batallas.stream().filter(batallaDomain -> batallaDomain.getEstadoBatalla().equals(EstadoBatallaEnum.EN_BATALLA.name())).findFirst();
        //revisar si se debe negar el OR
        if (batallas.isEmpty() || batallas.stream().anyMatch(batallaDomain ->
                List.of(EstadoBatallaEnum.EN_BATALLA, EstadoBatallaEnum.LOBY)
                        .contains(batallaDomain.getEstadoBatalla()))) {
            BatallaDomain batallaAgregada = agregarBatallaNueva(playerInformation);
            agregarJugador(playerInformation, batallaAgregada);
            respuesta = "La batalla y el jugador fueron agregados";
        } else if (batallaEnLoby.isPresent()) {
            JugadorDomain jugadorEnBatalla =
                    this.jugadorRepository.findByNameAndBatallaReference(playerInformation.getPlayerName(),
                            batallaEnLoby.get().getId().toString());
            if (ObjectUtils.isEmpty(jugadorEnBatalla)) {
                agregarJugador(playerInformation, batallaEnLoby.get());
                respuesta = "El jugador fue agregado a la batalla";
            } else {
                respuesta = "El Jugador ya esta en la batalla";
            }
        } else if (batallaEnCurso.isPresent()) {
            respuesta = "batalla en curso";
        }
//        if (batallaEnLoby.isPresent()) {
//            JugadorDomain jugadorEnBatalla =
//                    this.jugadorRepository.findByNameAndBatallaReference(playerInformation.getPlayerName(),
//                            batallaEnLoby.get().getId().toString());
//            if (ObjectUtils.isEmpty(jugadorEnBatalla)) {
//                return "El jugador fue agregado a la batalla";
//            }
//        }
        if(batallaEnCurso.isPresent()){
            respuesta = "batalla en curso";
        }
        return respuesta;
    }

    private BatallaDomain agregarBatallaNueva(PlayerInformation playerInformation) {
        // agragar batalla con referencia a jugador
        BatallaDomain nuevaBatalla = new BatallaDomain();
        nuevaBatalla.setEstadoBatalla(EstadoBatallaEnum.LOBY.name());
        return this.batallaRepository.save(nuevaBatalla);
    }

    private JugadorDomain agregarJugador(PlayerInformation playerInformation, BatallaDomain batallaDomain) {

        // agregar jugador con referencia a pokemon
        // verificar si jugador ya fue agregado a la batalla

        JugadorDomain nuevoJugador = new JugadorDomain();
        nuevoJugador.setName(playerInformation.getPlayerName());
        nuevoJugador.setBatallaReference(batallaDomain.getId().toString());
        JugadorDomain jugadorCreado = this.jugadorRepository.save(nuevoJugador);

        // agregar pokemon
        PokemonDomain newPokemon = new PokemonDomain();
        newPokemon.setName(playerInformation.getPokemon().getName());
        newPokemon.setVida(playerInformation.getPokemon().getVida());
        newPokemon.setJugadorReference(jugadorCreado.getId().toString());
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(playerInformation.getPokemon().getAttacks());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        newPokemon.setAttackList(jsonString);
        this.pokemonRepository.save(newPokemon);

        return jugadorCreado;
    }

    public BatallaResponse obtenerBatalla() {
        BatallaResponse batallaResponse = new BatallaResponse();
        List<BatallaDomain> batallas = this.batallaRepository.findAll();
        Optional<BatallaDomain> batallaEnCurso = batallas.stream().filter(batallaDomain -> List.of(EstadoBatallaEnum.EN_BATALLA.name(),
                EstadoBatallaEnum.LOBY.name()).contains(batallaDomain.getEstadoBatalla())).findFirst();
        if(batallaEnCurso.isPresent()){
            batallaResponse.setId(batallaEnCurso.get().getId());
            batallaResponse.setEstadoBatalla(batallaEnCurso.get().getEstadoBatalla());
            List<PlayerInformation> playerInformationList = new ArrayList<>();
            List<JugadorDomain> jugadoresEnBatalla =
                    this.jugadorRepository.findAllByBatallaReference(batallaEnCurso.get().getId().toString());
            jugadoresEnBatalla.stream().forEach(jugadorDomain -> {
                Optional<PokemonDomain> pokemonPlayer = this.pokemonRepository.findByJugadorReference(jugadorDomain.getId().toString());
                PlayerInformation playerInformation = new PlayerInformation();
                playerInformation.setPlayerName(jugadorDomain.getName());
                if(pokemonPlayer.isPresent()){
                    Pokemon pokemonDTO = new Pokemon();
                    pokemonDTO.setName(pokemonPlayer.get().getName());
                    pokemonDTO.setVida(pokemonPlayer.get().getVida());
                    pokemonDTO.setType(PokemonType.valueOf(pokemonPlayer.get().getType()));
                    List<Attack> attackList = List.of();
                    try {
                        attackList = objectMapper.readValue(pokemonPlayer.get().getAttackList(), new TypeReference<List<Attack>>() {});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    pokemonDTO.setAttacks(attackList);
                    playerInformation.setPokemon(pokemonDTO);
                }
                playerInformationList.add(playerInformation);
            });

            batallaResponse.setPlayerInformationList(playerInformationList);
        }
        return batallaResponse;
    }
}
