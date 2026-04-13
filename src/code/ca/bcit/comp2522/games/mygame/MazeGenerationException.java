package ca.bcit.comp2522.games.mygame;

/**
 * Thrown when maze generation fails due to invalid parameters.
 * This is an unchecked exception since invalid dimensions passed to
 * MazeGenerator represent a programming error that should be caught
 * during development rather than handled at runtime.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class MazeGenerationException extends RuntimeException
{
    /**
     * Constructs a MazeGenerationException with the given detail message.
     *
     * @param message a description of why generation failed
     */
    public MazeGenerationException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a MazeGenerationException with a detail message and cause.
     *
     * @param message a description of why generation failed
     * @param cause   the underlying exception that triggered this one
     */
    public MazeGenerationException(final String message,
                                   final Throwable cause)
    {
        super(message, cause);
    }
}