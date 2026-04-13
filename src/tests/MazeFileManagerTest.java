import ca.bcit.comp2522.games.mygame.MazeFileException;
import ca.bcit.comp2522.games.mygame.MazeFileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MazeFileManager class.
 * Verifies save and load round-trips, missing file handling,
 * invalid file content handling, and file existence checking.
 * Each test cleans up maze_seed.txt in @AfterEach to avoid
 * polluting the working directory between runs.
 *
 * @author Kian Castro
 * @version 1.0
 */
public class MazeFileManagerTest
{
    private static final String FILE_NAME       = "maze_seed.txt";
    private static final long   SEED_VALUE      = 123456789L;
    private static final long   SEED_NEGATIVE   = -987654321L;
    private static final String INVALID_CONTENT = "MAZE_SEED=not_a_number\n";
    private static final String EMPTY_CONTENT   = "# just a comment\n";

    @BeforeEach
    void setUp()
    {
        deleteFile();
    }

    @AfterEach
    void tearDown()
    {
        deleteFile();
    }

    @Test
    void testSaveSeed_createsFile() throws MazeFileException
    {
        MazeFileManager.saveSeed(SEED_VALUE);
        assertTrue(new File(FILE_NAME).exists());
    }

    @Test
    void testSaveAndLoad_roundTrip() throws MazeFileException
    {
        MazeFileManager.saveSeed(SEED_VALUE);
        final long loaded;
        loaded = MazeFileManager.loadSeed();
        assertEquals(SEED_VALUE, loaded);
    }

    @Test
    void testSaveAndLoad_negativeSeed() throws MazeFileException
    {
        MazeFileManager.saveSeed(SEED_NEGATIVE);
        final long loaded;
        loaded = MazeFileManager.loadSeed();
        assertEquals(SEED_NEGATIVE, loaded);
    }

    @Test
    void testSaveAndLoad_overwritesPreviousSeed() throws MazeFileException
    {
        final long secondSeed;
        secondSeed = 999999999L;

        MazeFileManager.saveSeed(SEED_VALUE);
        MazeFileManager.saveSeed(secondSeed);

        final long loaded;
        loaded = MazeFileManager.loadSeed();
        assertEquals(secondSeed, loaded);
    }

    @Test
    void testLoadSeed_fileNotFound_throwsMazeFileException()
    {
        assertThrows(MazeFileException.class, MazeFileManager::loadSeed);
    }

    @Test
    void testLoadSeed_invalidContent_throwsMazeFileException()
        throws IOException
    {
        writeRawContent(INVALID_CONTENT);
        assertThrows(MazeFileException.class, MazeFileManager::loadSeed);
    }

    @Test
    void testLoadSeed_emptyContent_throwsMazeFileException()
        throws IOException
    {
        writeRawContent(EMPTY_CONTENT);
        assertThrows(MazeFileException.class, MazeFileManager::loadSeed);
    }

    @Test
    void testSavedSeedExists_returnsFalse_whenNoFile()
    {
        assertFalse(MazeFileManager.savedSeedExists());
    }

    @Test
    void testSavedSeedExists_returnsTrue_afterSave() throws MazeFileException
    {
        MazeFileManager.saveSeed(SEED_VALUE);
        assertTrue(MazeFileManager.savedSeedExists());
    }

    /*
     * Deletes maze_seed.txt from the working directory if it exists.
     * Called before and after each test to ensure a clean state.
     */
    private void deleteFile()
    {
        final File file;
        file = new File(FILE_NAME);

        if (file.exists())
        {
            file.delete();
        }
    }

    /*
     * Writes raw string content directly to maze_seed.txt for testing
     * invalid and malformed file scenarios.
     */
    private void writeRawContent(final String content) throws IOException
    {
        try (final FileWriter writer = new FileWriter(new File(FILE_NAME)))
        {
            writer.write(content);
        }
    }
}