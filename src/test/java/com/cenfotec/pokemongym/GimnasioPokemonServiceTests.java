package com.cenfotec.pokemongym;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GimnasioPokemonServiceTests {

    private GimnasioPokemonApplicationTests aplicationTest=new GimnasioPokemonApplicationTests();
    @Test
    public void testAttackPokemon() {
        aplicationTest.testAttackPokemon();
    }


    @Test
    public void testJoinBattle() {
        aplicationTest.testJoinBattle();
    }

    @Test
    public void testGetBattleInfo() {
        aplicationTest.testGetBattleInfo();
    }

    @Test
    public void testStartBattle() {
        aplicationTest.testStartBattle();
    }


}
