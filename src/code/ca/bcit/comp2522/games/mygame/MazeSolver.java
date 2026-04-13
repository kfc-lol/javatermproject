package ca.bcit.comp2522.games.mygame;

import java.util.List;

/**
 * Abstract base class for all maze-solving algorithms.
 * Defines the contract that every solver must fulfil: stepping through
 * the maze one tick at a time, reporting its current display position,
 * and reconstructing the solution path once the exit is reached.
 * <p>
 * Concrete subclasses implement the search strategy — for example,
 * BFSSolver uses greedy best-first search. MazeUI holds a MazeSolver
 * reference, allowing any concrete solver to be substituted transparently
 * at runtime without changing the UI code (Liskov Substitution Principle).
 * <p>
 * The private maze field can be found all subclasses so they can query
 * wall data and bounds.
 *
 * @author Kian Castro
 * @version 1.0
 */
public abstract class MazeSolver
{
    /**
     * The maze this solver is operating on.
     * Accessible to subclasses for wall checking and bounds validation.
     */
    private final Maze maze;

    /**
     * Constructs a MazeSolver bound to the given maze.
     *
     * @param mazeParam the maze to solve
     */
    public MazeSolver(final Maze mazeParam)
    {
        maze = mazeParam;
    }

    public Maze getMaze()
    {
        return maze;
    }

    /**
     * Executes one step of the search algorithm.
     * Each call corresponds to exactly one game tick — matching the cost
     * of one player input. Implementations decide how much work constitutes
     * one step based on their algorithm's mechanics.
     */
    public abstract void step();

    /**
     * Returns the position used to compute Manhattan distance in the UI.
     * Must update every tick so the distance label never freezes.
     *
     * @return the current display position
     */
    public abstract Point getDisplayPosition();

    /**
     * Returns true if the solver has reached the maze exit.
     *
     * @return true if exit reached
     */
    public abstract boolean hasReachedExit();

    /**
     * Returns true if the solver has exhausted all reachable cells without
     * finding the exit. In a perfect maze this should never occur.
     *
     * @return true if no solution exists
     */
    public abstract boolean isUnsolvable();

    /**
     * Reconstructs and returns the solution path from start to exit.
     * Returns an empty list if the exit has not yet been reached.
     *
     * @return ordered list of Points from start to exit, or empty list
     */
    public abstract List<Point> reconstructPath();
}