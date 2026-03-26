package ca.bcit.comp2522.games;

import java.util.Scanner;

/**
 * Entry point for games.
 * Presents a menu allowing the user to select and play different games.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class Main
{
    private static final String PROMPT = "Please provide an input (W, N, M, Q):";

    /**
     * Main method that displays a menu and routes the user to the selected game.
     * W plays the Word Game, N plays the Number Game, M plays My Game, Q quits.
     *
     * @param args unused
     */
    public static void main(final String[] args)
    {
        final Scanner    scan;
        final World      earth;
        final WordGame   wordGame;
        final NumberGame numberGame;
        final MyGame     myGame;


        scan       = new Scanner(System.in);
        earth      = new World();
        wordGame   = new WordGame(earth);
        numberGame = new NumberGame();
        myGame     = new MyGame();


        System.out.println(PROMPT);

        while(scan.hasNextLine())
        {
            final String input;
            input = scan.nextLine().trim().toLowerCase();

            switch(input)
            {
                case "w" -> playWordGame(scan);
                case "n" -> playNumberGame(scan);
                case "m" -> playMyGame(scan);
                case "q" ->
                {
                    System.out.println("Goodbye!");
                    scan.close();
                    return;
                }
                default -> System.err.println("Invalid input: " + input);
            }

            System.out.println(PROMPT);
        }

        scan.close();
    }

    /**
     * Launches the Word Game, passing the active scanner for input.
     *
     * @param scan the scanner used to read user input
     */
    private static void playWordGame(final Scanner scan)
    {
        System.out.println("Playing the Word Game");
        WordGame.play(scan);
    }

    /**
     * Launches the Number Game, passing the active scanner for input.
     *
     * @param scan the scanner used to read user input
     */
    private static void playNumberGame(final Scanner scan)
    {
        System.out.println("Playing the Number Game");
        // NumberGame.play(scan);
    }

    /**
     * Launches My Game, passing the active scanner for input.
     *
     * @param scan the scanner used to read user input
     */
    private static void playMyGame(final Scanner scan)
    {
        System.out.println("Playing My Game");
        // MyGame.play(scan);
    }
}