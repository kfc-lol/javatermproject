package ca.bcit.comp2522.games.numbergame;

/**
 * Accumulates win/loss statistics across multiple rounds of play.
 * The summary string matches the format described in the specification.
 */
public class NumberScore {

    public static final int MIN_GAMES_PLAYED = 0;
    private int wins;
    private int losses;
    private int totalPlacements;

    /** Record a won round with the given successful-placement count. */
    public void recordWin(int placements) {
        wins++;
        totalPlacements += placements;
    }

    /** Record a lost round with the given successful-placement count. */
    public void recordLoss(int placements) {
        losses++;
        totalPlacements += placements;
    }

    public int getWins()            { return wins;            }
    public int getLosses()          { return losses;           }
    public int getTotalGames()      { return wins + losses;   }
    public int getTotalPlacements() { return totalPlacements; }

    public boolean hasGames() { return getTotalGames() > MIN_GAMES_PLAYED; }

    /**
     * Build a human-readable summary string that mirrors the examples given
     * in the specification, e.g.:
     * <pre>
     * "You lost 1 out of 1 game, with 12 successful placements,
     *  an average of 12 per game."
     *
     * "You won 1 out of 2 games and you lost 1 out of 2 games,
     *  with 24 successful placements, an average of 12 per game."
     * </pre>
     */
    public String getSummary() {
        int total = getTotalGames();
        if (total == 0) return "No games played yet.";

        double avg    = (double) totalPlacements / total;
        String avgStr = (avg % 1.0 == 0.0)
            ? String.valueOf((int) avg)
            : String.format("%.2f", avg);

        String gameWord = total == 1 ? "game" : "games";
        String plWord   = totalPlacements == 1 ? "placement" : "placements";

        StringBuilder sb = new StringBuilder();

        if (wins > 0 && losses > 0) {
            sb.append("You won ").append(wins).append(" out of ").append(total)
              .append(" ").append(gameWord)
              .append(" and you lost ").append(losses).append(" out of ").append(total)
              .append(" ").append(gameWord);
        } else if (wins > 0) {
            sb.append("You won ").append(wins).append(" out of ").append(total)
              .append(" ").append(gameWord);
        } else {
            sb.append("You lost ").append(losses).append(" out of ").append(total)
              .append(" ").append(gameWord);
        }

        sb.append(", with ").append(totalPlacements)
          .append(" successful ").append(plWord)
          .append(", an average of ").append(avgStr).append(" per game.");

        return sb.toString();
    }
}