import ca.bcit.comp2522.games.mygame.Direction;
import ca.bcit.comp2522.games.mygame.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Point class.
 * Verifies coordinate construction, stepping in all four directions,
 * Manhattan distance calculation, and equals/hashCode contracts.
 *
 * @author Your Name
 * @version 1.0
 */
public class PointTest
{
    private static final int COL_ZERO                 = 0;
    private static final int ROW_ZERO                 = 0;
    private static final int COL_THREE                = 3;
    private static final int ROW_FOUR                 = 4;
    private static final int EXPECTED_MANHATTAN_SEVEN = 7;
    private static final int EXPECTED_STEP_COL_NORTH  = 3;
    private static final int EXPECTED_STEP_ROW_NORTH  = 3;
    private static final int EXPECTED_STEP_COL_SOUTH  = 3;
    private static final int EXPECTED_STEP_ROW_SOUTH  = 5;
    private static final int EXPECTED_STEP_COL_EAST   = 4;
    private static final int EXPECTED_STEP_ROW_EAST   = 4;
    private static final int EXPECTED_STEP_COL_WEST   = 2;
    private static final int EXPECTED_STEP_ROW_WEST   = 4;

    private Point origin;
    private Point pointA;

    @BeforeEach
    void setUp()
    {
        origin = new Point(COL_ZERO, ROW_ZERO);
        pointA = new Point(COL_THREE, ROW_FOUR);
    }

    @Test
    void testConstructor_notNull()
    {
        assertNotNull(origin);
        assertNotNull(pointA);
    }

    @Test
    void testGetCol_returnsCorrectCol()
    {
        assertEquals(COL_THREE, pointA.getCol());
    }

    @Test
    void testGetRow_returnsCorrectRow()
    {
        assertEquals(ROW_FOUR, pointA.getRow());
    }

    @Test
    void testManhattanDistanceTo_correctDistance()
    {
        assertEquals(EXPECTED_MANHATTAN_SEVEN,
                     origin.manhattanDistanceTo(pointA));
    }

    @Test
    void testManhattanDistanceTo_samePoint_isZero()
    {
        assertEquals(0, origin.manhattanDistanceTo(origin));
    }

    @Test
    void testManhattanDistanceTo_isSymmetric()
    {
        assertEquals(origin.manhattanDistanceTo(pointA),
                     pointA.manhattanDistanceTo(origin));
    }

    @Test
    void testStep_north_decreasesRow()
    {
        final Point result;
        result = pointA.step(Direction.NORTH);
        assertEquals(EXPECTED_STEP_COL_NORTH, result.getCol());
        assertEquals(EXPECTED_STEP_ROW_NORTH, result.getRow());
    }

    @Test
    void testStep_south_increasesRow()
    {
        final Point result;
        result = pointA.step(Direction.SOUTH);
        assertEquals(EXPECTED_STEP_COL_SOUTH, result.getCol());
        assertEquals(EXPECTED_STEP_ROW_SOUTH, result.getRow());
    }

    @Test
    void testStep_east_increasesCol()
    {
        final Point result;
        result = pointA.step(Direction.EAST);
        assertEquals(EXPECTED_STEP_COL_EAST, result.getCol());
        assertEquals(EXPECTED_STEP_ROW_EAST, result.getRow());
    }

    @Test
    void testStep_west_decreasesCol()
    {
        final Point result;
        result = pointA.step(Direction.WEST);
        assertEquals(EXPECTED_STEP_COL_WEST, result.getCol());
        assertEquals(EXPECTED_STEP_ROW_WEST, result.getRow());
    }

    @Test
    void testEquals_sameCoordinates_returnsTrue()
    {
        final Point duplicate;
        duplicate = new Point(COL_THREE, ROW_FOUR);
        assertEquals(pointA, duplicate);
    }

    @Test
    void testEquals_differentCoordinates_returnsFalse()
    {
        assertNotEquals(origin, pointA);
    }

    @Test
    void testEquals_null_returnsFalse()
    {
        assertNotEquals(null, pointA);
    }

    @Test
    void testHashCode_equalPoints_sameHashCode()
    {
        final Point duplicate;
        duplicate = new Point(COL_THREE, ROW_FOUR);
        assertEquals(pointA.hashCode(), duplicate.hashCode());
    }

    @Test
    void testHashCode_differentPoints_differentHashCode()
    {
        assertNotEquals(origin.hashCode(), pointA.hashCode());
    }

    @Test
    void testToString_containsCoordinates()
    {
        final String result;
        result = pointA.toString();
        org.junit.jupiter.api.Assertions.assertTrue(
            result.contains(String.valueOf(COL_THREE)));
        org.junit.jupiter.api.Assertions.assertTrue(
            result.contains(String.valueOf(ROW_FOUR)));
    }
}