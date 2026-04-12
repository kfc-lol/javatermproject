package ca.bcit.comp2522.games.mygame;

/**
 * Represents the four cardinal directions used for maze navigation.
 * Used by both the player and the BFS solver to indicate movement direction,
 * check wall existence, and reveal walls on failed move attempts.
 *
 * @author Kian Castro
 * @version 1.0
 */
public enum Direction
{
    NORTH,
    SOUTH,
    EAST,
    WEST
}