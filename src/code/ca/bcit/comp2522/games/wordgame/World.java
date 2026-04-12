package ca.bcit.comp2522.games.wordgame;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the world as a collection of countries.
 * Countries are loaded from external files at construction time.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class World
{
    private static final String RESOURCE_FOLDER = "src/resources/";
    private static final String FILE_EXTENSION = ".txt";
    private static final String COLON_DELIMITER = ":";
    private static final int COUNTRY_NAME_INDEX = 0;
    private static final int CAPITAL_CITY_INDEX = 1;
    private static final int FACTS_ARRAY_SIZE = 3;
    private static final char FIRST_LETTER = 'a';
    private static final char LAST_LETTER = 'z';
    public static final char W_FILE = 'w';
    public static final char X_FILE = 'x';
    private final Map<String, Country> countries;

    /**
     * Constructs a World by loading all countries from the a.txt to z.txt files.
     * Each file contains countries starting with that letter, in the format: Name:Capital followed by facts on separate lines.
     */
    public World()
    {
        countries = new HashMap<>();
        loadCountries();
    }

    /**
     * Loads all countries from data files (a.txt through z.txt).
     * Each file contains country entries in format: Country:Capital followed by 3 facts.
     */
    private void loadCountries()
    {
        for (char currentLetter = FIRST_LETTER; currentLetter <= LAST_LETTER; currentLetter++)
        {
            if (currentLetter == W_FILE || currentLetter == X_FILE)
            {
                continue;
            }
            
            final String filename = RESOURCE_FOLDER + currentLetter + FILE_EXTENSION;
            final File file = new File(filename);

            loadCountriesFromFile(file);

        }

        System.out.println("Loaded " + countries.size() + " countries.");
    }

    /**
     * Loads countries from a single data file.
     * Reads country:capital line, then reads exactly 3 fact lines, skips blank line.
     *
     * @param fileParam the file to read countries from
     */
    private void loadCountriesFromFile(final File fileParam)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileParam)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                // Skip blank lines
                if (line.isEmpty())
                {
                    continue;
                }

                // This line should be country:capital
                parseAndStoreCountry(reader, line);
            }
        }
        catch (final IOException ioException)
        {
            System.err.println("Error reading " + fileParam.getName() + ": " + ioException.getMessage());
        }
    }

    /**
     * Parses a country:capital line and reads the following three facts.
     *
     * @param readerParam the BufferedReader to read facts from
     * @param countryCapitalLineParam the "Country:Capital" line to parse
     */
    private void parseAndStoreCountry(final BufferedReader readerParam,
                                      final String countryCapitalLineParam)
    {
        try
        {
            final String[] parts = countryCapitalLineParam.split(COLON_DELIMITER, 2);
            if (parts.length != 2)
            {
                return;
            }

            final String countryName = parts[COUNTRY_NAME_INDEX].trim();
            final String capitalCityName = parts[CAPITAL_CITY_INDEX].trim();

            // Read the next three facts
            final String[] facts = new String[FACTS_ARRAY_SIZE];
            for (int i = 0; i < FACTS_ARRAY_SIZE; i++)
            {
                final String factLine = readerParam.readLine();
                if (factLine != null)
                {
                    facts[i] = factLine.trim();
                }
            }

            // Create and store the country
            final Country country = new Country(countryName, capitalCityName, facts);
            countries.put(countryName, country);
        }
        catch (final IOException ioException)
        {
            System.err.println("Error parsing country data: " + ioException.getMessage());
        }
    }

    /**
     * Returns the country with the given name, or null if not found.
     *
     * @param name the name of the country to look up
     * @return the matching Country, or null if no match exists
     */
    public Country getCountry(final String name)
    {
        return countries.get(name);
    }

    /**
     * Returns an unmodifiable view of all countries in the world.
     *
     * @return an unmodifiable map of country names to Country objects
     */
    public Map<String, Country> getCountries()
    {
        return countries;
    }

    public String[] getAllCountryNames()
    {
        return countries.keySet().toArray(new String[0]);
    }
}