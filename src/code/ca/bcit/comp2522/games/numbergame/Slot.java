package ca.bcit.comp2522.games.numbergame;

/**
 * Represents a single slot/cell in the number game grid.
 * Each slot can hold a number and maintains its occupancy state.
 * Slots enforce the ascending order constraint when values are placed.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class Slot implements Playable {

    private int value;
    private boolean isOccupied;

    /**
     * Constructs an empty slot.
     */
    public Slot() {
        this.value = 0;
        this.isOccupied = false;
    }

    /**
     * Check if this slot is occupied.
     *
     * @return true if the slot contains a value, false otherwise
     */
    public boolean isOccupied() {
        return isOccupied;
    }

    /**
     * Get the value stored in this slot.
     *
     * @return the value in the slot, or 0 if empty
     */
    public int getValue() {
        return value;
    }

    /**
     * Place a value in this slot.
     * Only succeeds if the slot is empty.
     *
     * @param valueParam the number to place
     */
    public void placeValue(int valueParam) {
        if (isOccupied) {
            return;
        }
        this.value = valueParam;
        this.isOccupied = true;
    }

    /**
     * Not used in Slot context - implemented for Playable interface.
     */
    @Override
    public boolean placeNumber(int cellIndex) {
        // Handled by the game logic, not by individual slots
        return false;
    }

    /**
     * Not used in Slot context - implemented for Playable interface.
     */
    @Override
    public boolean isGameOver() {
        return false;
    }

    /**
     * Not used in Slot context - implemented for Playable interface.
     */
    @Override
    public boolean isWon() {
        return false;
    }

    /**
     * Not used in Slot context - implemented for Playable interface.
     */
    @Override
    public int getCurrentNumber() {
        return value;
    }

    /**
     * Not used in Slot context - implemented for Playable interface.
     */
    @Override
    public int[] getGrid() {
        return new int[]{value};
    }

    /**
     * Not used in Slot context - implemented for Playable interface.
     */
    @Override
    public boolean[] getFilled() {
        return new boolean[]{isOccupied};
    }

    /**
     * Not used in Slot context - implemented for Playable interface.
     */
    @Override
    public int getSuccessfulPlacements() {
        return isOccupied ? 1 : 0;
    }
}