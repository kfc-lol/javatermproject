import ca.bcit.comp2522.games.mygame.Maze;
import ca.bcit.comp2522.games.mygame.MazeFactory;
import ca.bcit.comp2522.games.mygame.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MazeFactory class.
 * Verifies that both factory methods return valid Maze instances with
 * correct dimensions and that seed-based generation is reproducible.
 *
 * @author Your Name
 * @version 1.0
 */
public class MazeFactoryTest
{
    private static final long SEED          = 77777L;
    private static final int  EXPECTED_COLS = 10;
    private static final int  EXPECTED_ROWS = 10;

    @Test
    void testCreateRandom_returnsNotNull()
    {
        final Maze maze;
        maze = MazeFactory.createRandom();
        assertNotNull(maze);
    }

    @Test
    void testCreateRandom_hasCorrectCols()
    {
        final Maze maze;
        maze = MazeFactory.createRandom();
        assertEquals(EXPECTED_COLS, maze.getCols());
    }

    @Test
    void testCreateRandom_hasCorrectRows()
    {
        final Maze maze;
        maze = MazeFactory.createRandom();
        assertEquals(EXPECTED_ROWS, maze.getRows());
    }

    @Test
    void testCreateFromSeed_returnsNotNull()
    {
        final Maze maze;
        maze = MazeFactory.createFromSeed(SEED);
        assertNotNull(maze);
    }

    @Test
    void testCreateFromSeed_hasCorrectCols()
    {
        final Maze maze;
        maze = MazeFactory.createFromSeed(SEED);
        assertEquals(EXPECTED_COLS, maze.getCols());
    }

    @Test
    void testCreateFromSeed_hasCorrectRows()
    {
        final Maze maze;
        maze = MazeFactory.createFromSeed(SEED);
        assertEquals(EXPECTED_ROWS, maze.getRows());
    }

    @Test
    void testCreateFromSeed_storesSeedCorrectly()
    {
        final Maze maze;
        maze = MazeFactory.createFromSeed(SEED);
        assertEquals(SEED, maze.getSeed());
    }

    @Test
    void testCreateFromSeed_reproducible_sameWallNorth()
    {
        final Maze mazeOne;
        final Maze mazeTwo;

        mazeOne = MazeFactory.createFromSeed(SEED);
        mazeTwo = MazeFactory.createFromSeed(SEED);

        for (int row = 0; row < EXPECTED_ROWS; row++)
        {
            for (int col = 0; col < EXPECTED_COLS; col++)
            {
                assertEquals(mazeOne.getCell(col, row).hasWallNorth(),
                             mazeTwo.getCell(col, row).hasWallNorth());
            }
        }
    }

    @Test
    void testCreateFromSeed_reproducible_sameWallEast()
    {
        final Maze mazeOne;
        final Maze mazeTwo;

        mazeOne = MazeFactory.createFromSeed(SEED);
        mazeTwo = MazeFactory.createFromSeed(SEED);

        for (int row = 0; row < EXPECTED_ROWS; row++)
        {
            for (int col = 0; col < EXPECTED_COLS; col++)
            {
                assertEquals(mazeOne.getCell(col, row).hasWallEast(),
                             mazeTwo.getCell(col, row).hasWallEast());
            }
        }
    }

    @Test
    void testCreateRandom_startIsOrigin()
    {
        final Maze  maze;
        final Point start;

        maze  = MazeFactory.createRandom();
        start = maze.getStart();

        assertEquals(0, start.getCol());
        assertEquals(0, start.getRow());
    }

    @Test
    void testCreateRandom_exitIsBottomRight()
    {
        final Maze  maze;
        final Point exit;

        maze = MazeFactory.createRandom();
        exit = maze.getExit();

        assertEquals(EXPECTED_COLS - 1, exit.getCol());
        assertEquals(EXPECTED_ROWS - 1, exit.getRow());
    }

    @Test
    void testCreateRandom_twoCallsProduceDifferentSeeds()
    {
        final Maze mazeOne;
        final Maze mazeTwo;

        mazeOne = MazeFactory.createRandom();
        mazeTwo = MazeFactory.createRandom();

        assertNotEquals(mazeOne.getSeed(), mazeTwo.getSeed());
    }
}