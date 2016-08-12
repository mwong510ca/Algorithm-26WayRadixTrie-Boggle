How it works: Insert a word in 26-way trie and store the radix.

1. Initialization: Determine the storage size.  
    * Estimate Capacity - start with 256 or given size + 26 reserved for first character.  
    * Estimate tire size - 2X, each insertion split at most once for the following storages:   
        2 Dimension trie storage, int array of radix index, int array of radix size, and String array of words.  
    * Estimate radix (substring) size - 4X rough estimate. Store in ASCII code (65-90), instead index (0-25).  

2. Insert the words from dictionary, assume all words in alphabet order.  
    It need 5 temporary storages to insert all words:  
    * trieR26 - 2 dimension int array for trie structure. Each expansion add 26 node for A - Z.  
    * radix - byte array for ASCII code, -1 for ended radix.  
    Substring inserted into radix storage once. During the split process, it will update radix position to new location and -1 for unused character. No shifting necessary.  
    * radixPos - starting position of radix storage reference to the trie index. During split, it will update it to new position. If no radix, it will reset to 0.  
    * radixLength - the byte array store the length of radix reference to the trie index.  
        During split, update the length changes.
    * values - store a copy of the word reference to the trie index. 
        During split, update to new location in needed.

There are 4 case of insertions:  
* Case 1: First entry of first character  
* Case 2: Extend at the end of the word  
* Case 3: Split in middle, new node require   
* Case 4: Split in middle, empty slot available  
Notes: 0 - 25 reserved for first character, new index starts from 26.  
Before each insert, if the storage size is almost full, it will double the size.  

Demonstrate a few words below:
<pre>
Insert 1st word: ABACUS (case 1 - first entry of A... word)                    values
Trie: [0][1] = 26 (B)                                                          [26] ABACUS
RadixPos[26] = 1
RadixLength[26] = 4 (ACUS)
"ABACUS" stores at 26
Radix storage = [0, A, C, U, S, -1, ...]
                    ^
                    |
                    26 

Insert 2nd word: ABANDON (case 3 - split at 3rd character 'A')                 values
RadixPos [26] = 1 (no change)                                                  [26] 
RadixLength[26] = 4 (ACUS) -> 1 (A)                                            [27] ABACUS
Trie[26] = new int [26]                                                        [28] ABANDON
Trie[26][2] = 27; (C)
"ABACUS" moved 26 -> 27
RadixPos[27] = 3
RadixLength[27] = 4 (ACUS) -> 2 (US)
Trie[26][13] = 28; (N)
RadixPos[28] = 6
RadixLength[28] = 3 (DON)
"ABANDON" stores at 28
Radix storage = [0, A, -1, U, S, -1, D, O, N, -1, ...]
                    ^      ^         ^
                    |      |         |
                    26     27        28

Insert 3rd word: ABANDONED (case 2 - extend end of word)                       values
Trie[28] = new int [26]                                                        [26] 
Trie[28][4] = 29; (E)                                                          [27] ABACUS
RadixPos[29] = 10                                                              [28] ABANDON
RadixLength[29] = 1 (D)                                                        [29] ABANDONED
"ABANDON" stores at 29
Radix storage = [0, A, -1, U, S, -1, D, O, N, -1, D, -1,...]
                    ^      ^         ^            ^
                    |      |         |            |
                    26     27        28           29

Insert 4th word: ABASE (case 4 - extend end of word)                           values
Trie[26][18] = 30; (S)                                                         [26] 
RadixPos [30] = 12                                                             [27] ABACUS 
RadixLength = 1 (E)                                                            [28] ABANDON
"ABASE" stores at 30                                                           [29] ABANDONED
Radix storage = [0, A, -1, U, S, -1, D, O, N, -1, D, -1, E, -1, ...]           [30] ABASE
                    ^      ^         ^            ^      ^
                    |      |         |            |      |
                    26     27        28           29     30

Insert 5th word: ABBEY (case 3 - split at 2nd character 'B')                   values
RadixPos[26] = 1 -> 0 (no radix)                                               [26] 
RadixLength[26] = 1 (A) -> 0                                                   [27] ABACUS 
Trie[0][1] = 26 -> 31 (B)                                                      [28] ABANDON
Trie[31] = new int[26]                                                         [29] ABANDONED
Trie[31][0] = 26; (A)                                                          [30] ABASE
Trie[31][1] = 32 (B)                                                           [31] 
RadixPos [32] = 13 (B)                                                         [32] ABBEY
RadixLength = 2 (EY)
"ABBEY" stores st 32
Radix storage = [0, -1, -1, U, S, -1, D, O, N, -1, D, -1, E, -1, E, Y, -1, ...]
                            ^         ^            ^      ^      ^
                            |         |            |      |      |
                            27        28           29     30     32
</pre>
