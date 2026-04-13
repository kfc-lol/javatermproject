package ca.bcit.comp2522.games.mygame;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

/**
 * Launcher for the Maze Race game.
 * Manages the JavaFX lifecycle using Platform.startup() on the first launch
 * and Platform.runLater() on subsequent launches, allowing the game to be
 * opened multiple times from the console menu without throwing an
 * IllegalStateException.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class MazeGameLauncher
{
    private static final String WINDOW_TITLE = "Maze Race";

    private static boolean        javaFxStarted = false;
    private static CountDownLatch shutdownLatch;

    /**
     * Private constructor to prevent instantiation.
     * All methods are static; this class is never instantiated.
     */
    private MazeGameLauncher()
    {
    }

    /**
     * Launches the Maze Race game window.
     * Initialises the JavaFX runtime on the first call via Platform.startup(),
     * then reuses it on all subsequent calls via Platform.runLater().
     * Blocks the calling console thread until the window is closed.
     */
    public static void launch()
    {
        shutdownLatch = new CountDownLatch(1);

        if (!javaFxStarted)
        {
            javaFxStarted = true;
            Platform.startup(MazeGameLauncher::openWindow);
        }
        else
        {
            Platform.runLater(MazeGameLauncher::openWindow);
        }

        try
        {
            shutdownLatch.await();
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.err.println("Maze Race interrupted: " + e.getMessage());
        }
    }

    /**
     * Creates and displays the primary Stage on the JavaFX Application Thread.
     * Constructs a MazeScore and MazeUI, then shows the title screen.
     * Counting down the shutdown latch on close returns control to the
     * console menu.
     */
    private static void openWindow()
    {
        final Stage     stage;
        final MazeScore score;
        final MazeUI    ui;

        stage = new Stage();
        score = new MazeScore();
        ui    = new MazeUI(stage, score);

        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(false);
        stage.setOnHidden(e -> shutdownLatch.countDown());

        ui.showTitle();
        stage.show();
    }
}