package ca.bcit.comp2522.games.wordgame;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The main WordGame class that runs the geography trivia game.
 * Manages game flow, question delivery, scoring, and score persistence.
 *
 * @author COMP2522 Student
 * @version 1.0
 */
public final class WordGame
{
    private static final int TOTAL_QUESTIONS = 10;
    private static final int CAPITAL_GIVEN = 0;
    private static final int COUNTRY_GIVEN = 1;
    private static final int FACT_GIVEN = 2;
    private static final int QUESTION_TYPE_COUNT = 3;
    private static final String SCORE_FILE = "score.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final World world;
    private final Scanner scanner;
    private final List<Score> allScores;
    private int totalGamesPlayed;
    private int gamesPlayedInSession;
    private int totalCorrectFirstAttempt;
    private int totalCorrectSecondAttempt;
    private int totalIncorrectTwoAttempts;

    /**
     * Constructs a WordGame object.
     *
     * @param scannerParam the Scanner for user input
     */
    public WordGame(final Scanner scannerParam)
    {
        this.world = new World();
        this.scanner = scannerParam;
        this.allScores = new ArrayList<>();
        this.totalGamesPlayed = 0;
        this.totalCorrectFirstAttempt = 0;
        this.totalCorrectSecondAttempt = 0;
        this.totalIncorrectTwoAttempts = 0;
        loadScoreHistory();
    }

    /**
     * Starts the word game.
     * Main game loop that allows players to play multiple games.
     */
    public void play()
    {
        gamesPlayedInSession = 0;
        displayWelcomeMessage();

        boolean playAgain = true;
        while (playAgain)
        {
            playGame();
            gamesPlayedInSession++;
            playAgain = askPlayAgain();
        }

        saveAndShowFinalScore();
    }

    /**
     * Displays the welcome message and game instructions.
     */
    private void displayWelcomeMessage()
    {
        System.out.println("\nWelcome to the Country Guessing Game!\n");
        System.out.println("You will be given 10 questions and a hint to be able to guess.");
        System.out.println("If you are given a capital city or a fact about the country,");
        System.out.println("you will have to guess the country's name.");
        System.out.println("If you are given the country, you must guess its capital.\n");
    }

    /**
     * Plays one complete game consisting of 10 questions.
     * Each question is randomly selected from one of three types.
     */
    private void playGame()
    {

        int correctFirstAttempt = 0;
        int correctSecondAttempt = 0;
        int incorrectTwoAttempts = 0;

        final String[] allCountries = world.getAllCountryNames();
        final Random random = new Random();

        System.out.println("Gathering your questions...\n");

        for (int questionNumber = 0; questionNumber < TOTAL_QUESTIONS; questionNumber++)
        {
            final String randomCountryName = allCountries[random.nextInt(allCountries.length)];
            final Country country = world.getCountry(randomCountryName);

            final int questionType = random.nextInt(QUESTION_TYPE_COUNT);
            boolean correctOnFirstAttempt = false;
            boolean correctOnSecondAttempt = false;

            switch (questionType)
            {
                case CAPITAL_GIVEN:
                    correctOnFirstAttempt = askCapitalQuestion(country, true);
                    if (!correctOnFirstAttempt)
                    {
                        correctOnSecondAttempt = askCapitalQuestion(country, false);
                    }
                    break;
                case COUNTRY_GIVEN:
                    correctOnFirstAttempt = askCountryQuestion(country, true);
                    if (!correctOnFirstAttempt)
                    {
                        correctOnSecondAttempt = askCountryQuestion(country, false);
                    }
                    break;
                case FACT_GIVEN:
                    final String[] facts = country.getFacts();
                    final Random factRandom = new Random();
                    final String randomFact = facts[factRandom.nextInt(facts.length)];
                    correctOnFirstAttempt = askFactQuestion(country, randomFact, true);
                    if (!correctOnFirstAttempt)
                    {
                        correctOnSecondAttempt = askFactQuestion(country, randomFact, false);
                    }
                    break;
                default:
                    break;
            }

            if (correctOnFirstAttempt)
            {
                correctFirstAttempt++;
            }
            else if (correctOnSecondAttempt)
            {
                correctSecondAttempt++;
            }
            else
            {
                displayCorrectAnswer(country, questionType, randomCountryName);
                incorrectTwoAttempts++;
            }
        }

        // Update totals
        gamesPlayedInSession++;
        totalGamesPlayed++;
        totalCorrectFirstAttempt += correctFirstAttempt;
        totalCorrectSecondAttempt += correctSecondAttempt;
        totalIncorrectTwoAttempts += incorrectTwoAttempts;

        // Print game summary
        printGameSummary(gamesPlayedInSession, correctFirstAttempt, correctSecondAttempt, incorrectTwoAttempts);
    }

    /**
     * Displays the correct answer when the user fails both attempts.
     *
     * @param countryParam the country object
     * @param questionTypeParam the type of question asked
     * @param countryNameParam the country name
     */
    private void displayCorrectAnswer(final Country countryParam,
                                      final int questionTypeParam,
                                      final String countryNameParam)
    {
        System.out.print("The correct answer was ");
        if (questionTypeParam == COUNTRY_GIVEN)
        {
            System.out.println(countryParam.getCapitalCityName() + "\n");
        }
        else
        {
            System.out.println(countryNameParam + "\n");
        }
    }

    /**
     * Asks a capital city question.
     *
     * @param countryParam the country being asked about
     * @param isFirstAttemptParam whether this is the first or second attempt
     * @return true if answered correctly
     */
    private boolean askCapitalQuestion(final Country countryParam,
                                       final boolean isFirstAttemptParam)
    {
        if (isFirstAttemptParam)
        {
            System.out.println("Question: What country has " + countryParam.getCapitalCityName()
                               + " as its capital?");
        }
        return processGuess(countryParam.getName(), isFirstAttemptParam);
    }

    /**
     * Asks a country question.
     *
     * @param countryParam the country being asked about
     * @param isFirstAttemptParam whether this is the first or second attempt
     * @return true if answered correctly
     */
    private boolean askCountryQuestion(final Country countryParam,
                                       final boolean isFirstAttemptParam)
    {
        if(isFirstAttemptParam)
        {
            System.out.println("Question: What is the capital of " + countryParam.getName() + "?");
        }
        return processGuess(countryParam.getCapitalCityName(), isFirstAttemptParam);
    }

    /**
     * Asks a fact question.
     *
     * @param countryParam the country being asked about
     * @param factParam the specific fact to display
     * @param isFirstAttemptParam whether this is the first or second attempt
     * @return true if answered correctly
     */
    private boolean askFactQuestion(final Country countryParam,
                                    final String factParam,
                                    final boolean isFirstAttemptParam)
    {
        if (isFirstAttemptParam)
        {
            System.out.println("Fact: " + factParam);
        }
        return processGuess(countryParam.getName(), isFirstAttemptParam);
    }

    /**
     * Processes a user guess and provides feedback.
     *

     * @param expectedAnswerParam the expected answer (case-insensitive)
     * @param isFirstAttemptParam whether this is the first or second attempt
     * @return true if the answer is correct
     */
    private boolean processGuess(final String expectedAnswerParam,
                                 final boolean isFirstAttemptParam)
    {
        final String userAnswer = getUserInput();

        if (userAnswer.equalsIgnoreCase(expectedAnswerParam))
        {
            System.out.println("CORRECT!\n");
            return true;
        }
        else
        {
            if (isFirstAttemptParam)
            {
                System.out.println("INCORRECT! You have one more chance.");
            }
            else
            {
                System.out.println("INCORRECT!");
            }
            return false;
        }
    }

    /**
     * Gets user input from the Scanner, trimmed and lowercased.
     *
     * @return the user input
     */
    private String getUserInput()
    {
        return scanner.nextLine().trim().toLowerCase();
    }

    /**
     * Prints the summary for one game.
     *
     * @param gamesPlayedParam the number of games in this session
     * @param correctFirstParam the number of correct first-attempt answers
     * @param correctSecondParam the number of correct second-attempt answers
     * @param incorrectParam the number of incorrect answers
     */
    private void printGameSummary(final int gamesPlayedParam,
                                  final int correctFirstParam,
                                  final int correctSecondParam,
                                  final int incorrectParam)
    {
        System.out.println("\n--- Game Summary ---");
        final String gameWord = gamesPlayedParam == 1 ? "game" : "games";
        System.out.println("- " + gamesPlayedParam + " word " + gameWord + " played");

        final String firstWord = correctFirstParam == 1 ? "answer" : "answers";
        System.out.println("- " + correctFirstParam + " correct " + firstWord + " on the first attempt");

        final String secondWord = correctSecondParam == 1 ? "answer" : "answers";
        System.out.println("- " + correctSecondParam + " correct " + secondWord + " on the second attempt");

        final String incorrectWord = incorrectParam == 1 ? "answer" : "answers";
        System.out.println("- " + incorrectParam + " incorrect " + incorrectWord + " on two attempts each");
        System.out.println();
    }

    /**
     * Asks if the user wants to play again.
     * Validates input and rejects entries other than yes/no.
     *
     * @return true if the user wants to play again
     */
    private boolean askPlayAgain()
    {

        while (true)
        {
            System.out.println("Do you want to play again? (Y)es / (N)o");
            final String response = getUserInput();

            if (response.equals("yes") || response.equals("y"))
            {
                return true;
            }
            else if (response.equals("no") || response.equals("n"))
            {
                return false;
            }
            else
            {
                System.err.println("ERROR: Please enter 'Yes' or 'No'");
            }
        }
    }

    /**
     * Saves the final cumulative score and displays appropriate message.
     * Compares the current score with the high score and displays a message.
     */
    private void saveAndShowFinalScore()
    {
        final LocalDateTime now = LocalDateTime.now();
        final Score currentSession = new Score(now, totalGamesPlayed, totalCorrectFirstAttempt,
                                               totalCorrectSecondAttempt, totalIncorrectTwoAttempts);

        saveScore(currentSession);

        showHighScoreMessage(currentSession);

        System.out.println("\nThanks for playing! Goodbye!");
    }

    /**
     * Displays a high score message based on whether the current score is a new high score.
     *
     * @param currentSessionParam the current game session score
     */
    private void showHighScoreMessage(final Score currentSessionParam)
    {
        // If no previous scores, this is the first high score
        if (allScores.isEmpty())
        {
            System.out.println("\nCONGRATULATIONS! You are the new high score with an average of "
                               + String.format("%.2f", currentSessionParam.getAveragePointsPerGame())
                               + " points per game!");
            return;
        }

        // Find the highest score from previous games
        Score highScore = allScores.getFirst();
        for (final Score score : allScores)
        {
            if (score.getAveragePointsPerGame() > highScore.getAveragePointsPerGame())
            {
                highScore = score;
            }
        }

        final double currentAverage = currentSessionParam.getAveragePointsPerGame();
        final double highAverage = highScore.getAveragePointsPerGame();

        // Check if current score is a new high score
        if (currentAverage > highAverage)
        {
            System.out.println("\nCONGRATULATIONS! You are the new high score with an average of "
                               + String.format("%.2f", currentAverage)
                               + " points per game; the previous record was "
                               + String.format("%.2f", highAverage)
                               + " points per game on "
                               + highScore.getDateTimePlayed().format(FORMATTER) + ".");
        }
        else
        {
            System.out.println("\nYou did not beat the high score of "
                               + String.format("%.2f", highAverage)
                               + " points per game from "
                               + highScore.getDateTimePlayed().format(FORMATTER) + ".");
        }
    }

    /**
     * Saves a score to the score.txt file.
     * Appends to the file if it exists.
     *
     * @param scoreParam the Score object to save
     */
    private void saveScore(final Score scoreParam)
    {
        try
        {
            Score.appendScoreToFile(scoreParam, SCORE_FILE);
        }
        catch (final IOException ioException)
        {
            System.err.println("Error saving score: " + ioException.getMessage());
        }
    }

    /**
     * Loads all scores from score.txt file into memory.
     */
    private void loadScoreHistory()
    {
        try
        {
            this.allScores.addAll(Score.readScoresFromFile(SCORE_FILE));
        }
        catch (final IOException ioException)
        {
            System.err.println("Error loading score history: " + ioException.getMessage());
        }
    }
}