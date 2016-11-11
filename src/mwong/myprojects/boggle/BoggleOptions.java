package mwong.myprojects.boggle;

public enum BoggleOptions {
    CLASSIC("Classic", 4, 3, 
            new String [] {
                "AACIOT", "ABILTY", "ABJMOQ", "ACDEMP",
                "ACELRS", "ADENVZ", "AHMORS", "BIFORX",
                "DENOSW", "DKNOTU", "EEFHIY", "EGINTV",
                "EGKLUY", "EHINPS", "ELPSTU", "GILRUW",
            },
            null),
   NEW1992("New 1992", 4, 3,
    	    new String[] {
    		        "AAEEGN", "ABBJOO", "ACHOPS", "AFFKPS",
    		        "AOOTTW", "CIMOTU", "DEILRX", "DELRVY",
    		        "DISTTY", "EEGHNW", "EEINSU", "EHRTVW",
    		        "EIOSST", "ELRTTY", "HIMNUQ", "HLNNRZ",
    		    }, null),
    DELUXE("Deluxe", 5, 3,
    		new String[] {
    		        "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
    		        "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCNSTW",
    		        "CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DHHLOR",
    		        "DHHNOT", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
    		        "FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU"
    		    }, null),
    BIG("Big 1979", 5, 3,
    		new String[] {
    		        "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
    		        "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCENST",
    		        "CEIILT", "CEILPT", "CEIPST", "DDLNOT", "DHHLOR",
    		        "DHLNOR", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
    		        "FIPRSY", "GORRVW", "IPRRRY", "NOOTUW", "OOOTTU"
    		    }, null),
    SUPERBIG("Super Big", 6, 4,
    		new String[] {
    				"AAAFRS", "AAEEEE", "AAEEOO", "AAFIRS", "ABDEIO", "ADENNN",
    			    "AEEEEM", "AEEGMU", "AEGMNN", "AEILMN", "AEINOU", "AFIRSY",
    			    "AEHIQT", "BBJKXZ", "CCENST", "CDDLNN", "CEIITT", "CEIPST",
    			    "CFGNUY", "DDHNOT", "DHHLOR", "DHHNOW", "DHLNOR", "EHILRS",
    			    "EIILST", "EILPST", "EIO###", "EMTTTO", "ENSSSU", "GORRVW",
    			    "HIRSTV", "HOPRST", "IPRSYY", "JKQWXZ", "NOOTUW", "OOOTTU"
    		    }, new int[] {12} );
    
    private String type;
    private int size;
    private int minWordLength;
    private String[] dices;
    private int[] doubleDice;

    /**
     * Initializes a PatternOptions reference type.
     */
    BoggleOptions(String type, int size, int minWordLength, String[] dices, int[] doubleDice) {
        this.type = type;
        this.size = size;
        this.minWordLength = minWordLength;
        this.dices = dices;
        this.doubleDice = doubleDice;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return this.size;
    }

    public int getMinWordLength() {
        return this.minWordLength;
    }

    public String[] getDices() {
        return this.dices;
    }

    public boolean hasDoubleLetters(int index) {
        if (this.doubleDice == null)
        	return false;
        for (int idx : this.doubleDice) {
        	if (idx == index)
        		return true;
        }
        return false;
    }
}
