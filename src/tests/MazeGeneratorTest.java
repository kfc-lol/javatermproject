import ca.bcit.comp2522.games.mygame.Maze;
import ca.bcit.comp2522.games.mygame.MazeGenerationException;
import ca.bcit.comp2522.games.mygame.MazeGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MazeGenerator class.
 * Verifies valid generation, invalid dimension handling,
 * seed reproducibility, and basic structural properties of
 * the generated grid.
 *
 * @author Kian Castro
 * @version 1.0
 */
public class MazeGeneratorTest
{
    private static final int  VALID_COLS   = 10;
    private static final int  VALID_ROWS   = 10;
    private static final int  INVALID_COLS = 1;
    private static final int  INVALID_ROWS = 1;
    private static final long SEED_A       = 12345L;
    private static final long SEED_B       = 99999L;

    @Test
    void testGenerate_validDimensions_returnsGrid()
    {
        final Maze.Cell[][] grid;
        grid = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);
        assertNotNull(grid);
    }

    @Test
    void testGenerate_gridHasCorrectRowCount()
    {
        final Maze.Cell[][] grid;
        grid = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);
        assertEquals(VALID_ROWS, grid.length);
    }

    @Test
    void testGenerate_gridHasCorrectColCount()
    {
        final Maze.Cell[][] grid;
        grid = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);
        assertEquals(VALID_COLS, grid[ 0 ].length);
    }

    @Test
    void testGenerate_invalidDimensions_throwsMazeGenerationException()
    {
        assertThrows(MazeGenerationException.class,
                     () -> MazeGenerator.generate(INVALID_COLS,
                                                  INVALID_ROWS,
                                                  SEED_A));
    }

    @Test
    void testGenerate_colsLessThanTwo_throwsMazeGenerationException()
    {
        assertThrows(MazeGenerationException.class,
                     () -> MazeGenerator.generate(1, VALID_ROWS, SEED_A));
    }

    @Test
    void testGenerate_rowsLessThanTwo_throwsMazeGenerationException()
    {
        assertThrows(MazeGenerationException.class,
                     () -> MazeGenerator.generate(VALID_COLS, 1, SEED_A));
    }

    @Test
    void testGenerate_sameSeed_producesSameWallConfiguration()
    {
        final Maze.Cell[][] gridOne;
        final Maze.Cell[][] gridTwo;

        gridOne = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);
        gridTwo = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);

        for (int row = 0; row < VALID_ROWS; row++)
        {
            for (int col = 0; col < VALID_COLS; col++)
            {
                assertEquals(gridOne[ row ][ col ].hasWallNorth(), gridTwo[ row ][ col ].hasWallNorth());
                assertEquals(gridOne[ row ][ col ].hasWallSouth(), gridTwo[ row ][ col ].hasWallSouth());
                assertEquals(gridOne[ row ][ col ].hasWallEast(), gridTwo[ row ][ col ].hasWallEast());
                assertEquals(gridOne[ row ][ col ].hasWallWest(), gridTwo[ row ][ col ].hasWallWest());
            }
        }
    }

    @Test
    void testGenerate_differentSeeds_produceDifferentObjects()
    {
        final Maze.Cell[][] gridA;
        final Maze.Cell[][] gridB;

        gridA = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);
        gridB = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_B);

        assertNotSame(gridA, gridB);
    }

    @Test
    void testGenerate_allCellsNotNull()
    {
        final Maze.Cell[][] grid;
        grid = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);

        for (final Maze.Cell[] row : grid)
        {
            for (final Maze.Cell cell : row)
            {
                assertNotNull(cell);
            }
        }
    }

    @Test
    void testGenerate_startCellExists()
    {
        final Maze.Cell[][] grid;
        grid = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);
        assertNotNull(grid[ 0 ][ 0 ]);
    }

    @Test
    void testGenerate_exitCellExists()
    {
        final Maze.Cell[][] grid;
        grid = MazeGenerator.generate(VALID_COLS, VALID_ROWS, SEED_A);
        assertNotNull(grid[ VALID_ROWS - 1 ][ VALID_COLS - 1 ]);
    }
}