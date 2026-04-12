package ca.bcit.comp2522.games.numbergame;


/**
 * Interface defining the contract for the Number Placement Game.
 * Any game mode must implement these core operations.
 */
public interface Playable {

    /**
     * Attempt to place the current number into the given cell.
     *
     * @param cellIndex 0-based index into the 20-cell grid
     * @return {@code true} if the placement was accepted; {@code false} if
     *         the cell was already filled (click ignored) or if the placement
     *         violated ordering and the game is now over
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
     * @return parallel boolean array; {@code true} means the cell is filled.
     */
    boolean[] getFilled();

    /** @return how many numbers have been successfully placed this round. */
    int getSuccessfulPlacements();
}