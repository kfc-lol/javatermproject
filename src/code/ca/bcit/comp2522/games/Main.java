package ca.bcit.comp2522.games;

import ca.bcit.comp2522.games.mygame.MazeGameLauncher;
import ca.bcit.comp2522.games.numbergame.NumberGameLauncher;
import ca.bcit.comp2522.games.wordgame.WordGame;

import java.util.Scanner;

/**
 * Entry point for the game suite.
 * Presents a menu allowing the user to select and play different games.
 * Manages the main game loop and routes users to appropriate game implementations.
 *
 * @author Kian
 * @version 1.0
 */
public final class Main
{

    private static final String INVALID_INPUT_MESSAGE = "Invalid input: ";
    private static final String GOODBYE_MESSAGE = "Goodbye!";
    private static final String WORD_GAME_OPTION = "W";
    private static final String NUMBER_GAME_OPTION = "N";
    private static final String MY_GAME_OPTION = "M";
    private static final String QUIT_OPTION = "Q";
    private static final String MENU_PROMPT = "Please provide an input (" +
                                              WORD_GAME_OPTION + ", " +
                                              NUMBER_GAME_OPTION + ", " +
                                              MY_GAME_OPTION + ", " +
                                              QUIT_OPTION +"):";

    /**
     * Private constructor to prevent instantiation.
     * This class contains only static utility methods.
     */
    private Main()
    {
    }

    /**
     * Main method that displays a menu and routes the user to the selected game.
     * W plays the Word Game, N plays the Number Game, M plays My Game, Q quits.
     * Manages the main game loop and handles user input validation.
     *
     * @param args unused command-line arguments
     */
    public static void main(final String[] args)
    {
        try (Scanner scan = new Scanner(System.in))
        {
            displayWelcomeMessage();
            gameLoop(scan);
        }
    }

    /**
     * Displays the welcome message and initial instructions to the user.
     */
    private static void displayWelcomeMessage()
    {
        System.out.println("\n------------------------------------------");
        System.out.println("|     Welcome to my Java Term Project!   |");
        System.out.println("|  Choose a game to play or quit:        |");
        System.out.println("|  (W)ord Game - Geography Trivia        |");
        System.out.println("|  (N)umber Game - Number Guessing       |");
        System.out.println("|  (M)aze Game - Beat The BFS            |");
        System.out.println("|  (Q)uit - Exit the suite               |");
        System.out.println("------------------------------------------\n");
    }

    /**
     * Main game loop that continuously displays the menu and processes user input.
     * Continues until the user selects the quit option.
     *
     * @param scanParam the Scanner for reading user input
     */
    private static void gameLoop(final Scanner scanParam)
    {
        boolean isRunning = true;

        while (isRunning)
        {
            System.out.println(MENU_PROMPT);

            if (scanParam.hasNextLine())
            {
                final String input = scanParam.nextLine().trim().toUpperCase();
                isRunning = processMenuSelection(input, scanParam);
            }
        }
    }

    /**
     * Processes the user's menu selection and routes to the appropriate game.
     *
     * @param inputParam the user's menu choice (w, n, m, or q)
     * @param scanParam the Scanner for passing to game methods
     * @return false if the user selected quit, true otherwise
     */
    private static boolean processMenuSelection(final String inputParam,
                                                final Scanner scanParam)
    {
        return switch (inputParam)
        {
            case WORD_GAME_OPTION ->
            {
                playWordGame(scanParam);
                yield true;
            }
            case NUMBER_GAME_OPTION ->
            {
                playNumberGame();
                yield true;
            }
            case MY_GAME_OPTION ->
            {
                playMyGame();
                yield true;
            }
            case QUIT_OPTION ->
            {
                System.out.println(GOODBYE_MESSAGE);
                yield false;
            }
            default ->
            {
                System.err.println(INVALID_INPUT_MESSAGE + inputParam);
                yield true;
            }
        };

    }

    /**
     * Launches the Word Game with the active scanner.
     * Creates a new WordGame instance and starts the game.
     *
     * @param scanParam the scanner used to read user input
     */
    private static void playWordGame(final Scanner scanParam)
    {
        System.out.println("\n--- Word Game Starting ---\n");
        final WordGame wordGame = new WordGame(scanParam);
        wordGame.play();
    }

    /**
     * Launches the Number Game using NumberGameLauncher.
     * Handles the JavaFX application lifecycle properly to allow returning to main menu.
     */
    private static void playNumberGame()
    {
        System.out.println("\n--- Number Game Starting ---\n");
        NumberGameLauncher.launch();
        System.out.println("\n--- Returning to Main Menu ---\n");
    }

    /**
     * Launches My Game with the active scanner.
     * Placeholder for custom game implementation.
     *
     */
    private static void playMyGame()
    {
        System.out.println("\n--- My Game Starting ---\n");
        MazeGameLauncher.launch();
    }
}