package ca.bcit.comp2522.games.numbergame;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

/**
 * Wrapper for launching the Number Game with proper JavaFX lifecycle management.
 * Ensures that the JavaFX runtime is initialised only once per JVM session,
 * allowing the game to be launched multiple times from the console menu without
 * throwing an IllegalStateException.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class NumberGameLauncher
{
    public static final int COUNT = 1;
    private static boolean javaFxStarted = false;
    private static CountDownLatch shutdownLatch;

    /**
     * Private constructor to prevent instantiation.
     */
    private NumberGameLauncher()
    {
    }

    /**
     * Launches the Number Game.
     * On the first call, initialises the JavaFX runtime via Platform.startup().
     * On subsequent calls, reuses the existing runtime via Platform.runLater().
     * Blocks the calling (console) thread until the game window is closed.
     */
    public static void launch()
    {
        shutdownLatch = new CountDownLatch(COUNT);


        if(!javaFxStarted)
        {
            javaFxStarted = true;
            Platform.startup(NumberGameLauncher::openWindow);
        }
        else
        {
            Platform.runLater(NumberGameLauncher::openWindow);
        }

        try
        {
            shutdownLatch.await();
        }
        catch(final InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.err.println("Number game interrupted: " + e.getMessage());
        }
    }

    /**
     * Creates and displays the primary Stage.
     * Must be called on the JavaFX Application Thread.
     */
    private static void openWindow()
    {
        final Stage stage;
        stage = new Stage();

        stage.setTitle("Number Game");
        stage.setResizable(false);

        stage.setOnHidden(e -> shutdownLatch.countDown());

        final NumberScore tracker;
        tracker = new NumberScore();

        final NumberUI ui;
        ui = new NumberUI(stage, tracker);
        ui.showMenu(null);

        stage.show();
    }
}