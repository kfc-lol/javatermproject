package ca.bcit.comp2522.games;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents the world as a collection of countries.
 * Countries are loaded from an external file at construction time.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class World
{
    private static final String COUNTRIES_FILE    = "countries.txt";
    private static final String DELIMITER         = "\\|";
    private static final int    NAME_INDEX        = 0;
    private static final int    CAPITAL_INDEX     = 1;
    private static final int    FACTS_STARTING_INDEX = 2;

    private final Map<String, Country> countries;

    /**
     * Constructs a World by loading all countries from the countries.txt file.
     * Each line in the file must follow the format: Name|Capital|fact1|fact2|...
     */
    public World()
    {
        countries = new HashMap<>();
        loadCountries();
    }

    /**
     * Reads countries from the countries file and populates the countries map.
     * Each line is split by the pipe delimiter into name, capital, and facts.
     * Lines that are malformed (fewer than 2 fields) are skipped with an error message.
     */
    private void loadCountries()
    {
        final File file;
        file = new File(COUNTRIES_FILE);

        try(final Scanner scanner = new Scanner(file))
        {
            while(scanner.hasNextLine())
            {
                final String   line;
                final String[] parts;

                line  = scanner.nextLine().trim();
                parts = line.split(DELIMITER);

                if(parts.length < FACTS_STARTING_INDEX)
                {
                    System.err.println("Skipping unreadable line: " + line);
                    continue;
                }

                final String   name;
                final String   capital;
                final String[] facts;
                final Country  country;

                name    = parts[NAME_INDEX];
                capital = parts[CAPITAL_INDEX];
                facts   = Arrays.copyOfRange(parts, FACTS_STARTING_INDEX, parts.length);
                country = new Country(name, capital, facts);

                countries.put(name, country);
            }
        }
        catch(final FileNotFoundException e)
        {
            System.err.println("Error: " + COUNTRIES_FILE + " not found.");
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
        return Collections.unmodifiableMap(countries);
    }
}