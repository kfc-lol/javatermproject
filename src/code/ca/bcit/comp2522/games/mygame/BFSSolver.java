package ca.bcit.comp2522.games.mygame;

import java.util.*;

/**
 * Concrete maze solver using greedy best-first search.
 * Extends MazeSolver and overrides all abstract methods to implement
 * the search strategy. The frontier is sorted by Manhattan distance to
 * the exit before each step using a lambda comparator, so the algorithm
 * always processes the cell closest to the exit first.
 *
 * Each call to step() fully processes one frontier cell: all four directions
 * are attempted, wall hits are recorded privately without touching the Maze
 * model, and valid unvisited neighbours are enqueued. displayPosition is
 * updated to the processed cell every tick so the UI distance label moves
 * smoothly with no freezing.
 *
 * MazeUI holds a MazeSolver reference pointing to this instance,
 * demonstrating substitution — the UI works with any MazeSolver subclass
 * without modification.
 *
 * @author Your Name
 * @version 1.0
 */
public final class BFSSolver extends MazeSolver
{
    private final List<Point>       frontier;
    private final Set<Point>        visited;
    private final Map<Point, Point> parentMap;
    private final Set<String>       wallsHit;
    private final Maze maze;

    private Point   displayPosition;
    private boolean reachedExit;

    /**
     * Constructs a BFSSolver for the given maze.
     * Seeds the frontier with the start position and initialises all
     * collections to their interface types.
     * Calls the superclass constructor to set the shared maze field.
     *
     * @param mazeParam the maze to solve
     */
    public BFSSolver(final Maze mazeParam)
    {
        super(mazeParam);

        final Point startPoint;
        this.maze = mazeParam;
        frontier        = new ArrayList<>();
        visited         = new HashSet<>();
        parentMap       = new HashMap<>();
        wallsHit        = new HashSet<>();
        reachedExit     = false;

        startPoint      = mazeParam.getStart();
        displayPosition = startPoint;

        visited.add(startPoint);
        frontier.add(startPoint);
    }

    public List<Point> getFrontier()
    {
        return frontier;
    }

    public Map<Point, Point> getParentMap()
    {
        return parentMap;
    }

    public Set<String> getWallsHit()
    {
        return wallsHit;
    }

    @Override
    public Maze getMaze()
    {
        return maze;
    }

    public boolean isReachedExit()
    {
        return reachedExit;
    }

    /**
     * Executes one solver step — fully processing the frontier cell closest
     * to the exit.
     *
     * The frontier is sorted by Manhattan distance to the exit so the closest
     * cell is always processed first. All four directions of that cell are
     * attempted: walls are recorded privately, valid unvisited neighbours are
     * enqueued. displayPosition is updated to the processed cell every tick
     * so the UI distance label always moves smoothly with no freezing.
     * Has no effect if the exit has already been reached or the frontier
     * is empty.
     */
    @Override
    public void step()
    {
        if(reachedExit || frontier.isEmpty())
        {
            return;
        }

        sortFrontierByDistance();

        final Point current;
        current         = frontier.removeFirst();
        displayPosition = current;

        if(current.equals(maze.getExit()))
        {
            reachedExit = true;
            return;
        }

        for(final Direction direction : Direction.values())
        {
            processDirection(current, direction);
        }
    }

    /**
     * Returns the display position — the cell the solver processed last tick.
     * Updated every call to step() so the Manhattan distance shown in the UI
     * always changes smoothly with no freezing.
     *
     * @return the current display position
     */
    @Override
    public Point getDisplayPosition()
    {
        return displayPosition;
    }

    /**
     * Returns true if the solver has reached the maze exit.
     *
     * @return true if exit reached
     */
    @Override
    public boolean hasReachedExit()
    {
        return reachedExit;
    }

    /**
     * Returns true if the frontier is exhausted without reaching the exit.
     * In a perfect maze this should never occur since every cell is reachable.
     *
     * @return true if no solution exists
     */
    @Override
    public boolean isUnsolvable()
    {
        return frontier.isEmpty() && !reachedExit;
    }

    /**
     * Reconstructs the solution path from start to exit using the parent map.
     * Returns an ordered list of Points from start to exit inclusive.
     * Returns an empty list if the exit has not yet been reached.
     *
     * @return the solution path, or an empty list if not yet solved
     */
    @Override
    public List<Point> reconstructPath()
    {
        final List<Point> path;
        path = new ArrayList<>();

        if(!reachedExit)
        {
            return path;
        }

        Point current;
        current = maze.getExit();

        while(current != null)
        {
            path.addFirst(current);
            current = parentMap.get(current);
        }

        return path;
    }

    public Set<Point> getVisited()
    {
        return Collections.unmodifiableSet(visited);

    }

    /*
     * Attempts one direction from the given cell.
     * Blocked directions are recorded privately without touching the Maze model.
     * Valid unvisited neighbours are added to the frontier and visited set.
     */
    private void processDirection(final Point     from,
                                  final Direction direction)
    {
        final boolean movePossible;
        movePossible = maze.isValidMove(from.getCol(), from.getRow(), direction);

        if(!movePossible)
        {
            recordWallHit(from, direction);
            return;
        }

        final Point neighbour;
        neighbour = from.step(direction);

        if(!maze.isInBounds(neighbour.getCol(), neighbour.getRow()))
        {
            return;
        }

        if(visited.contains(neighbour))
        {
            return;
        }

        visited.add(neighbour);
        parentMap.put(neighbour, from);
        frontier.add(neighbour);
    }

    /*
     * Sorts the frontier list in ascending order of Manhattan distance to the
     * exit using a lambda comparator, so the closest cell is always processed
     * first. Satisfies the lambda expressions OOP requirement.
     */
    private void sortFrontierByDistance()
    {
        final Point exitPoint;
        exitPoint = maze.getExit();

        frontier.sort(Comparator.comparingInt((Point a) -> a.manhattanDistanceTo(exitPoint)));
    }

    /*
     * Records a wall hit privately using a direction-keyed string.
     * Never writes to the Maze model — preserves the player's fog of war.
     */
    private void recordWallHit(final Point     from,
                               final Direction direction)
    {
        final String wallKey;
        wallKey = from.getCol() + "," + from.getRow() + "," + direction.name();
        wallsHit.add(wallKey);
    }
}