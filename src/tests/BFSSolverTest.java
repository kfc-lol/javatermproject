import ca.bcit.comp2522.games.mygame.BFSSolver;
import ca.bcit.comp2522.games.mygame.Maze;
import ca.bcit.comp2522.games.mygame.MazeFactory;
import ca.bcit.comp2522.games.mygame.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BFSSolver class.
 * Verifies initial state, step behaviour, exit detection,
 * path reconstruction, and the visited set.
 *
 * @author Kian Castro
 * @version 1.0
 */
public class BFSSolverTest
{
    private static final long SEED         = 54321L;
    private static final int  MAX_STEPS    = 10000;
    private static final int  MIN_PATH_LEN = 2;

    private Maze      maze;
    private BFSSolver solver;

    @BeforeEach
    void setUp()
    {
        maze   = MazeFactory.createFromSeed(SEED);
        solver = new BFSSolver(maze);
    }

    @Test
    void testInitialDisplayPosition_isStart()
    {
        final Point displayPosition;
        displayPosition = solver.getDisplayPosition();
        assertEquals(maze.getStart(), displayPosition);
    }

    @Test
    void testInitialReachedExit_isFalse()
    {
        assertFalse(solver.hasReachedExit());
    }

    @Test
    void testInitialIsUnsolvable_isFalse()
    {
        assertFalse(solver.isUnsolvable());
    }

    @Test
    void testInitialVisited_containsStart()
    {
        final Set<Point> visited;
        visited = solver.getVisited();
        assertTrue(visited.contains(maze.getStart()));
    }

    @Test
    void testStep_displayPositionUpdates()
    {
        final Point beforeStep;
        beforeStep = solver.getDisplayPosition();

        solver.step();

        final Point afterStep;
        afterStep = solver.getDisplayPosition();

        assertNotNull(afterStep);
    }

    @Test
    void testStep_visitedSetGrows()
    {
        final int initialSize;
        initialSize = solver.getVisited().size();

        solver.step();

        assertTrue(solver.getVisited().size() >= initialSize);
    }

    @Test
    void testSolver_reachesExitOnPerfectMaze()
    {
        int steps;
        steps = 0;

        while (!solver.hasReachedExit() && steps < MAX_STEPS)
        {
            solver.step();
            steps++;
        }

        assertTrue(solver.hasReachedExit());
    }

    @Test
    void testSolver_isUnsolvable_falseOnPerfectMaze()
    {
        while (!solver.hasReachedExit())
        {
            solver.step();
        }

        assertFalse(solver.isUnsolvable());
    }

    @Test
    void testReconstructPath_notEmptyAfterSolving()
    {
        while (!solver.hasReachedExit())
        {
            solver.step();
        }

        final List<Point> path;
        path = solver.reconstructPath();

        assertFalse(path.isEmpty());
    }

    @Test
    void testReconstructPath_startsAtStart()
    {
        while (!solver.hasReachedExit())
        {
            solver.step();
        }

        final List<Point> path;
        path = solver.reconstructPath();

        assertEquals(maze.getStart(), path.get(0));
    }

    @Test
    void testReconstructPath_endsAtExit()
    {
        while (!solver.hasReachedExit())
        {
            solver.step();
        }

        final List<Point> path;
        path = solver.reconstructPath();

        assertEquals(maze.getExit(), path.get(path.size() - 1));
    }

    @Test
    void testReconstructPath_minimumLength()
    {
        while (!solver.hasReachedExit())
        {
            solver.step();
        }

        final List<Point> path;
        path = solver.reconstructPath();

        assertTrue(path.size() >= MIN_PATH_LEN);
    }

    @Test
    void testReconstructPath_emptyBeforeSolving()
    {
        final List<Point> path;
        path = solver.reconstructPath();
        assertTrue(path.isEmpty());
    }

    @Test
    void testStep_hasNoEffectAfterExitReached()
    {
        while (!solver.hasReachedExit())
        {
            solver.step();
        }

        final int visitedSizeAfterSolving;
        visitedSizeAfterSolving = solver.getVisited().size();

        solver.step();
        solver.step();

        assertEquals(visitedSizeAfterSolving, solver.getVisited().size());
    }

    @Test
    void testGetVisited_returnsUnmodifiableSet()
    {
        final Set<Point> visited;
        visited = solver.getVisited();

        org.junit.jupiter.api.Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> visited.add(new Point(0, 0)));
    }
}