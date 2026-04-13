package ca.bcit.comp2522.games.mygame;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents the maze as a two-dimensional grid of Cells.
 * Provides wall checking, wall revealing, and move validation
 * for both the player and the BFS solver.
 * The maze grid is traversable via a custom MazeIterator.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class Maze implements Iterable<Maze.Cell>
{
    private static final int START_COL       = 0;
    private static final int START_ROW       = 0;
    private static final int EXIT_COL_OFFSET = 1;
    private static final int EXIT_ROW_OFFSET = 1;

    private final int      cols;
    private final int      rows;
    private final Cell[][] grid;
    private final Point    start;
    private final Point    exit;
    private final long     seed;

    /**
     * Constructs a Maze with the given dimensions, cell grid, and seed.
     * Called exclusively by MazeFactory.
     *
     * @param cols the number of columns
     * @param rows the number of rows
     * @param grid the fully generated cell grid
     * @param seed the seed used to generate this maze
     */
    public Maze(final int cols,
                final int rows,
                final Cell[][] grid,
                final long seed)
    {
        this.cols  = cols;
        this.rows  = rows;
        this.grid  = grid;
        this.seed  = seed;
        this.start = new Point(START_COL, START_ROW);
        this.exit  = new Point(cols - EXIT_COL_OFFSET, rows - EXIT_ROW_OFFSET);
    }

    /**
     * Returns the seed used to generate this maze.
     * Used by MazeUI to save the current maze for replay.
     *
     * @return the generation seed
     */
    public long getSeed()
    {
        return seed;
    }

    /**
     * Returns the Cell at the given column and row.
     *
     * @param col the column index
     * @param row the row index
     * @return the Cell at that position
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public Cell getCell(final int col,
                        final int row)
    {
        validatePosition(col, row);
        return grid[ row ][ col ];
    }

    /**
     * Returns true if moving from the given position in the given direction
     * is not blocked by a wall.
     *
     * @param col       the current column
     * @param row       the current row
     * @param direction the direction of movement
     * @return true if the move is valid (no wall in that direction)
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public boolean isValidMove(final int col,
                               final int row,
                               final Direction direction)
    {
        validatePosition(col, row);

        final Cell    currentCell;
        final boolean wallBlocking;

        currentCell = grid[ row ][ col ];

        switch (direction)
        {
            case NORTH -> wallBlocking = currentCell.hasWallNorth();
            case SOUTH -> wallBlocking = currentCell.hasWallSouth();
            case EAST -> wallBlocking = currentCell.hasWallEast();
            case WEST -> wallBlocking = currentCell.hasWallWest();
            default -> wallBlocking = true;
        }

        return !wallBlocking;
    }

    /**
     * Reveals the wall in the given direction from the given position,
     * marking it as discovered by the player's failed move attempt.
     *
     * @param col       the column of the cell
     * @param row       the row of the cell
     * @param direction the direction of the blocked wall
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public void revealWall(final int col,
                           final int row,
                           final Direction direction)
    {
        validatePosition(col, row);

        final Cell cell;
        cell = grid[ row ][ col ];

        switch (direction)
        {
            case NORTH -> cell.revealWallNorth();
            case SOUTH -> cell.revealWallSouth();
            case EAST -> cell.revealWallEast();
            case WEST -> cell.revealWallWest();
            default ->
            {
            }
        }
    }

    /**
     * Marks the cell at the given position as visited by the player.
     *
     * @param col the column index
     * @param row the row index
     * @throws IllegalArgumentException if the position is out of bounds
     */
    public void markVisitedByPlayer(final int col,
                                    final int row)
    {
        validatePosition(col, row);
        grid[ row ][ col ].markVisitedByPlayer();
    }

    /**
     * Returns true if the given position is within the maze bounds.
     *
     * @param col the column index
     * @param row the row index
     * @return true if in bounds
     */
    public boolean isInBounds(final int col,
                              final int row)
    {
        return col >= START_COL && col < cols && row >= START_ROW && row < rows;
    }

    /**
     * Returns the starting point of the maze.
     *
     * @return the start Point
     */
    public Point getStart()
    {
        return start;
    }

    /**
     * Returns the exit point of the maze.
     *
     * @return the exit Point
     */
    public Point getExit()
    {
        return exit;
    }

    /**
     * Returns the number of columns in the maze.
     *
     * @return column count
     */
    public int getCols()
    {
        return cols;
    }

    /**
     * Returns the number of rows in the maze.
     *
     * @return row count
     */
    public int getRows()
    {
        return rows;
    }

    /**
     * Returns an iterator that traverses all cells row by row, left to right.
     * Satisfies the Iterable contract and supports the Iterator OOP requirement.
     *
     * @return a MazeIterator over all cells
     */
    @Override
    public Iterator<Cell> iterator()
    {
        return new MazeIterator();
    }

    /**
     * Validates that the given column and row are within bounds.
     * Throws IllegalArgumentException if not.
     *
     * @param col column to validate
     * @param row row to validate
     */
    private void validatePosition(final int col,
                                  final int row)
    {
        if (!isInBounds(col, row))
        {
            throw new InvalidMoveException(
                "Position out of bounds: col=" + col + " row=" + row);
        }
    }

    // ===================================================================
    //  Static nested Cell class
    // ===================================================================

    /**
     * Represents a single cell within the maze grid.
     * Tracks wall existence on all four sides, which walls have been
     * revealed to the player, and whether the player has visited this cell.
     * Static nested so it can be used without a Maze instance reference.
     *
     * @author Kian Castro
     * @version 1.0
     */
    public static final class Cell
    {
        private boolean wallNorth;
        private boolean wallSouth;
        private boolean wallEast;
        private boolean wallWest;

        private boolean revealedNorth;
        private boolean revealedSouth;
        private boolean revealedEast;
        private boolean revealedWest;

        private boolean visitedByPlayer;

        {
            // Instance initializer: all walls present, nothing revealed
            wallNorth       = true;
            wallSouth       = true;
            wallEast        = true;
            wallWest        = true;
            revealedNorth   = false;
            revealedSouth   = false;
            revealedEast    = false;
            revealedWest    = false;
            visitedByPlayer = false;
        }

        /**
         * Constructs a Cell with all four walls intact and nothing revealed.
         * The instance initializer handles all field setup.
         */
        public Cell()
        {
        }

        // --- Wall removal (called by MazeGenerator during carving) ---

        /**
         * Removes the north wall of this cell.
         */
        public void removeWallNorth()
        {
            wallNorth = false;
        }

        /**
         * Removes the south wall of this cell.
         */
        public void removeWallSouth()
        {
            wallSouth = false;
        }

        /**
         * Removes the east wall of this cell.
         */
        public void removeWallEast()
        {
            wallEast = false;
        }

        /**
         * Removes the west wall of this cell.
         */
        public void removeWallWest()
        {
            wallWest = false;
        }

        // --- Wall reveal (called when player bumps a wall) ---

        /**
         * Marks the north wall as revealed to the player.
         */
        public void revealWallNorth()
        {
            revealedNorth = true;
        }

        /**
         * Marks the south wall as revealed to the player.
         */
        public void revealWallSouth()
        {
            revealedSouth = true;
        }

        /**
         * Marks the east wall as revealed to the player.
         */
        public void revealWallEast()
        {
            revealedEast = true;
        }

        /**
         * Marks the west wall as revealed to the player.
         */
        public void revealWallWest()
        {
            revealedWest = true;
        }

        /**
         * Marks this cell as visited by the player.
         */
        public void markVisitedByPlayer()
        {
            visitedByPlayer = true;
        }

        // --- Wall existence queries ---

        /**
         * Returns true if the north wall exists.
         *
         * @return true if north wall is present
         */
        public boolean hasWallNorth()
        {
            return wallNorth;
        }

        /**
         * Returns true if the south wall exists.
         *
         * @return true if south wall is present
         */
        public boolean hasWallSouth()
        {
            return wallSouth;
        }

        /**
         * Returns true if the east wall exists.
         *
         * @return true if east wall is present
         */
        public boolean hasWallEast()
        {
            return wallEast;
        }

        /**
         * Returns true if the west wall exists.
         *
         * @return true if west wall is present
         */
        public boolean hasWallWest()
        {
            return wallWest;
        }

        // --- Reveal state queries ---

        /**
         * Returns true if the north wall has been revealed to the player.
         *
         * @return true if north wall is revealed
         */
        public boolean isRevealedNorth()
        {
            return revealedNorth;
        }

        /**
         * Returns true if the south wall has been revealed to the player.
         *
         * @return true if south wall is revealed
         */
        public boolean isRevealedSouth()
        {
            return revealedSouth;
        }

        /**
         * Returns true if the east wall has been revealed to the player.
         *
         * @return true if east wall is revealed
         */
        public boolean isRevealedEast()
        {
            return revealedEast;
        }

        /**
         * Returns true if the west wall has been revealed to the player.
         *
         * @return true if west wall is revealed
         */
        public boolean isRevealedWest()
        {
            return revealedWest;
        }

        /**
         * Returns true if the player has stepped on this cell.
         *
         * @return true if visited by player
         */
        public boolean isVisitedByPlayer()
        {
            return visitedByPlayer;
        }
    }

    // ===================================================================
    //  MazeIterator nested class
    // ===================================================================

    /**
     * Iterates over all cells in the maze row by row, left to right.
     *
     * @author Kian Castro
     * @version 1.0
     */
    private final class MazeIterator implements Iterator<Cell>
    {
        private int currentCol;
        private int currentRow;

        {
            currentCol = START_COL;
            currentRow = START_ROW;
        }

        private MazeIterator()
        {
        }

        /**
         * Checks if there are any more cells
         *
         * @return true if not all cells have been visited
         */
        @Override
        public boolean hasNext()
        {
            return currentRow < rows;
        }

        /**
         * Returns the next Cell in row-major order.
         *
         * @return the next Cell
         * @throws NoSuchElementException if no more cells remain
         */
        @Override
        public Cell next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException("No more cells in maze.");
            }

            final Cell cell;
            cell = grid[ currentRow ][ currentCol ];

            currentCol++;

            if (currentCol >= cols)
            {
                currentCol = START_COL;
                currentRow++;
            }

            return cell;
        }
    }
}