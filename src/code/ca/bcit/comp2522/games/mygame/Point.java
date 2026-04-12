package ca.bcit.comp2522.games.mygame;

/**
 * Represents a two-dimensional grid coordinate within the maze.
 * Stores a column and row index. Used by the player, BFS solver,
 * and maze model to identify cell positions without coupling to JavaFX.
 * Immutable by design — all fields are final.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class Point
{
    private final int col;
    private final int row;

    /**
     * Constructs a Point at the given column and row.
     *
     * @param col the column index (zero-based)
     * @param row the row index (zero-based)
     */
    public Point(final int col,
                 final int row)
    {
        this.col = col;
        this.row = row;
    }

    /**
     * Returns the column index of this point.
     *
     * @return the column index
     */
    public int getCol()
    {
        return col;
    }

    /**
     * Returns the row index of this point.
     *
     * @return the row index
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Returns a new Point shifted one step in the given direction.
     * Does not perform bounds checking — callers must validate the result.
     *
     * @param direction the direction to step toward
     * @return a new Point one cell in that direction
     */
    public Point step(final Direction direction)
    {
        final Point result;

        switch(direction)
        {
            case NORTH -> result = new Point(col, row - 1);
            case SOUTH -> result = new Point(col, row + 1);
            case EAST  -> result = new Point(col + 1, row);
            case WEST  -> result = new Point(col - 1, row);
            default    -> result = new Point(col, row);
        }

        return result;
    }

    /**
     * Computes the Manhattan distance from this point to the given target.
     * Used by progress bars to show how far each racer is from the exit.
     *
     * @param target the destination point
     * @return the Manhattan distance in cells
     */
    public int manhattanDistanceTo(final Point target)
    {
        return Math.abs(col - target.getCol())
               + Math.abs(row - target.getRow());
    }

    /**
     * Returns true if this Point has the same column and row as the other.
     *
     * @param other the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(final Object other)
    {
        if(this == other)
        {
            return true;
        }

        if(!(other instanceof Point))
        {
            return false;
        }

        final Point otherPoint;
        otherPoint = (Point) other;

        return col == otherPoint.col && row == otherPoint.row;
    }

    /**
     * Returns a hash code consistent with equals, based on col and row.
     * Required so Point works correctly as a key in HashMap and HashSet.
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return 31 * col + row;
    }

    /**
     * Returns a human-readable string representation of this point.
     *
     * @return string in the format (col, row)
     */
    @Override
    public String toString()
    {
        return "(" + col + ", " + row + ")";
    }
}