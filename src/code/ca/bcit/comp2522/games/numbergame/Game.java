package ca.bcit.comp2522.games.numbergame;

import java.util.Random;

/**
 * Abstract base class for a number placement game.
 * Manages the grid, random numbers, and game state.
 */
public abstract class Game implements Playable
{

    /** Offset used when checking neighboring cells. */
    public static final int OFFSET_INDEX = 1;

    /** Total number of cells in the grid. */
    private static final int GRID_SIZE = 20;

    /** Minimum random number value. */
    private static final int MIN_NUM = 1;

    /** Maximum random number value. */
    private static final int MAX_NUM = 1000;

    /** Lower bound used for random generation. */
    private static final int LOWER_BOUND = 1;

    private final Random random;
    private final int[] grid;
    private final boolean[] filled;
    private final int[] numbers;

    private int currentIndex;
    private boolean gameOver;
    private boolean won;
    private int successfulPlacements;

    /**
     * Constructs a new Game and initializes arrays.
     */
    public Game()
    {
        random  = new Random();
        grid    = new int[GRID_SIZE];
        filled  = new boolean[GRID_SIZE];
        numbers = new int[GRID_SIZE];
    }

    /**
     * @return the size of the grid
     */
    public static int getGridSize()
    {
        return GRID_SIZE;
    }

    /**
     * @return the minimum number value
     */
    public static int getMinNum()
    {
        return MIN_NUM;
    }

    /**
     * @return the maximum number value
     */
    public static int getMaxNum()
    {
        return MAX_NUM;
    }

    /**
     * Fills the numbers array with random values.
     */
    public void generateNumbers()
    {
        for (int i = 0; i < GRID_SIZE; i++)
        {
            numbers[i] = random.nextInt(MAX_NUM - MIN_NUM + LOWER_BOUND) + MIN_NUM;
        }
    }

    /**
     * Checks if a number can be placed at a given index.
     *
     * @param num the number to place
     * @param cellIndex the index in the grid
     * @return true if placement is valid, false otherwise
     */
    public boolean isValidPlacementFor(final int num, final int cellIndex)
    {
        if (filled[cellIndex])
        {
            return false;
        }

        for (int i = cellIndex - OFFSET_INDEX; i >= 0; i--)
        {
            if (filled[i])
            {
                if (grid[i] > num)
                {
                    return false;
                }
                break;
            }
        }

        for (int i = cellIndex + OFFSET_INDEX; i < GRID_SIZE; i++)
        {
            if (filled[i])
            {
                if (grid[i] < num)
                {
                    return false;
                }
                break;
            }
        }
        return true;
    }

    /**
     * Checks if the number can be placed anywhere on the grid.
     *
     * @param num the number to check
     * @return true if at least one valid placement exists
     */
    public boolean hasAnyValidPlacement(final int num)
    {
        for (int i = 0; i < GRID_SIZE; i++)
        {
            if (!filled[i] && isValidPlacementFor(num, i))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Starts the game. Must be implemented by subclasses.
     */
    public abstract void startGame();

    /**
     * @return the grid array
     */
    public int[] getGridInternal()
    {
        return grid;
    }

    /**
     * @return the filled state array
     */
    public boolean[] getFilledInternal()
    {
        return filled;
    }

    /**
     * @return the generated numbers array
     */
    public int[] getNumbersInternal()
    {
        return numbers;
    }

    /**
     * @return the random number generator
     */
    public Random getRandom()
    {
        return random;
    }

    /**
     * @return the current index
     */
    public int getCurrentIndex()
    {
        return currentIndex;
    }

    /**
     * Sets the current index.
     *
     * @param i the new index
     */
    public void setCurrentIndex(int i)
    {
        currentIndex = i;
    }

    /**
     * @return true if the game is over
     */
    public boolean getGameOver()
    {
        return gameOver;
    }

    /**
     * @return true if the player won
     */
    public boolean getWon()
    {
        return won;
    }

    /**
     * @return number of successful placements
     */
    public int getSuccessfulPlacementsInternal()
    {
        return successfulPlacements;
    }

    /**
     * Increments the number of successful placements.
     */
    public void incrementSuccessfulPlacements()
    {
        successfulPlacements++;
    }

    /**
     * Sets a value in the grid and marks it as filled.
     *
     * @param index the grid index
     * @param value the value to place
     */
    public void setCell(final int index, final int value)
    {
        grid[index]   = value;
        filled[index] = true;
    }

    /**
     * @return the grid (Playable interface)
     */
    @Override
    public int[] getGrid()
    {
        return grid;
    }

    /**
     * @return true if the game is over (Playable interface)
     */
    @Override
    public boolean isGameOver()
    {
        return gameOver;
    }

    /**
     * Sets the game over state.
     *
     * @param v true if game is over
     */
    public void setGameOver(boolean v)
    {
        gameOver = v;
    }

    /**
     * @return true if the player has won (Playable interface)
     */
    @Override
    public boolean isWon()
    {
        return won;
    }

    /**
     * Sets the win state.
     *
     * @param v true if won
     */
    public void setWon(boolean v)
    {
        won = v;
    }

    /**
     * @return number of successful placements (Playable interface)
     */
    @Override
    public int getSuccessfulPlacements()
    {
        return successfulPlacements;
    }

    /**
     * Sets the number of successful placements.
     *
     * @param v the new value
     */
    public void setSuccessfulPlacements(int v)
    {
        successfulPlacements = v;
    }

    /**
     * Gets the current number to place.
     *
     * @return the current number, or -1 if out of bounds
     */
    @Override
    public int getCurrentNumber()
    {
        if (currentIndex < GRID_SIZE)
        {
            return numbers[currentIndex];
        }
        return -1;
    }
}