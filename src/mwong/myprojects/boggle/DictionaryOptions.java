package mwong.myprojects.boggle;

public enum DictionaryOptions {
	OSPD("ospd.txt", "OSPD", "Official Scrabble Players Dictionary (US)"),
    EOWL("eowl.txt", "EOWL", "English Open Word List (UK)"),
    SOWPODS("sowpods.txt", "SOWPODS", "SOWPODS (SCRABBLE) Dictionary");
    
    private final String filename;
    private final String acronym;
    private final String description;
    
    /**
     * Initializes a Direction reference type.
     */
	DictionaryOptions(String filename, String acronym, String description) {
        this.filename = filename;
        this.acronym = acronym;
        this.description = description;
    }
	
	public String getFilename() {
		return filename;
	}
	
	public String getAcronym() {
		return acronym;
	}
	
	public String getDescription() {
		return description;
	}
}
