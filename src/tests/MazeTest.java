import ca.bcit.comp2522.games.mygame.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Maze class.
 * Verifies cell access, bounds checking, move validation, wall revelation,
 * player visit marking, and the seed stored on construction.
 *
 * @author Your Name
 * @version 1.0
 */
public class MazeTest
{
    private static final int  COLS          = 5;
    private static final int  ROWS          = 5;
    private static final long SEED          = 42L;
    private static final int  VALID_COL     = 0;
    private static final int  VALID_ROW     = 0;
    private static final int  OUT_OF_BOUNDS = 99;

    private Maze maze;

    @BeforeEach
    void setUp()
    {
        maze = MazeFactory.createFromSeed(SEED);
    }

    @Test
    void testGetCell_validPosition_notNull()
    {
        assertNotNull(maze.getCell(VALID_COL, VALID_ROW));
    }

    @Test
    void testGetCell_outOfBounds_throwsInvalidMoveException()
    {
        assertThrows(InvalidMoveException.class,
                     () -> maze.getCell(OUT_OF_BOUNDS, OUT_OF_BOUNDS));
    }

    @Test
    void testIsInBounds_validPosition_returnsTrue()
    {
        assertTrue(maze.isInBounds(VALID_COL, VALID_ROW));
    }

    @Test
    void testIsInBounds_outOfBounds_returnsFalse()
    {
        assertFalse(maze.isInBounds(OUT_OF_BOUNDS, OUT_OF_BOUNDS));
    }

    @Test
    void testIsInBounds_negativeCol_returnsFalse()
    {
        assertFalse(maze.isInBounds(-1, VALID_ROW));
    }

    @Test
    void testGetStart_isOrigin()
    {
        final Point start;
        start = maze.getStart();
        assertEquals(0, start.getCol());
        assertEquals(0, start.getRow());
    }

    @Test
    void testGetExit_isBottomRight()
    {
        final Point exit;
        exit = maze.getExit();
        assertEquals(maze.getCols() - 1, exit.getCol());
        assertEquals(maze.getRows() - 1, exit.getRow());
    }

    @Test
    void testGetSeed_returnsSeedUsedToCreate()
    {
        assertEquals(SEED, maze.getSeed());
    }

    @Test
    void testMarkVisitedByPlayer_cellBecomesVisited()
    {
        maze.markVisitedByPlayer(VALID_COL, VALID_ROW);
        assertTrue(maze.getCell(VALID_COL, VALID_ROW).isVisitedByPlayer());
    }

    @Test
    void testMarkVisitedByPlayer_outOfBounds_throwsInvalidMoveException()
    {
        assertThrows(InvalidMoveException.class,
                     () -> maze.markVisitedByPlayer(OUT_OF_BOUNDS, OUT_OF_BOUNDS));
    }

    @Test
    void testRevealWall_north_cellShowsRevealedNorth()
    {
        maze.revealWall(VALID_COL, VALID_ROW, Direction.NORTH);
        assertTrue(maze.getCell(VALID_COL, VALID_ROW).isRevealedNorth());
    }

    @Test
    void testRevealWall_south_cellShowsRevealedSouth()
    {
        maze.revealWall(VALID_COL, VALID_ROW, Direction.SOUTH);
        assertTrue(maze.getCell(VALID_COL, VALID_ROW).isRevealedSouth());
    }

    @Test
    void testRevealWall_east_cellShowsRevealedEast()
    {
        maze.revealWall(VALID_COL, VALID_ROW, Direction.EAST);
        assertTrue(maze.getCell(VALID_COL, VALID_ROW).isRevealedEast());
    }

    @Test
    void testRevealWall_west_cellShowsRevealedWest()
    {
        maze.revealWall(VALID_COL, VALID_ROW, Direction.WEST);
        assertTrue(maze.getCell(VALID_COL, VALID_ROW).isRevealedWest());
    }

    @Test
    void testIterator_coversAllCells()
    {
        int count;
        count = 0;

        for (final Maze.Cell cell : maze)
        {
            assertNotNull(cell);
            count++;
        }

        assertEquals(maze.getCols() * maze.getRows(), count);
    }

    @Test
    void testGetCols_returnsCorrectCount()
    {
        assertEquals(10, maze.getCols());
    }

    @Test
    void testGetRows_returnsCorrectCount()
    {
        assertEquals(10, maze.getRows());
    }
}