package com.cenfotec.pokemongym.service;

import com.cenfotec.pokemongym.DTO.BattleResponse;
import com.cenfotec.pokemongym.DTO.ResponseDTO;
import com.cenfotec.pokemongym.Domain.*;
import com.cenfotec.pokemongym.model.Attack;
import com.cenfotec.pokemongym.model.PlayerInformation;
import com.cenfotec.pokemongym.model.Pokemon;
import com.cenfotec.pokemongym.model.PokemonType;
import com.cenfotec.pokemongym.repository.BattleRepository;
import com.cenfotec.pokemongym.repository.PlayerRepository;
import com.cenfotec.pokemongym.repository.PokemonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class PokemonGymService {
    private final PokemonRepository pokemonRepository;
    private final BattleRepository battleRepository;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PokemonGymService(PokemonRepository pokemonRepository, BattleRepository battleRepository, PlayerRepository playerRepository) {
        this.pokemonRepository = pokemonRepository;
        this.battleRepository = battleRepository;
        this.playerRepository = playerRepository;
    }

    public ResponseEntity<Object> joinBattle(PlayerInformation playerInformation) {
        String message = Strings.EMPTY;
        boolean success = false;
        List<BattleDomain> battles = this.battleRepository.findAll();
        Optional<BattleDomain> lobbyBattle = battles.stream().filter(battleDomain -> checkBattleState(battleDomain, BattleStateEnum.LOBBY)).findFirst();
        Optional<BattleDomain> currentBattle = battles.stream().filter(battleDomain -> checkBattleState(battleDomain, BattleStateEnum.EN_BATALLA)).findFirst();
        // Check if OR should be negative
        if (battles.isEmpty() || battles.stream().noneMatch(battleDomain ->
                List.of(BattleStateEnum.EN_BATALLA.name(), BattleStateEnum.LOBBY.name())
                        .contains(battleDomain.getState()))) {
            BattleDomain addedBattle = addNewBattle();
            addPlayer(playerInformation, addedBattle);
            message = "The battle and player were added.";
            success = true;
        } else if (lobbyBattle.isPresent()) {
            PlayerDomain playerInBattle =
                    this.playerRepository.findByNameAndBattleReference(playerInformation.getPlayerName(),
                            lobbyBattle.get().getId().toString());
            if (ObjectUtils.isEmpty(playerInBattle)) {
                addPlayer(playerInformation, lobbyBattle.get());
                message = "The player was added to the battle.";
                success = true;
            } else {
                message = "The player is already in the battle.";
            }
        } else if (currentBattle.isPresent()) {
            message = "The battle is in progress.";
        }
        if (currentBattle.isPresent()) {
            message = "The battle is in progress.";
        }

        return createResponse(message, success);
    }

    public ResponseEntity<Object> attackPokemon(String sourcePlayerName, String targetPlayerName, int attackId) {
        String message;
        boolean success = false;
        // Search a current battle
        Optional<BattleDomain> currentBattle = getCurrentBattle();

        if (currentBattle.isPresent() && checkBattleState(currentBattle.get(), BattleStateEnum.EN_BATALLA)) {
            // If there is a current battle, and it is in battle, then search the attacking player
            BattleDomain currentBattleInstance = currentBattle.get();
            PlayerDomain attackingPlayer = this.playerRepository.findByNameAndBattleReference(sourcePlayerName,
                    currentBattleInstance.getId().toString());

            if (checkPlayerState(attackingPlayer, PlayerStateEnum.ATACANDO)) {
                // If the attacking player is valid, then search the attacked player
                PlayerDomain attackedPlayer =
                        this.playerRepository.findByNameAndBattleReference(targetPlayerName,
                                currentBattleInstance.getId().toString());

                if (!ObjectUtils.isEmpty(attackedPlayer) && checkPlayerState(attackedPlayer, PlayerStateEnum.EN_BATALLA)) {
                    // If the attacker player is valid, then search the pokémon by player reference
                    Optional<PokemonDomain> pokemonPlayer = this.pokemonRepository.findByPlayerReference(attackedPlayer.getId().toString());
                    Optional<PokemonDomain> attackingPokemonPlayer = this.pokemonRepository.findByPlayerReference(attackingPlayer.getId().toString());

                    if (pokemonPlayer.isPresent() && attackingPokemonPlayer.isPresent()) {
                        PokemonDomain modifiedPokemon = pokemonPlayer.get();
                        try {
                            List<Attack> attacks = objectMapper.readValue(attackingPokemonPlayer.get().getAttackList(), new TypeReference<>() {});
                            int attackIndex = attackId - 1;
                            if (attackIndex >= 0 && attackIndex < attacks.size()) {
                                // Calculate and save the new pokémon life
                                double newAttack = calculateAttack(modifiedPokemon.getType(), attacks.get(attackIndex));
                                double newLife = modifiedPokemon.getLife() - newAttack;
                                newLife = newLife <= 0 ? 0 : newLife;
                                modifiedPokemon.setLife(newLife);
                                this.pokemonRepository.save(modifiedPokemon);
                                // Change the players state
                                attackingPlayer.setState(PlayerStateEnum.EN_BATALLA.name());
                                this.playerRepository.save(attackingPlayer);
                                if (modifiedPokemon.getLife() <= 0) {
                                    attackedPlayer.setState(PlayerStateEnum.DERROTADO.name());
                                    this.playerRepository.save(attackedPlayer);
                                }
                                // Finally, set the new player turn
                                boolean isNextPlayerAvailable = setNextPlayer(attackingPlayer);
                                if(isNextPlayerAvailable){
                                    message = modifiedPokemon.getName() + " has received a " + newAttack + " power attack.";
                                } else {
                                    message = attackingPlayer.getName() + " has win the Battle, Congratulations. Ending the Battle.";
                                }

                                success = true;
                            } else {
                                message = "Pokemon attack could not be found.";
                            }
                        } catch (JsonProcessingException e) {
                            message = "Error finding the pokemon attack";
                        }
                    }
                    else {
                        message = "Target Pokemon could not be found.";
                    }
                } else {
                    message = "Pokemon Attack could not be sent.";
                }
            } else {
                message = "It is not " + sourcePlayerName + " turn.";
            }
        } else {
            message = "The Pokemon Gym is not available.";
        }

        return createResponse(message, success);
    }

    public boolean setNextPlayer(PlayerDomain attackingPlayer) {
        // Search a current battle
        Optional<BattleDomain> currentBattle = getCurrentBattle();
        if (currentBattle.isPresent()) {
            // If there is a current battle, search the waiting players and look for the current player index
            BattleDomain currentBattleInstance = currentBattle.get();
            List<PlayerDomain> playerDomainList = this.playerRepository
                    .findAllByBattleReference(currentBattleInstance.getId().toString());
            playerDomainList.sort(Comparator.comparingLong(PlayerDomain::getId));
            playerDomainList = playerDomainList.stream().filter(playerDomain -> checkPlayerState(playerDomain, PlayerStateEnum.EN_BATALLA)).toList();
            int attackingPlayerIndex = playerDomainList.indexOf(attackingPlayer);
            int nextPlayerIndex = attackingPlayerIndex + 1;
            // Check if the next the player is the first player again
            if (nextPlayerIndex > playerDomainList.size() - 1) {
                nextPlayerIndex = 0;
            }
            PlayerDomain nextPlayer = playerDomainList.get(nextPlayerIndex);
            // Check if the battles is over
            if (nextPlayer.getName().equals(attackingPlayer.getName())) {
                nextPlayer.setState(PlayerStateEnum.GANADOR.name());
                currentBattleInstance.setState(BattleStateEnum.TERMINADA.name());
                this.battleRepository.save(currentBattleInstance);
                return false;
            } else {
                nextPlayer.setState(PlayerStateEnum.ATACANDO.name());
            }
            // Save the next player in the database
            this.playerRepository.save(nextPlayer);
            return true;
        }
        return false;
    }

    public BattleResponse getBattleInfo() {
        BattleResponse battleResponse = new BattleResponse();
        Optional<BattleDomain> currentBattle = getCurrentBattle();
        if (currentBattle.isEmpty()) {
            List<BattleDomain> battles = this.battleRepository.findAll();
            List<String> states = List.of(BattleStateEnum.TERMINADA.name());
            battles.sort(Comparator.comparingLong(BattleDomain::getId));
            Collections.reverse(battles);
            currentBattle = battles.stream().filter(battleDomain -> states.contains(battleDomain.getState())).findFirst();
        }
        if (currentBattle.isPresent()) {
            BattleDomain currentBattleInstance = currentBattle.get();
            battleResponse.setId(currentBattleInstance.getId());
            battleResponse.setState(currentBattleInstance.getState());
            List<PlayerInformation> playerInformationList = new ArrayList<>();
            List<PlayerDomain> playersInBattle =
                    this.playerRepository.findAllByBattleReference(currentBattleInstance.getId().toString());
            playersInBattle.forEach(playerDomain -> {
                Optional<PokemonDomain> pokemonPlayer = this.pokemonRepository.findByPlayerReference(playerDomain.getId().toString());
                PlayerInformation playerInformation = new PlayerInformation();
                playerInformation.setPlayerName(playerDomain.getName());
                playerInformation.setState(playerDomain.getState());
                if (pokemonPlayer.isPresent()) {
                    Pokemon pokemonDTO = new Pokemon();
                    pokemonDTO.setName(pokemonPlayer.get().getName());
                    pokemonDTO.setLife(pokemonPlayer.get().getLife());
                    pokemonDTO.setType(PokemonType.valueOf(pokemonPlayer.get().getType()));
                    List<Attack> attackList;
                    try {
                        attackList = objectMapper.readValue(pokemonPlayer.get().getAttackList(), new TypeReference<>() {
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    pokemonDTO.setAttacks(attackList);
                    playerInformation.setPokemon(pokemonDTO);
                }
                playerInformationList.add(playerInformation);
            });

            battleResponse.setPlayerInformationList(playerInformationList);
        } else {
            throw new RuntimeException("There is not a current Battle.");
        }
        return battleResponse;
    }

    public void addPlayer(PlayerInformation playerInformation, BattleDomain battleDomain) {
        // Add new player with pokémon reference
        // Check if pokémon was added
        PlayerDomain newPlayer = new PlayerDomain();
        newPlayer.setName(playerInformation.getPlayerName());
        newPlayer.setBattleReference(battleDomain.getId().toString());
        newPlayer.setState(PlayerStateEnum.EN_BATALLA.name());
        PlayerDomain createdPlayer = this.playerRepository.save(newPlayer);

        // Add pokemon
        PokemonDomain newPokemon = new PokemonDomain();
        newPokemon.setName(playerInformation.getPokemon().getName());
        newPokemon.setLife(playerInformation.getPokemon().getLife());
        newPokemon.setType(playerInformation.getPokemon().getType().name());
        newPokemon.setPlayerReference(createdPlayer.getId().toString());
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(playerInformation.getPokemon().getAttacks());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        newPokemon.setAttackList(jsonString);
        this.pokemonRepository.save(newPokemon);
    }

    public ResponseEntity<Object> startBattle() {
        Optional<BattleDomain> currentBattle = getCurrentBattle();
        boolean success = false;
        String message;
        if (currentBattle.isPresent() && checkBattleState(currentBattle.get(), BattleStateEnum.LOBBY)) {
            BattleDomain currentBattleInstance = currentBattle.get();
            currentBattleInstance.setState(BattleStateEnum.EN_BATALLA.name());
            this.battleRepository.save(currentBattleInstance);
            List<PlayerDomain> playerDomainList = this.playerRepository
                    .findAllByBattleReference(currentBattleInstance.getId().toString());
            playerDomainList.sort(Comparator.comparingLong(PlayerDomain::getId));
            playerDomainList = playerDomainList.stream().filter(playerDomain -> checkPlayerState(playerDomain, PlayerStateEnum.EN_BATALLA)).toList();
            PlayerDomain firstPlayer = playerDomainList.get(0);
            firstPlayer.setState(PlayerStateEnum.ATACANDO.name());
            this.playerRepository.save(firstPlayer);
            message = "The Battle has been initialized, to Battle!.";
            success = true;
        } else {
            message = "Already in Battle";
        }

        return createResponse(message, success);
    }

    public BattleDomain addNewBattle() {
        // Add Battle with player reference
        BattleDomain newBattle = new BattleDomain();
        newBattle.setState(BattleStateEnum.LOBBY.name());
        return this.battleRepository.save(newBattle);
    }

    public Optional<BattleDomain> getCurrentBattle() {
        List<BattleDomain> battles = this.battleRepository.findAll();
        List<String> states = List.of(BattleStateEnum.EN_BATALLA.name(), BattleStateEnum.LOBBY.name());
        return battles.stream().filter(battleDomain -> states.contains(battleDomain.getState())).findFirst();
    }

    public boolean checkBattleState(BattleDomain battle, BattleStateEnum state) {
        return BattleStateEnum.valueOf(battle.getState()).equals(state);
    }

    public boolean checkPlayerState(PlayerDomain player, PlayerStateEnum state) {
        return PlayerStateEnum.valueOf(player.getState()).equals(state);
    }

    public double calculateAttack(String pokemonType, Attack attack) {
        double product = 1.0;
        switch (attack.getType()) {
            case fuego:
                if (pokemonType.equals(PokemonType.agua.name())) {
                    product = 0.75;
                } else if (pokemonType.equals(PokemonType.planta.name())) {
                    product = 1.5;
                }
                break;
            case agua:
                if (pokemonType.equals(PokemonType.planta.name())) {
                    product = 0.75;
                } else if (pokemonType.equals(PokemonType.fuego.name())) {
                    product = 1.5;
                }
                break;
            case planta:
                if (pokemonType.equals(PokemonType.fuego.name())) {
                    product = 0.75;
                } else if (pokemonType.equals(PokemonType.agua.name())) {
                    product = 1.5;
                }
                break;
        }

        return attack.getPower() * product;
    }

    public ResponseEntity<Object> createResponse(String message, boolean success) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setSuccess(success);
        responseDTO.setMessage(message);
        return success ? ResponseEntity.ok(responseDTO) : ResponseEntity.badRequest().body(responseDTO);
    }
}
