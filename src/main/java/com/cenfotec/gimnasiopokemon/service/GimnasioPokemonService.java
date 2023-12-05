package com.cenfotec.gimnasiopokemon.service;

import com.cenfotec.gimnasiopokemon.DTO.BatallaResponse;
import com.cenfotec.gimnasiopokemon.Domain.*;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
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

    public String atacarPokemon(String sourcePlayerName, String targetPlayerName, int attackId) {
        String response = Strings.EMPTY;
        Optional<BatallaDomain> batallaEnCurso = obtenerBatallaEnCurso();
        if(batallaEnCurso.isPresent() &&
                EstadoBatallaEnum.valueOf(batallaEnCurso.get().getEstadoBatalla()).equals(EstadoBatallaEnum.EN_BATALLA)){
            JugadorDomain jugadorAtacante = this.jugadorRepository.findByNameAndBatallaReference(sourcePlayerName,
                    batallaEnCurso.get().getId().toString());
            if(JugadorEstadoEnum.valueOf(jugadorAtacante.getEstado()).equals(JugadorEstadoEnum.EN_ATAQUE)){
                JugadorDomain jugadorAtacado =
                        this.jugadorRepository.findByNameAndBatallaReference(targetPlayerName,
                                batallaEnCurso.get().getId().toString());
                if(!ObjectUtils.isEmpty(jugadorAtacado) &&
                        JugadorEstadoEnum.valueOf(jugadorAtacado.getEstado()).equals(JugadorEstadoEnum.EN_ESPERA)) {
                    Optional<PokemonDomain> pokemonPlayer = this.pokemonRepository.findByJugadorReference(jugadorAtacado.getId().toString());
                    if (pokemonPlayer.isPresent()) {
                        PokemonDomain pokemonModificado = descontarVidaPokemon(pokemonPlayer.get(), attackId);
                        this.pokemonRepository.save(pokemonModificado);
                        jugadorAtacante.setEstado(JugadorEstadoEnum.EN_ESPERA.name());
                        this.jugadorRepository.save(jugadorAtacante);
                        if (pokemonModificado.getVida() <= 0) {
                            jugadorAtacado.setEstado(JugadorEstadoEnum.TERMINADO.name());
                            this.jugadorRepository.save(jugadorAtacado);
                        }
                        List<JugadorDomain> jugadorDomainList = this.jugadorRepository
                                .findAllByBatallaReference(batallaEnCurso.get().getId().toString());
                        jugadorDomainList.sort(Comparator.comparingLong(JugadorDomain::getId));
                        jugadorDomainList = jugadorDomainList.stream().filter(jugadorDomain -> JugadorEstadoEnum.valueOf(jugadorDomain.getEstado()).equals(JugadorEstadoEnum.EN_ESPERA)).toList();
                        int indexOfJugadorAtacante = jugadorDomainList.indexOf(jugadorAtacante);
                        int indexOfSiguienteJugador = indexOfJugadorAtacante + 1;
                        if(indexOfSiguienteJugador > jugadorDomainList.size() - 1){
                            indexOfSiguienteJugador = 0;
                        }
                        JugadorDomain siguienteJugador = jugadorDomainList.get(indexOfSiguienteJugador);
                        if(siguienteJugador.getName().equals(jugadorAtacante.getName())){
                            siguienteJugador.setEstado(JugadorEstadoEnum.GANADOR.name());
                            batallaEnCurso.get().setEstadoBatalla(EstadoBatallaEnum.TERMINADA.name());
                            this.batallaRepository.save(batallaEnCurso.get());
                        }else{
                            siguienteJugador.setEstado(JugadorEstadoEnum.EN_ATAQUE.name());
                        }
                        this.jugadorRepository.save(siguienteJugador);
                        response = "Pokemon Attack has been sent.";
                    }
                }else {
                    response = "Pokemon Attack could not be sent.";
                }
            } else {
                response = "Pokemon Attack could not be sent.";
            }
        } else {
            response = "Pokemon Attack could not be sent.";
        }
        return response;
    }

    private PokemonDomain descontarVidaPokemon(PokemonDomain pokemonPlayer, int attackId) {
        int vidaActual = pokemonPlayer.getVida();
        List<Attack> attackList = List.of();
        try {
            attackList = objectMapper.readValue(pokemonPlayer.getAttackList(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        int poderAtaque = attackList.get(attackId).getPower();
        pokemonPlayer.setVida(vidaActual - poderAtaque);
        return pokemonPlayer;
    }

    public String unirseBatalla(PlayerInformation playerInformation) {
        String respuesta = Strings.EMPTY;
        List<BatallaDomain> batallas = this.batallaRepository.findAll();
        Optional<BatallaDomain> batallaEnLoby = batallas.stream().filter(batallaDomain -> batallaDomain.getEstadoBatalla().equals(EstadoBatallaEnum.LOBY.name())).findFirst();
        Optional<BatallaDomain> batallaEnCurso = batallas.stream().filter(batallaDomain -> batallaDomain.getEstadoBatalla().equals(EstadoBatallaEnum.EN_BATALLA.name())).findFirst();
        //revisar si se debe negar el OR
        if (batallas.isEmpty() || !batallas.stream().anyMatch(batallaDomain ->
                List.of(EstadoBatallaEnum.EN_BATALLA.name(), EstadoBatallaEnum.LOBY.name())
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
        nuevoJugador.setEstado(JugadorEstadoEnum.EN_ESPERA.name());
        JugadorDomain jugadorCreado = this.jugadorRepository.save(nuevoJugador);

        // agregar pokemon
        PokemonDomain newPokemon = new PokemonDomain();
        newPokemon.setName(playerInformation.getPokemon().getName());
        newPokemon.setVida(playerInformation.getPokemon().getVida());
        newPokemon.setType(playerInformation.getPokemon().getType().name());
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
        Optional<BatallaDomain> batallaEnCurso = obtenerBatallaEnCurso();
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
        } else {
            throw new RuntimeException("Error getting Battle Information");
        }
        return batallaResponse;
    }

    public String iniciarBatalla() {
        Optional<BatallaDomain> batallaEnCurso = obtenerBatallaEnCurso();
        String response = Strings.EMPTY;
        if(batallaEnCurso.isPresent() &&
                EstadoBatallaEnum.valueOf(batallaEnCurso.get().getEstadoBatalla()).equals(EstadoBatallaEnum.LOBY)) {
            batallaEnCurso.get().setEstadoBatalla(EstadoBatallaEnum.EN_BATALLA.name());
            this.batallaRepository.save(batallaEnCurso.get());
            List<JugadorDomain> jugadorDomainList = this.jugadorRepository
                    .findAllByBatallaReference(batallaEnCurso.get().getId().toString());
            jugadorDomainList.sort(Comparator.comparingLong(JugadorDomain::getId));
            jugadorDomainList = jugadorDomainList.stream().filter(jugadorDomain -> JugadorEstadoEnum.valueOf(jugadorDomain.getEstado()).equals(JugadorEstadoEnum.EN_ESPERA)).toList();
            JugadorDomain firstPlayer = jugadorDomainList.get(0);
            firstPlayer.setEstado(JugadorEstadoEnum.EN_ATAQUE.name());
            this.jugadorRepository.save(firstPlayer);
            response = "The Battle has been initialized, to Battle.";
        }else {
            response = "Already in Battle";
        }
        return response;
    }

    private Optional<BatallaDomain> obtenerBatallaEnCurso() {
        List<BatallaDomain> batallas = this.batallaRepository.findAll();
        Optional<BatallaDomain> batallaEnCurso = batallas.stream().filter(batallaDomain -> List.of(EstadoBatallaEnum.EN_BATALLA.name(),
                EstadoBatallaEnum.LOBY.name()).contains(batallaDomain.getEstadoBatalla())).findFirst();
        return batallaEnCurso;
    }


}
