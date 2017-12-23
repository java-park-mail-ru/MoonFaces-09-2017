package ru.mail.park;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.websocket.GameField;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class GameMechTest {

    @Test
    public void testFieldMirror() {
        GameField gameField = new GameField();
        int[][] field = {
                {1, 0, 0, 0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0, 0, 0, 1},
                {0, 0, 1, 0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0, 1, 0, 0},
                {1, 0, 0, 0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0, 1, 0, 0},
        };
        int[][] inverted = {
                {0, 0, 0, 1, 0, 0, 0, 1},
                {0, 0, 1, 0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0, 1, 0, 0},
                {1, 0, 0, 0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0, 0, 0, 1},
                {0, 0, 1, 0, 0, 0, 1, 0},
        };
        gameField.setGameField(field);
        assertArrayEquals(inverted, gameField.getIntArrayInverted());
    }

    @Test
    public void testUserScoresCounter() {
        GameField gameField = new GameField();

        int[][] field = {
            {1, 0, 0, 0, 1, 0, 0, 0},
            {0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 0},
            {0, 0, 0, 1, 0, 0, 0, 1},
            {0, 0, 1, 0, 0, 0, 1, 0},
            {0, 1, 0, 0, 0, 1, 0, 0},
            {1, 0, 0, 0, 1, 0, 0, 0},
            {0, 1, 0, 0, 0, 1, 0, 0},
        };

        gameField.setGameField(field);

        assertEquals(gameField.getPlayer1Scores(), gameField.getPlayer2Scores());
        assertEquals(gameField.getPlayer1Scores(), 8);
    }
}
