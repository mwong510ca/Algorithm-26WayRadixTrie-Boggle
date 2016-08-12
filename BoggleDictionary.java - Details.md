How it works : Sort the trie set in the order below.
<pre>
    Category            Has next trie   is a word   has radix       flags / indicators
    1st set             Yes             No          No
                                                                    hasWord1
    2nd set             Yes             Yes         No
                                                                    hasRadix (also use as end Word1)
    3rd set             Yes             No          Yes
                                                                    hasWord2
    4th set             Yes             Yes         Yes
                                                                    endTrie 
    5th set             No              Yes         Yes
                                                                    endRadix
    last set            No              Yes         No    
</pre>  

1. Load all words in 26-way Radix trie.  Retrieve a copy of all components.

2. Loop over it and get the count for each set, determine starting index of each group.

3. Initialize the storage:  
<pre>
    trieR26:  1 dimension array of trie in sorted order.  
              Size: sum of groups 1 - 4 plus 26 of first characters times 26.
    radix:    byte array of ASCII code, -1 end of radix indicator.  
              Size: actual usage size from trie object with few empty slots.  
    radixIdx: Integer array of starting radix position in radix array.  
              Size: Sum of groups 3 - 5   
    words:    String array of original key  
              Size: Sum of groups 2 - 6  
    visited:  re-useable int array for Boggle. Use for mark the found word in each boggle board, 
              prevent duplicate entry of the same word in Collections set to improve performance.  
              Size: Sum of groups 2 - 6  
</pre>     

4.  Create a conversion table and reference table.  Loop over it 2nd time to reorder
    these trie indexes.

5.  Compute the final copy into a 1 dimension array.

6.  Functions for Boggle  
    Base on the depth first searching of Boggle, design the function to lookup it category and status.  Use
    the defined indicator for all situations as describe above to improve performance.  Such as:
    * the trie node has radix - tire index must between 3rd set to 5th set
    * the trie node has next child node - trie index must between 1st set to 4th set
    * the trie node is a word - trie index either 2nd set or between 4th to 6th set 
