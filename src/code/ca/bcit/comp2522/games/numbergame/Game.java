package ca.bcit.comp2522.games.numbergame;

import java.util.Random;

public abstract class Game implements Playable {


    protected static final int GRID_SIZE = 20;
    protected static final int MIN_NUM   = 1;
    protected static final int MAX_NUM   = 1000;

    protected int[]     grid;
    protected boolean[] filled;
    protected int[]     numbers;
    protected int       currentIndex;
    protected boolean   gameOver;
    protected boolean   won;
    protected int       successfulPlacements;

    protected final Random random;

    public Game() {
        random  = new Random();
        grid    = new int[GRID_SIZE];
        filled  = new boolean[GRID_SIZE];
        numbers = new int[GRID_SIZE];
    }

    /** Fill the {@code numbers} array with GRID_SIZE random values [MIN, MAX]. */
    public void generateNumbers() {
        for (int i = 0; i < GRID_SIZE; i++) {
            numbers[i] = random.nextInt(MAX_NUM - MIN_NUM + 1) + MIN_NUM;
        }
    }

    public boolean isValidPlacementFor(int num, int cellIndex) {
        if (filled[cellIndex]) return false;

        // Scan left for nearest neighbour
        for (int i = cellIndex - 1; i >= 0; i--) {
            if (filled[i]) {
                if (grid[i] > num) return false;
                break;
            }
        }

        // Scan right for nearest neighbour
        for (int i = cellIndex + 1; i < GRID_SIZE; i++) {
            if (filled[i]) {
                if (grid[i] < num) return false;
                break;
            }
        }
        return true;
    }

    /**
     * @return {@code true} if {@code num} can be placed in at least one
     *         empty cell without violating the ascending-order constraint.
     */
    public boolean hasAnyValidPlacement(int num) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (!filled[i] && isValidPlacementFor(num, i)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------
    //  IGame default implementations (subclasses may override)
    // ---------------------------------------------------------------
    @Override public int[]     getGrid()                  { return grid;                 }
    @Override public boolean[] getFilled()                { return filled;               }
    @Override public boolean   isGameOver()               { return gameOver;             }
    @Override public boolean   isWon()                    { return won;                  }
    @Override public int       getSuccessfulPlacements()  { return successfulPlacements; }

    @Override
    public int getCurrentNumber() {
        return (currentIndex < GRID_SIZE) ? numbers[currentIndex] : -1;
    }
}