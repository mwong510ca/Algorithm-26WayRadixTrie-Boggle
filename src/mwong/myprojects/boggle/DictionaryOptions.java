package mwong.myprojects.boggle;

/**
 * Dictionary options that can be used.
 *  <li>{@link #OSPD}</li>
 *  <li>{@link #EOWL}</li>
 *  <li>{@link #SOWPODS}</li>
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 *         www.github.com/mwong510ca/Boggle_TrieDataStructure
 */

public enum DictionaryOptions {
    OSPD("ospd.txt", "OSPD", "Official Scrabble Players Dictionary (US)"),
    EOWL("eowl.txt", "EOWL", "English Open Word List (UK)"),
    SOWPODS("sowpods.txt", "SOWPODS", "SOWPODS (SCRABBLE) Dictionary");

    private final String filename;
    private final String acronym;
    private final String description;

    // Initialize dictionary options type.
    private DictionaryOptions(String filename, String acronym, String description) {
        this.filename = filename;
        this.acronym = acronym;
        this.description = description;
    }

    /**
     * Return the String of dictionary filename.
     *
     * @return String of dictionary filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Return the String of dictionary acronym.
     *
     * @return String of dictionary acronym
     */
    public String getAcronym() {
        return acronym;
    }

    /**
     * Return the String of dictionary description.
     *
     * @return String of dictionary description
     */
    public String getDescription() {
        return description;
    }
}
