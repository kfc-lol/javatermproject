package ca.bcit.comp2522.games.mygame;

import java.util.Random;

/**
 * Static factory for creating Maze instances.
 * Implements the Factory design pattern — callers request a Maze without
 * knowing how it is constructed or generated.
 * Provides two overloaded factory methods: one for random mazes and one
 * for reproducible mazes from a caller-supplied seed.
 *
 * @author Your Name
 * @version 1.0
 */
public final class MazeFactory
{
    private static final int DEFAULT_COLS = 10;
    private static final int DEFAULT_ROWS = 10;

    /**
     * Private constructor to prevent instantiation.
     * All methods are static; this class is never instantiated.
     */
    private MazeFactory()
    {
    }

    /**
     * Creates a Maze with a randomly generated seed.
     * Each call produces a different maze layout.
     * Uses default dimensions of 10 columns by 10 rows.
     *
     * @return a new randomly generated Maze
     */
    public static Maze createRandom()
    {
        final long seed;
        seed = new Random().nextLong();
        return buildMaze(seed);
    }

    /**
     * Creates a Maze from the given seed value.
     * The same seed always produces the same maze layout,
     * allowing players to replay or share specific mazes.
     * Uses default dimensions of 10 columns by 10 rows.
     *
     * @param seed the seed for reproducible maze generation
     * @return a new Maze generated from the provided seed
     */
    public static Maze createFromSeed(final long seed)
    {
        return buildMaze(seed);
    }

    /*
     * Delegates grid generation to MazeGenerator and wraps the result in a Maze.
     */
    private static Maze buildMaze(final long seed)
    {
        final Maze.Cell[][] grid;
        grid = MazeGenerator.generate(MazeFactory.DEFAULT_COLS, MazeFactory.DEFAULT_ROWS, seed);
        return new Maze(MazeFactory.DEFAULT_COLS, MazeFactory.DEFAULT_ROWS, grid, seed);
    }
}