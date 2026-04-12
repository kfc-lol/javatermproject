package ca.bcit.comp2522.games.numbergame;

import java.util.Arrays;

/**
 *
 */
public class NumberGame extends Game {

    public static final int MIN_GRID_SIZE = 0;

    public NumberGame() {
        super();
    }


    /** Reset all arrays and counters, then generate a fresh number sequence. */
    public void startGame() {
        Arrays.fill(grid,   0);
        Arrays.fill(filled, false);
        currentIndex         = 0;
        gameOver             = false;
        won                  = false;
        successfulPlacements = 0;
        generateNumbers();
    }

    @Override
    public boolean placeNumber(final int cellIndex) {
        // Game already finished – reject silently
        if (gameOver || won) return false;

        // Out-of-bounds guard
        if (cellIndex < MIN_GRID_SIZE || cellIndex >= GRID_SIZE) return false;

        // Cell already occupied – ignore per specification
        if (filled[cellIndex]) return false;

        int num = getCurrentNumber();

        // Check whether the placement respects ascending order
        if (!isValidPlacementFor(num, cellIndex)) {
            return false;  // Invalid placement - just reject, don't end game
        }

        // Accept the placement
        grid[cellIndex]  = num;
        filled[cellIndex] = true;
        successfulPlacements++;
        currentIndex++;

        // All 20 numbers placed – player wins
        if (currentIndex == GRID_SIZE) {
            won = true;
            return true;
        }

        // Check whether the next number can be placed anywhere
        if (!hasAnyValidPlacement(numbers[currentIndex])) {
            gameOver = true;          // no legal cell for next number → loss
        }

        return true;
    }
}