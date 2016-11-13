###Preface
Besides the [lecture] of the R-way tire and ternary search tries (TST).  I found [Radix (PATRICIA) trie] on the internet, it reduce the depth of TST, but not beat the search performance of R-way trie.  
<pre>
                  Search hit      Search miss          Insert        Space
    R-way trie    L               log (base R) N       L             (R + 1) * N
    TST           L + ln N        ln (base 3) N        L + ln N      4 * N
    Radix trie    L + ln N        ln (base 3) N        L + ln N      4 * N
    R - R size of 256 for ASCII code, 26 for Boggle (A-Z).
    L - length of word. 
    N - size of words added to trie. 
</pre>

### 26WayRadixTrie - concept
I take the advantage of R-way trie on performance and Radix trie on memory usage.  First 2 letter always stored as 26wayTrie.  Remaining string stored as combined into new R-way Radix trie as below:  
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

###Redo the Boggle programming assignment
With my new 26-way radix trie on, it has slightly better performance and use less memory.

* BoggleBoardPlus.java -- A data type for Boggle.  
Load the BoggleBoard, keep a set of face character in ASCII code for radix search, a set trie node index (0 - 25) for trie search, and a set of it's neighbor dices' positions.'

* BoggleSolver.java:  Boggle solver that finds all valid words in a given Boggle board, using a given dictionary.  
Load the dictionary, analysis the boggle board, then walk through the board with depth first search.  If there is a word in dictionary, store the word in the words set.  Revisited word will not add the words set.

* TrieInterface - The interface of universal trie structure and standard functions.

* [BoggleTrie26WayRadix.java] -- A data type of trie combine the concept of R-way trie and Radix Trie, stores in 2 dimension array.

* [BoggleDictionary.java] -- A data type to load dictionary for Boggle.  A customize version of trie designed for Boggle that dictionary is a final set.  Unlike standard trie structure, once the (dictionary) tire object created, changes (put or delete feature) is not allowed.  It load all words in trie, then reorder it is specific order as describe below and packed it in 1 dimension array.  It reduced over 50% the memory usage and the performance improvement above the same or slightly better.  

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
[lecture]: http://algs4.cs.princeton.edu/lectures/52Tries.pdf
[Radix (PATRICIA) trie]: https://en.wikipedia.org/wiki/Radix_tree
[BoggleTrie26WayRadix.java]: https://github.com/mwong510ca/java_code/blob/master/Algorithm%20-%2026-way%20Radix%20Trie%20-%20Boggle%20/BoggleTrie26WayRadix.java%20-%20Details.md
[BoggleDictionary.java]: https://github.com/mwong510ca/java_code/blob/master/Algorithm%20-%2026-way%20Radix%20Trie%20-%20Boggle%20/BoggleDictionary.java%20-%20Details.md
