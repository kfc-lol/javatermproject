package ca.bcit.comp2522.games.mygame;

/**
 * Tracks tick counts for the player and the BFS algorithm across a maze run.
 * A tick is registered on every input the player makes, regardless of whether
 * the move was successful or resulted in a wall collision.
 * <p>
 * Also calculates a performance score (0–100) based on how efficiently
 * the player completes the maze compared to the BFS algorithm.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class MazeScore
{
    private static final int INITIAL_TICKS        = 0;
    private static final int MAX_SCORE            = 100;
    private static final int MIN_SCORE_DIFFERENCE = 0;
    private static final int GREAT_SCORE_BOUND    = 80;
    private static final int GOOD_SCORE_BOUND     = 60;
    private static final int OK_SCORE_BOUND       = 40;
    private static final int BAD_SCORE_BOUND      = 20;

    private int     playerTicks;
    private int     algorithmTicks;
    private boolean playerFinished;
    private boolean algorithmFinished;

    /**
     * Constructs a MazeScore with all counters reset to zero.
     */
    public MazeScore()
    {
        playerTicks       = INITIAL_TICKS;
        algorithmTicks    = INITIAL_TICKS;
        playerFinished    = false;
        algorithmFinished = false;
    }

    /**
     * Increments the player's tick counter by one.
     * Has no effect if the player has already finished.
     */
    public void incrementPlayerTicks()
    {
        if (!playerFinished)
        {
            playerTicks++;
        }
    }

    /**
     * Increments the algorithm's tick counter by one.
     * Has no effect if the algorithm has already finished.
     */
    public void incrementAlgorithmTicks()
    {
        if (!algorithmFinished)
        {
            algorithmTicks++;
        }
    }

    /**
     * Marks the player as having reached the exit.
     */
    public void markPlayerFinished()
    {
        playerFinished = true;
    }

    /**
     * Marks the algorithm as having reached the exit.
     */
    public void markAlgorithmFinished()
    {
        algorithmFinished = true;
    }

    /**
     * Returns the player's total tick count.
     *
     * @return player ticks
     */
    public int getPlayerTicks()
    {
        return playerTicks;
    }

    /**
     * Returns the algorithm's total tick count.
     *
     * @return algorithm ticks
     */
    public int getAlgorithmTicks()
    {
        return algorithmTicks;
    }

    /**
     * Returns whether the player has finished the maze.
     *
     * @return true if the player reached the exit
     */
    public boolean hasPlayerFinished()
    {
        return playerFinished;
    }

    /**
     * Returns whether the algorithm has finished the maze.
     *
     * @return true if the algorithm reached the exit
     */
    public boolean hasAlgorithmFinished()
    {
        return algorithmFinished;
    }

    /**
     * Calculates a score from 0 to 100 based on how the player performed
     * relative to the BFS algorithm.
     * <p>
     * 100 points → player is equal or faster than BFS
     * 0 points   → player is 100+ ticks slower than BFS
     *
     * @return score between 0 and 100
     */
    public int getPerformanceScore()
    {
        if (!playerFinished || !algorithmFinished)
        {
            return MIN_SCORE_DIFFERENCE;
        }

        final int difference = playerTicks - algorithmTicks;

        if (difference <= MIN_SCORE_DIFFERENCE)
        {
            return MAX_SCORE;
        }

        if (difference >= MAX_SCORE)
        {
            return MIN_SCORE_DIFFERENCE;
        }

        return MAX_SCORE - difference;
    }

    /**
     * Returns a performance rank based on the score.
     *
     * @return rank description
     */
    public String getPerformanceRank()
    {
        final int score = getPerformanceScore();

        if (score == MAX_SCORE)
        {
            return "For all I know, you could be an algorithm!";
        }
        else if (score >= GREAT_SCORE_BOUND)
        {
            return "Robots got NOTHING on you";
        }
        else if (score >= GOOD_SCORE_BOUND)
        {
            return "Good job!";
        }
        else if (score >= OK_SCORE_BOUND)
        {
            return "Not bad...";
        }
        else if (score >= BAD_SCORE_BOUND)
        {
            return "Got lost?";
        }
        else
        {
            return "Try again";
        }
    }

    /**
     * Returns a result summary describing who won, including score and rank.
     *
     * @return a human-readable result string
     */
    public String getResultSummary()
    {
        final String result;

        if (!playerFinished && !algorithmFinished)
        {
            result = "Game in progress.";
        }
        else
        {
            final int    score = getPerformanceScore();
            final String rank  = getPerformanceRank();

            if (playerFinished && !algorithmFinished)
            {
                result = "You win! You reached the exit in "
                         + playerTicks + " ticks.\nScore: "
                         + score + "\n" + rank;
            }
            else if (algorithmTicks < playerTicks)
            {
                result = "BFS wins! " + algorithmTicks
                         + " ticks vs your " + playerTicks
                         + " ticks.\nScore: "
                         + score + "\n" + rank;
            }
            else
            {
                result = "It's a tie! Both finished in "
                         + playerTicks + " ticks.\nScore: "
                         + score + "\n" + rank;
            }
        }

        return result;
    }

    /**
     * Resets all tick counts and finish flags for a new game.
     */
    public void reset()
    {
        playerTicks       = INITIAL_TICKS;
        algorithmTicks    = INITIAL_TICKS;
        playerFinished    = false;
        algorithmFinished = false;
    }
}