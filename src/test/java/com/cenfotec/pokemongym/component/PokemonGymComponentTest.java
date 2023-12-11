package com.cenfotec.pokemongym.component;

import com.cenfotec.pokemongym.DTO.BattleResponse;
import com.cenfotec.pokemongym.DTO.ResponseDTO;
import com.cenfotec.pokemongym.Domain.BattleStateEnum;
import com.cenfotec.pokemongym.model.Attack;
import com.cenfotec.pokemongym.model.PlayerInformation;
import com.cenfotec.pokemongym.model.Pokemon;
import com.cenfotec.pokemongym.model.PokemonType;
import com.cenfotec.pokemongym.repository.BattleRepository;
import com.cenfotec.pokemongym.repository.PlayerRepository;
import com.cenfotec.pokemongym.repository.PokemonRepository;
import com.cenfotec.pokemongym.service.PokemonGymService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PokemonGymComponentTest {


    @Autowired
    private ObjectMapper objectMapper;
    private final PokemonRepository pokemonRepository;

    private final BattleRepository battleRepository;

    private final PlayerRepository playerRepository;
    private final PokemonGymService pokemonGymService;
    private static final String BASE_URL = "http://localhost:8081"; // Replace with your actual base URL

    private HttpClient httpClient = HttpClient.newHttpClient();

    @Autowired
    public PokemonGymComponentTest(PokemonRepository pokemonRepository, BattleRepository battleRepository,
                                   PlayerRepository playerRepository,
                                   PokemonGymService pokemonGymService) {
        this.pokemonRepository = pokemonRepository;
        this.battleRepository = battleRepository;
        this.playerRepository = playerRepository;
        this.pokemonGymService = pokemonGymService;
    }

    @AfterEach
    public void cleanupDatabase() {
        // logic to clean up the database
        pokemonRepository.deleteAll();
        battleRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void shouldUnirseBatallaTest() throws Exception {
        //given
        PlayerInformation playerInformation = createNewPlayer();

        ResponseDTO responseDTOFromServer = new ResponseDTO();
        responseDTOFromServer.setSuccess(true);
        responseDTOFromServer.setMessage("The battle and player were added.");

        //when

        HttpRequest.BodyPublisher requestBodyPublisher = HttpRequest.BodyPublishers.ofString(
                objectMapper.writeValueAsString(playerInformation));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/gimnasio/unirse"))
                .header("Content-Type", "application/json")
                .POST(requestBodyPublisher)
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // then

        assertNotNull(response.body());
        assertEquals(200, response.statusCode());
        assertEquals(objectMapper.writeValueAsString(responseDTOFromServer), response.body());
    }

    @Test
    public void shouldFailWhenUnirseBatallaWithBadRequestTest() throws Exception {
        //given
        PlayerInformation playerInformation = createNewPlayer();
        playerInformation.setPlayerName(null);

        ResponseDTO responseDTOFromServer = new ResponseDTO();
        responseDTOFromServer.setSuccess(true);
        responseDTOFromServer.setMessage("The battle and player were added.");

        //when

        HttpRequest.BodyPublisher requestBodyPublisher = HttpRequest.BodyPublishers.ofString(
                objectMapper.writeValueAsString(playerInformation));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/gimnasio/unirse"))
                .header("Content-Type", "application/json")
                .POST(requestBodyPublisher)
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // then

        assertNotNull(response.body());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void shouldGetBatallaInfoTest() throws Exception {
        //given
        PlayerInformation playerInformation = createNewPlayer();
        playerInformation.setState(BattleStateEnum.EN_BATALLA.name());
        BattleResponse battleResponseFromServer = new BattleResponse();
        battleResponseFromServer.setState(BattleStateEnum.LOBBY.name());
        battleResponseFromServer.setPlayerInformationList(List.of(playerInformation));

        this.pokemonGymService.joinBattle(createNewPlayer());
        //when
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/gimnasio/info"))
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        // then
        assertEquals(200, response.statusCode());
        BattleResponse currentResponse = objectMapper.readValue(response.body(), new TypeReference<>() {});
        assertEquals(battleResponseFromServer.getState(), currentResponse.getState());

    }

    @Test
    public void shouldFailWhenBatallaNotInitiateTest() throws Exception {
        //given
        ResponseDTO responseDTOFail = new ResponseDTO();
        responseDTOFail.setSuccess(false);
        responseDTOFail.setMessage("There is not a current Battle.");

        //when
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/gimnasio/info"))
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(400, response.statusCode());
        assertEquals(objectMapper.writeValueAsString(responseDTOFail), response.body());
    }

    private static PlayerInformation createNewPlayer() {
        Attack attack1 = new Attack();
        attack1.setPower(100);
        attack1.setType(PokemonType.normal);

        Attack attack2 = new Attack();
        attack1.setPower(75);
        attack1.setType(PokemonType.normal);

        Attack attack3 = new Attack();
        attack1.setPower(50);
        attack1.setType(PokemonType.normal);
        Pokemon pokemon = new Pokemon();
        pokemon.setName("pokemonName");
        pokemon.setType(PokemonType.normal);
        pokemon.setLife(1000);
        pokemon.setAttacks(List.of(attack1, attack2, attack3));

        PlayerInformation playerInformation = new PlayerInformation();
        playerInformation.setPlayerName("playerName");
        playerInformation.setPokemon(pokemon);
        return playerInformation;
    }
}
