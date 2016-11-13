### Boggle Game
[Screenshots] / [Youtube demo]
![Boggle Game - start up screen]
(screenshots/gui1.png)  

My Boggle game support 4x4, 5x5 and 6x6 boards with double letters (An, Er, He, In, Qu and Th) and a blank dice.  It will generate the random board or custom setup by the player.  

There are 3 dictionaries can be choose from: 
* [OSPD] - Official Scrabble Player Dictionary (US)  
* [EOWL] - English Open Word List (UK)  
* [SOWPODS] - English-language tournament Scrabble 
Player may use their choice of dictionary file in txt format.

[System requirements and installation]  

### GUI design

The GUI is writtern in pyqt5, using [py4j] connected to dictionary support in java.  It starts with a 4x4 Boggle board with OSPD US dictionary.  Player may change the Boggle size and dictionary from the menu bar.

Simply click the button to start a new game.  It will count down 3 mins for 4x4 and 5x5 board, and 4 mins for 6x6 board; and display the possible maximum scores.  The game will terminate either run out of time or the player found all words.

Player has two ways to submit the words:  
  1. Type the word on the left side. Notes: Copy and paste is not allowed.  
  2. Click the frist letter, highlight the word, and click the last letter.  

If the player change the dictionary while the game is running.  It will reset and restart the current game.  The possible maximum scores may changed based on the new dictionary.

### Dictionary support - 26 Way Radix Trie

The dictionary is stored in trie structure written in java.  I take the advantage of [R-way trie] on performance by Princeton University and [Radix (PATRICIA) trie] on memory usage from the internet and called it "26 Way Radix Trie". 
* First 2 letter always stored as 26-way Trie.  
* Remaining string stored as combined into new R-way Radix trie as below:  
<pre>
    ...
    ABSENCE
    ABSENT
    ABSOLUTE
    ABSOLUTELY
    ...
    [0(A) 1(B) ... ... 25(Z)]
       |
       V
    [0(A) 1(B) ... ... 25(Z)]
            | 
            V
    [... ... 18(S) ... ...]
                |
                V
    [... ... 4(E) ... ... ... ... ... 14(O)... ...]
               |                         |
               N                       LUTE
               |                    (ABSOLUTE)
               V                        |
    [... 2(C) ... ... 19(T) ...]        V
           |             |        [... 11(L) ...]
           E          (ABSENT)            |
       (ABSENCE)                          Y
                                   (ABSOLUTELY)
</pre>

* TrieInterface.java - The interface of universal trie structure and standard functions.

* [BoggleTrie26WayRadix.java] -- A data type of trie combine the concept of R-way trie and Radix Trie, stores in 2 dimension array.

* [BoggleDictionary.java] -- A data type to load dictionary for Boggle.  A customize version of trie designed for Boggle that dictionary is a final set.  Unlike standard trie structure, once the (dictionary) tire object created, changes (put or delete feature) is not allowed.  It load all words in trie, then reorder it is specific order as describe below and packed it in 1 dimension array.  It reduced over 50% the memory usage and the performance improvement above the same or slightly better.  

* BoggleSolver.java -- Use depth first search to find all words from the boggle board.  Modify my original version from programming assignment to support double letters and blank dice.

<pre>
                Has next trie   is a word   has radix       flags / indicators
    1st set         Yes             No         No
                                                        hasWord1
    2nd set         Yes             Yes        No
                                                        hasRadix (also use as end Word1)
    3rd set         Yes             No         Yes
                                                        hasWord2
    4th set         Yes             Yes        Yes
                                                        endTrie
    5th set         No              Yes        Yes
                                                        endRadix
    last set        No              Yes        No    
</pre>
---
###Compare the memory and timing
<pre>
    Computing memory of BoggleSolver     26-way trie   vs   (NEW) 26-way radix trie with reorder
    =================================================================================
    Test 1: memory with dictionary-algs4.txt (must be <= 2x reference solution). 
    memory of student   BoggleSolver   = 2423928 bytes      907984 bytes
    student / reference                = 0.47               0.18  
    Test 2: memory with dictionary-shakespeare.txt (must be <= 2x reference solution).
    memory of student   BoggleSolver   = 8069192 bytes      3361808 bytes
    student / reference                = 0.46               0.19  
    Test 3: memory with dictionary-yawl.txt (must be <= 2x reference solution).
    memory of student   BoggleSolver   = 85540064 bytes     41151512 bytes
    student / reference                = 0.48               0.23  
    
    Timing BoggleSolver                  26-way trie   vs  (NEW) 26-way radix trie with reorder
    =================================================================================
    Test 2: timing getAllValidWords() for 5.0 seconds using dictionary-yawl.txt
    (must be <= 2x reference solution)
    reference solution calls per second: 9428.12            9688.37
    student solution calls per second:   20030.11           20418.32
    reference / student ratio:           0.47               0.47
    
</pre>
[Screenshots]: https://github.com/mwong510ca/BoggleGame/blob/master/screenshots/README.md
[Youtube demo]: https://youtu.be/KAHKMHzHqos
[R-way trie]: http://algs4.cs.princeton.edu/lectures/52Tries.pdf
[Radix (PATRICIA) trie]: https://en.wikipedia.org/wiki/Radix_tree
[BoggleTrie26WayRadix.java]: https://github.com/mwong510ca/java_code/blob/master/Algorithm%20-%2026-way%20Radix%20Trie%20-%20Boggle%20/BoggleTrie26WayRadix.java%20-%20Details.md
[BoggleDictionary.java]: https://github.com/mwong510ca/java_code/blob/master/Algorithm%20-%2026-way%20Radix%20Trie%20-%20Boggle%20/BoggleDictionary.java%20-%20Details.md
[OSPD]: http://www.puzzlers.org/pub/wordlists/ospd.txt
[EOWL]: http://dreamsteep.com/projects/the-english-open-word-list.html
[SOWPODS]: https://www.wordgamedictionary.com/sowpods/
[System requirements and installation]: https://github.com/mwong510ca/BoggleGame/tree/master/gui(pyqt5)
[py4j]: https://www.py4j.org
