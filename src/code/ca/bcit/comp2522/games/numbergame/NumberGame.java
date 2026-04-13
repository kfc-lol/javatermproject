package ca.bcit.comp2522.games.numbergame;

import java.util.Arrays;

public class NumberGame extends Game
{

    private static final int MIN_GRID_SIZE = 0;
    private static final int INCREMENT_INDEX = 1;
    private static final int STARTING_VALUE = 0;

    public NumberGame()
    {
        super();
    }

    /**
     * Starts the game.
     */
    @Override
    public void startGame()
    {
        Arrays.fill(getGridInternal(),   STARTING_VALUE);
        Arrays.fill(getFilledInternal(), false);
        setCurrentIndex(0);
        setGameOver(false);
        setWon(false);
        setSuccessfulPlacements(STARTING_VALUE);
        generateNumbers();
    }

    /**
     * Determines possibility of placing number in a spot.
     *
     * @param cellIndex 0-based index into the 20-cell grid
     * @return if valid placement
     */
    @Override
    public boolean placeNumber(final int cellIndex)
    {
        if (isGameOver() || isWon())
        {
            return false;
        }

        if (cellIndex < MIN_GRID_SIZE || cellIndex >= getGridSize())
        {
            return false;
        }

        if (getFilledInternal()[cellIndex])
        {
            return false;
        }

        final int num = getCurrentNumber();

        if (!isValidPlacementFor(num, cellIndex))
        {
            return false;
        }

        setCell(cellIndex, num);
        incrementSuccessfulPlacements();
        setCurrentIndex(getCurrentIndex() + INCREMENT_INDEX);


        if (getCurrentIndex() == getGridSize())
        {
            setWon(true);
            return true;
        }

        if (!hasAnyValidPlacement(getNumbersInternal()[getCurrentIndex()]))
        {
            setGameOver(true);
        }

        return true;
    }
}