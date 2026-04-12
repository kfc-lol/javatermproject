import ca.bcit.comp2522.games.mygame.MazeScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MazeScore class.
 * Verifies initial state, tick incrementing, finish marking,
 * result summary generation for all outcomes, and reset behaviour.
 *
 * @author Your Name
 * @version 1.0
 */
public class MazeScoreTest
{
    private static final int TICKS_ZERO  = 0;
    private static final int TICKS_ONE   = 1;
    private static final int TICKS_THREE = 3;
    private static final int TICKS_FIVE  = 5;

    private MazeScore score;

    @BeforeEach
    void setUp()
    {
        score = new MazeScore();
    }

    @Test
    void testInitialPlayerTicks_isZero()
    {
        assertEquals(TICKS_ZERO, score.getPlayerTicks());
    }

    @Test
    void testInitialAlgorithmTicks_isZero()
    {
        assertEquals(TICKS_ZERO, score.getAlgorithmTicks());
    }

    @Test
    void testInitialPlayerFinished_isFalse()
    {
        assertFalse(score.hasPlayerFinished());
    }

    @Test
    void testInitialAlgorithmFinished_isFalse()
    {
        assertFalse(score.hasAlgorithmFinished());
    }

    @Test
    void testIncrementPlayerTicks_incrementsByOne()
    {
        score.incrementPlayerTicks();
        assertEquals(TICKS_ONE, score.getPlayerTicks());
    }

    @Test
    void testIncrementAlgorithmTicks_incrementsByOne()
    {
        score.incrementAlgorithmTicks();
        assertEquals(TICKS_ONE, score.getAlgorithmTicks());
    }

    @Test
    void testIncrementPlayerTicks_multipleIncrements()
    {
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        assertEquals(TICKS_THREE, score.getPlayerTicks());
    }

    @Test
    void testIncrementPlayerTicks_noEffectAfterFinished()
    {
        score.markPlayerFinished();
        score.incrementPlayerTicks();
        assertEquals(TICKS_ZERO, score.getPlayerTicks());
    }

    @Test
    void testIncrementAlgorithmTicks_noEffectAfterFinished()
    {
        score.markAlgorithmFinished();
        score.incrementAlgorithmTicks();
        assertEquals(TICKS_ZERO, score.getAlgorithmTicks());
    }

    @Test
    void testMarkPlayerFinished_setsFinishedTrue()
    {
        score.markPlayerFinished();
        assertTrue(score.hasPlayerFinished());
    }

    @Test
    void testMarkAlgorithmFinished_setsFinishedTrue()
    {
        score.markAlgorithmFinished();
        assertTrue(score.hasAlgorithmFinished());
    }

    @Test
    void testGetResultSummary_playerWins_notNull()
    {
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.markPlayerFinished();

        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();

        final String summary;
        summary = score.getResultSummary();

        assertNotNull(summary);
        assertTrue(summary.toLowerCase().contains("you win"));
    }

    @Test
    void testGetResultSummary_bfsWins_notNull()
    {
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.markPlayerFinished();

        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();
        score.markAlgorithmFinished();

        final String summary;
        summary = score.getResultSummary();

        assertNotNull(summary);
        assertTrue(summary.toLowerCase().contains("bfs wins"));
    }

    @Test
    void testGetResultSummary_tie_notNull()
    {
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.incrementPlayerTicks();
        score.markPlayerFinished();

        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();
        score.incrementAlgorithmTicks();
        score.markAlgorithmFinished();

        final String summary;
        summary = score.getResultSummary();

        assertNotNull(summary);
        assertTrue(summary.toLowerCase().contains("tie"));
    }

    @Test
    void testGetResultSummary_inProgress_notNull()
    {
        final String summary;
        summary = score.getResultSummary();
        assertNotNull(summary);
    }

    @Test
    void testReset_playerTicksBackToZero()
    {
        score.incrementPlayerTicks();
        score.reset();
        assertEquals(TICKS_ZERO, score.getPlayerTicks());
    }

    @Test
    void testReset_algorithmTicksBackToZero()
    {
        score.incrementAlgorithmTicks();
        score.reset();
        assertEquals(TICKS_ZERO, score.getAlgorithmTicks());
    }

    @Test
    void testReset_playerFinishedBackToFalse()
    {
        score.markPlayerFinished();
        score.reset();
        assertFalse(score.hasPlayerFinished());
    }

    @Test
    void testReset_algorithmFinishedBackToFalse()
    {
        score.markAlgorithmFinished();
        score.reset();
        assertFalse(score.hasAlgorithmFinished());
    }

    @Test
    void testReset_allowsTickIncrementAfterReset()
    {
        score.markPlayerFinished();
        score.reset();
        score.incrementPlayerTicks();
        assertEquals(TICKS_ONE, score.getPlayerTicks());
    }
}