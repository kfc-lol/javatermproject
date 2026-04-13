package ca.bcit.comp2522.games.numbergame;

/**
 * Accumulates win/loss statistics across multiple rounds of play.
 * The summary string matches the format described in the specification.
 */
public class NumberScore
{

    private static final int MIN_GAMES_PLAYED = 0;
    private static final int NO_GAMES_PLAYED  = 0;
    public static final int SINGULAR = 1;
    public static final int MIN_WINS = 0;
    public static final int MIN_LOSSES = 0;
    private              int wins;
    private              int losses;
    private              int totalPlacements;

    /**
     * Record a won round with the given successful-placement count.
     *
     * @param placements numbers placed successfully
     */
    public void recordWin(int placements)
    {
        wins++;
        totalPlacements += placements;
    }

    /**
     * Record a lost round with the given successful-placement count.
     */
    public void recordLoss(int placements)
    {
        losses++;
        totalPlacements += placements;
    }

    public int getWins()
    {
        return wins;
    }

    public int getLosses()
    {
        return losses;
    }

    public int getTotalGames()
    {
        return wins + losses;
    }

    public int getTotalPlacements()
    {
        return totalPlacements;
    }

    public boolean hasGames()
    {
        return getTotalGames() > MIN_GAMES_PLAYED;
    }

    /**
     * Builds a summary string.
     */
    public String getSummary()
    {
        final int total = getTotalGames();
        if (total == NO_GAMES_PLAYED)
        {
            return "No games played yet.";
        }
        final double avg;
        final String avgStr;
        final String gameWord;
        final String plWord;
        final StringBuilder sb;

        avg = (double) totalPlacements / total;
        avgStr = String.format("%.2f", avg);
        if (total == SINGULAR)
        {
            gameWord = "game";
        }
        else
        {
            gameWord = "games";
        }

        if (totalPlacements == SINGULAR)
        {
            plWord = "placement";
        }
        else
        {
            plWord = "placements";
        }

        sb = new StringBuilder();

        if (wins > MIN_WINS && losses > MIN_LOSSES)
        {
            sb.append("You won ").append(wins).append(" out of ").append(total)
              .append(" ").append(gameWord)
              .append(" and you lost ").append(losses).append(" out of ").append(total)
              .append(" ").append(gameWord);
        }
        else if (wins > MIN_WINS)
        {
            sb.append("You won ").append(wins).append(" out of ").append(total)
              .append(" ").append(gameWord);
        }
        else
        {
            sb.append("You lost ").append(losses).append(" out of ").append(total)
              .append(" ").append(gameWord);
        }

        sb.append(", with ").append(totalPlacements)
          .append(" successful ").append(plWord)
          .append(", an average of ").append(avgStr).append(" per game.");

        return sb.toString();
    }
}