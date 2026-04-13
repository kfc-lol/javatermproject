package ca.bcit.comp2522.games.wordgame;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a score record for a game session.
 * Tracks the number of correct answers at different attempt levels
 * and calculates average points per game.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class Score
{
    private static final int FIRST_ATTEMPT_POINTS = 2;
    private static final int SECOND_ATTEMPT_POINTS = 1;
    private static final double ZERO = 0.0;
    private static final int GAMES_PLAYED = 0;
    private static final int CORRECT_FIRST = 0;
    private static final int CORRECT_SECOND = 0;
    private static final int INCORRECT = 0;
    private static final int NO_GAMES_PLAYED = 0;

    private final LocalDateTime dateTimePlayed;
    private final int numGamesPlayed;
    private final int numCorrectFirstAttempt;
    private final int numCorrectSecondAttempt;
    private final int numIncorrectTwoAttempts;
    private final int score;

    /**
     * Constructs a Score object with game session statistics.
     *
     * @param dateTimePlayedParam the date and time the game was played
     * @param numGamesPlayedParam the number of games played in this session
     * @param numCorrectFirstAttemptParam the number of correct answers on first attempt
     * @param numCorrectSecondAttemptParam the number of correct answers on second attempt
     * @param numIncorrectTwoAttemptsParam the number of incorrect answers after two attempts
     */
    public Score(final LocalDateTime dateTimePlayedParam,
                 final int numGamesPlayedParam,
                 final int numCorrectFirstAttemptParam,
                 final int numCorrectSecondAttemptParam,
                 final int numIncorrectTwoAttemptsParam)
    {
        this.dateTimePlayed = dateTimePlayedParam;
        this.numGamesPlayed = numGamesPlayedParam;
        this.numCorrectFirstAttempt = numCorrectFirstAttemptParam;
        this.numCorrectSecondAttempt = numCorrectSecondAttemptParam;
        this.numIncorrectTwoAttempts = numIncorrectTwoAttemptsParam;
        this.score = numCorrectFirstAttemptParam * FIRST_ATTEMPT_POINTS + numCorrectSecondAttemptParam * SECOND_ATTEMPT_POINTS;
    }

    public int getScore()
    {
        return score;
    }

    /**
     * Gets the date and time the game was played.
     *
     * @return the LocalDateTime
     */
    public LocalDateTime getDateTimePlayed() {
        return dateTimePlayed;
    }

    /**
     * Gets the number of games played.
     *
     * @return the number of games
     */
    public int getNumGamesPlayed() {
        return numGamesPlayed;
    }

    /**
     * Gets the number of correct answers on first attempt.
     *
     * @return the count of first-attempt correct answers
     */
    public int getNumCorrectFirstAttempt() {
        return numCorrectFirstAttempt;
    }

    /**
     * Gets the number of correct answers on second attempt.
     *
     * @return the count of second-attempt correct answers
     */
    public int getNumCorrectSecondAttempt() {
        return numCorrectSecondAttempt;
    }

    /**
     * Gets the number of incorrect answers after two attempts.
     *
     * @return the count of both-attempt incorrect answers
     */
    public int getNumIncorrectTwoAttempts() {
        return numIncorrectTwoAttempts;
    }

    /**
     * Calculates the average points per game.
     * Scoring: First-attempt correct = 2 points,
     *          Second-attempt correct = 1 point,
     *          Both-attempt incorrect = 0 points.
     *
     * @return the average points per game, or 0.0 if numGamesPlayed is 0
     */
    public double getAveragePointsPerGame()
    {
        final int totalPoints = (numCorrectFirstAttempt * FIRST_ATTEMPT_POINTS)
                                + (numCorrectSecondAttempt * SECOND_ATTEMPT_POINTS);

        if (numGamesPlayed == NO_GAMES_PLAYED) {
            return ZERO;
        }

        return (double) totalPoints / numGamesPlayed;
    }

    /**
     * Returns a string representation of the score for file persistence.
     *
     * @return formatted string suitable for score.txt file
     */
    @Override
    public String toString()
    {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final StringBuilder sb;
        sb = new StringBuilder();
        sb.append("Date and Time: ");
        sb.append(dateTimePlayed.format(formatter));
        sb.append("\nGames Played: ");
        sb.append(numGamesPlayed);
        sb.append("\nCorrect First Attempts: ");
        sb.append(numCorrectFirstAttempt);
        sb.append("\nCorrect Second Attempts: ");
        sb.append(numCorrectSecondAttempt);
        sb.append("\nIncorrect Attempts: ");
        sb.append(numIncorrectTwoAttempts);
        sb.append("\nScore: ");
        sb.append(score);
        sb.append(" points\n");

        return sb.toString();
    }

    /**
     * Appends a score to a file.
     *
     * @param scoreParam the Score object to append
     * @param filenameParam the filename to append to
     * @throws IOException if an I/O error occurs
     */
    public static void appendScoreToFile(final Score scoreParam, final String filenameParam) throws IOException {
        try (final FileWriter fw = new FileWriter(filenameParam, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.print(scoreParam.toString());
        }
    }

    /**
     * Reads all scores from a file.
     *
     * @param filenameParam the filename to read from
     * @return a list of Score objects
     * @throws IOException if an I/O error occurs
     */
    public static List<Score> readScoresFromFile(final String filenameParam) throws IOException {
        final List<Score> scores = new ArrayList<>();
        final File file = new File(filenameParam);

        if (!file.exists()) {
            return scores;
        }

        try (final BufferedReader br = new BufferedReader(new FileReader(filenameParam))) {
            String line;
            LocalDateTime dateTime = null;
            int gamesPlayed = GAMES_PLAYED;
            int correctFirst = CORRECT_FIRST;
            int correctSecond = CORRECT_SECOND;
            int incorrect = INCORRECT;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Date and Time: ")) {
                    final String dateTimeStr = line.substring("Date and Time: ".length());
                    dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } else if (line.startsWith("Games Played: ")) {
                    gamesPlayed = Integer.parseInt(line.substring("Games Played: ".length()));
                } else if (line.startsWith("Correct First Attempts: ")) {
                    correctFirst = Integer.parseInt(line.substring("Correct First Attempts: ".length()));
                } else if (line.startsWith("Correct Second Attempts: ")) {
                    correctSecond = Integer.parseInt(line.substring("Correct Second Attempts: ".length()));
                } else if (line.startsWith("Incorrect Attempts: ")) {
                    incorrect = Integer.parseInt(line.substring("Incorrect Attempts: ".length()));
                } else if (line.startsWith("Score: ")) {
                    // End of a score record, create the Score object
                    if (dateTime != null) {
                        scores.add(new Score(dateTime, gamesPlayed, correctFirst, correctSecond, incorrect));
                        dateTime = null;
                    }
                }
            }
        }

        return scores;
    }
}

