package ca.bcit.comp2522.games.mygame;

/**
 * Thrown when an invalid move is attempted in the maze.
 * This is an unchecked exception since it indicates a programming error —
 * callers should validate direction input before attempting a move rather
 * than catching this exception in normal control flow.
 *
 * Examples of invalid moves include a null direction or a direction that
 * somehow bypasses the key handler validation.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class InvalidMoveException extends RuntimeException
{
    /**
     * Constructs an InvalidMoveException with the given detail message.
     *
     * @param message a description of why the move was invalid
     */
    public InvalidMoveException(final String message)
    {
        super(message);
    }

    /**
     * Constructs an InvalidMoveException with a detail message and cause.
     *
     * @param message a description of why the move was invalid
     * @param cause   the underlying exception that triggered this one
     */
    public InvalidMoveException(final String    message,
                                final Throwable cause)
    {
        super(message, cause);
    }
}