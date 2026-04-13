package ca.bcit.comp2522.games.numbergame;


/**
 * Interface for games.
 *
 * @author Kian Castro
 * @version 1.0
 */
public interface Playable
{

    /**
     * Attempt to place the current number into the given cell.
     *
     * @param cellIndex 0-based index into the 20-cell grid
     * @return if number placement was successful
     */
    boolean placeNumber(int cellIndex);

    /** @return {@code true} once the game has ended (win or loss). */
    boolean isGameOver();

    /** @return {@code true} if the player won (all 20 numbers placed). */
    boolean isWon();

    /**
     * @return the integer the player must now place, or {@code -1} if all
     *         numbers have already been placed.
     */
    int getCurrentNumber();

    /**
     * @return the current grid snapshot; element is 0 for an empty cell,
     *         otherwise the number placed there.
     */
    int[] getGrid();

    /**
     * @return how many numbers have been successfully placed this round.
     */
    int getSuccessfulPlacements();
}