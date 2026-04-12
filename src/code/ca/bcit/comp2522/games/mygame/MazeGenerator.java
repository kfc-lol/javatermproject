package ca.bcit.comp2522.games.mygame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates a perfect maze using the recursive backtracking algorithm.
 * A perfect maze has exactly one path between any two cells — no loops,
 * no isolated regions. The algorithm uses a seeded Random so any maze
 * can be reproduced exactly from its seed value.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class MazeGenerator
{
    private static final int DIRECTION_COUNT = 4;

    /**
     * Private constructor to prevent instantiation.
     * All functionality is exposed through the static generate method.
     */
    private MazeGenerator()
    {
    }

    /**
     * Generates a two-dimensional Cell grid of the given dimensions using
     * recursive backtracking, seeded with the provided value.
     * The same seed always produces the same maze layout.
     *
     * @param cols the number of columns
     * @param rows the number of rows
     * @param seed the random seed for reproducible generation
     * @return a fully carved Cell grid ready to be wrapped in a Maze
     * @throws IllegalArgumentException if cols or rows are less than two
     */
    public static Maze.Cell[][] generate(final int  cols,
                                         final int  rows,
                                         final long seed)
    {
        if(cols < 2 || rows < 2)
        {
            throw new MazeGenerationException(
                "Maze dimensions must be at least 2x2. Got: "
                + cols + "x" + rows);
        }

        final Maze.Cell[][] grid;
        final Random        random;

        grid   = buildEmptyGrid(cols, rows);
        random = new Random(seed);

        carve(grid, cols, rows, random, 0, 0);

        return grid;
    }

    /*
     * Allocates a fresh grid of Cells, all with four walls intact.
     */
    private static Maze.Cell[][] buildEmptyGrid(final int cols,
                                                final int rows)
    {
        final Maze.Cell[][] grid;
        grid = new Maze.Cell[rows][cols];

        for(int row = 0; row < rows; row++)
        {
            for(int col = 0; col < cols; col++)
            {
                grid[row][col] = new Maze.Cell();
            }
        }

        return grid;
    }

    /*
     * Recursively carves passages from (col, row) using depth-first search.
     * Visits each unvisited neighbour in a random order and removes the
     * shared wall between the current cell and the chosen neighbour.
     */
    private static void carve(final Maze.Cell[][] grid,
                              final int           cols,
                              final int           rows,
                              final Random        random,
                              final int           col,
                              final int           row)
    {
        final List<Direction> directions;
        directions = buildShuffledDirections(random);

        for(final Direction direction : directions)
        {
            final int neighbourCol;
            final int neighbourRow;

            neighbourCol = col + colDelta(direction);
            neighbourRow = row + rowDelta(direction);

            if(isUnvisited(grid, cols, rows, neighbourCol, neighbourRow))
            {
                removeWallBetween(grid, col, row, neighbourCol,
                                  neighbourRow, direction);
                carve(grid, cols, rows, random, neighbourCol, neighbourRow);
            }
        }
    }

    /*
     * Builds a list of all four directions in a randomly shuffled order.
     */
    private static List<Direction> buildShuffledDirections(final Random random)
    {
        final List<Direction> directions;
        directions = new ArrayList<>(DIRECTION_COUNT);
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        Collections.shuffle(directions, random);
        return directions;
    }

    /*
     * Returns true if the given position is within bounds and all four
     * walls of that cell are still intact (i.e. not yet visited by carve).
     */
    private static boolean isUnvisited(final Maze.Cell[][] grid,
                                       final int           cols,
                                       final int           rows,
                                       final int           col,
                                       final int           row)
    {
        if(col < 0 || col >= cols || row < 0 || row >= rows)
        {
            return false;
        }

        final Maze.Cell cell;
        cell = grid[row][col];

        return cell.hasWallNorth()
               && cell.hasWallSouth()
               && cell.hasWallEast()
               && cell.hasWallWest();
    }

    /*
     * Removes the shared wall between the current cell and its neighbour
     * in the given direction.
     */
    private static void removeWallBetween(final Maze.Cell[][] grid,
                                          final int           col,
                                          final int           row,
                                          final int           neighbourCol,
                                          final int           neighbourRow,
                                          final Direction     direction)
    {
        final Maze.Cell current;
        final Maze.Cell neighbour;

        current   = grid[row][col];
        neighbour = grid[neighbourRow][neighbourCol];

        switch(direction)
        {
            case NORTH ->
            {
                current.removeWallNorth();
                neighbour.removeWallSouth();
            }
            case SOUTH ->
            {
                current.removeWallSouth();
                neighbour.removeWallNorth();
            }
            case EAST ->
            {
                current.removeWallEast();
                neighbour.removeWallWest();
            }
            case WEST ->
            {
                current.removeWallWest();
                neighbour.removeWallEast();
            }
            default -> { }
        }
    }

    /*
     * Returns the column delta for a given direction.
     */
    private static int colDelta(final Direction direction)
    {
        return switch(direction)
        {
            case EAST  ->  1;
            case WEST  -> -1;
            default    ->  0;
        };
    }

    /*
     * Returns the row delta for a given direction.
     */
    private static int rowDelta(final Direction direction)
    {
        return switch(direction)
        {
            case SOUTH ->  1;
            case NORTH -> -1;
            default    ->  0;
        };
    }
}