package ca.bcit.comp2522.games.mygame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/**
 * Manages saving and loading maze seeds to and from a text file.
 * Seeds are stored in maze_seed.txt in the working directory, allowing
 * players to save a maze they enjoyed and replay it exactly later.
 * <p>
 * File reading uses Scanner to read lines and the Stream API to locate
 * and parse the seed value, keeping each operation focused and readable.
 * All file errors are wrapped in MazeFileException so callers can recover
 * gracefully — for example by falling back to a random maze.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class MazeFileManager
{
    private static final String FILE_NAME    = "maze_seed.txt";
    private static final String SEED_KEY     = "MAZE_SEED = ";
    private static final String SEED_COMMENT = "# Maze Race saved seed";
    private static final int    SEED_KEY_LEN = SEED_KEY.length();

    /**
     * Private constructor to prevent instantiation.
     * All methods are static; this class is never instantiated.
     */
    private MazeFileManager()
    {
    }

    /**
     * Saves the given maze seed to maze_seed.txt in the working directory.
     * Overwrites any previously saved seed. The file is written in the format:
     * <pre>
     * # Maze Race saved seed
     * MAZE_SEED=1234567890
     * </pre>
     *
     * @param seed the maze seed to save
     * @throws MazeFileException if the file cannot be written
     */
    public static void saveSeed(final long seed) throws MazeFileException
    {
        final File file;
        file = new File(FILE_NAME);

        try (final FileWriter writer = new FileWriter(file))
        {
            writer.write(SEED_COMMENT + System.lineSeparator());
            writer.write(SEED_KEY + seed + System.lineSeparator());
        }
        catch (final IOException e)
        {
            throw new MazeFileException(
                "Failed to save maze seed to " + FILE_NAME + ": "
                + e.getMessage(), e);
        }
    }

    /**
     * Loads a maze seed from maze_seed.txt in the working directory.
     * Uses Scanner to read all lines into an array, then uses the Stream
     * API with a filter and map to locate and parse the seed value.
     *
     * @return the saved maze seed
     * @throws MazeFileException if the file does not exist, cannot be read,
     *                           or does not contain a valid seed entry
     */
    public static long loadSeed() throws MazeFileException
    {
        final File file;
        file = new File(FILE_NAME);

        if (!file.exists())
        {
            throw new MazeFileException(
                FILE_NAME + " does not exist. Save a maze first.");
        }

        final String[] lines;
        lines = readLines(file);

        return parseSeedFromLines(lines);
    }

    /**
     * Returns true if maze_seed.txt exists in the working directory.
     * Used by MazeUI to decide whether to enable the Load Saved button.
     *
     * @return true if the seed file exists
     */
    public static boolean savedSeedExists()
    {
        return new File(FILE_NAME).exists();
    }

    /*
     * Reads all lines from the given file into a String array using Scanner.
     * Throws MazeFileException if the file cannot be opened or read.
     */
    private static String[] readLines(final File file) throws MazeFileException
    {
        final StringBuilder content;
        content = new StringBuilder();

        try (final Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                content.append(scanner.nextLine());
                content.append(System.lineSeparator());
            }
        }
        catch (final IOException e)
        {
            throw new MazeFileException(
                "Failed to read " + FILE_NAME + ": " + e.getMessage(), e);
        }

        return content.toString().split(System.lineSeparator());
    }

    /*
     * Uses the Stream API to find the MAZE_SEED= line and parse its value.
     * Filters out comment lines and empty lines, maps the seed line to its
     * numeric value, and returns the first match.
     * Throws MazeFileException if no valid seed line is found.
     */
    private static long parseSeedFromLines(final String[] lines)
        throws MazeFileException
    {
        final java.util.Optional<Long> seedValue;

        seedValue = Arrays.stream(lines)
                          .filter(line -> line.startsWith(SEED_KEY))
                          .map(line -> line.substring(SEED_KEY_LEN).trim())
                          .map(MazeFileManager::parseLong)
                          .filter(Objects::nonNull)
                          .findFirst();

        if (seedValue.isEmpty())
        {
            throw new MazeFileException(
                "No valid seed entry found in " + FILE_NAME + ".");
        }

        return seedValue.get();
    }

    /*
     * Parses a String to a Long, returning null if the string is not a
     * valid long value. Used as a method reference in the stream pipeline
     * to safely convert the seed string without throwing in the stream.
     */
    private static Long parseLong(final String text)
    {
        try
        {
            return Long.parseLong(text);
        }
        catch (final NumberFormatException e)
        {
            return null;
        }
    }
}