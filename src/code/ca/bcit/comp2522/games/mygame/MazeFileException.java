package ca.bcit.comp2522.games.mygame;

/**
 * Thrown when a maze file operation fails during reading or writing.
 * This is a checked exception since file failures are recoverable —
 * callers are expected to handle this exception and fall back gracefully,
 * for example by generating a random maze when a seed file cannot be read.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class MazeFileException extends Exception
{
    /**
     * Constructs a MazeFileException with the given detail message.
     *
     * @param message a description of the file operation that failed
     */
    public MazeFileException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a MazeFileException with a detail message and cause.
     * Use this constructor when wrapping a lower-level IOException so the
     * original cause is preserved in the stack trace.
     *
     * @param message a description of the file operation that failed
     * @param cause   the underlying exception that triggered this one
     */
    public MazeFileException(final String message,
                             final Throwable cause)
    {
        super(message, cause);
    }
}