
## JAVA program implementing the minimax algorithm with max cutoff and alpha beta pruning 



## How to run the Minimax program?

1. SIMPLE, download all files in one location and just hit 'make' on your command line. The makefile will compile the minimaxAI.java file. Note there will be two .class files created - minimaxAI.class and Node.class
2. Run "java minimaxAI <arguments> <file destination>"
3. Note that the program expects overall two arguments passed at the least - max/min and the input file name. The program will throw an error if it doesn't atleast get these two arguments. Note input file is always the last argument as denoted in the problem statement as well.
4. We can also pass other type of arguments : -v to print in verbose mode, -ab to include alpha beta pruning and an integer value n for max cutoff consideration. These needn't be in any order.

Few samples of commands that are accepted:

java minimaxAI max -v -ab 14 ./input1.txt
java minimaxAI 18 min ./input2.txt
java minimaxAI max ./input3.txt
(assuming .class and .txt files are in same location and we are running our command from that location on command prompt)


5. The output will be printed on the command line prompt.


## Code Walkthrough

1. We have two classes defined : Node and the main minimaxAI class. Node stores the implementation of each node in the DAG. Information includes it's name, it's value, the list of children and information about whether it is a root or not and whether it is a terminal node or not. We have setter and getter functions defined in node for debugging purposes majorly.

2. In minimaxAI, we have 3 methods primarily defined
a)  parser() function
- This method reads the input file line by line and using regex pattern matching determines whether the line is describing a terminal node or a non terminal node. It also stores this relevant information in different hashmaps defined.
- There are three main hashmaps used : parentChildSet - key is parent node and it maintains a list of its children; childParentSet - key is child node and it maintains a list of its parents; terminals - this stores the terminal node name and thier values. Along with this, we also store all the nodes created in a hashset of node objects.
b) minimaxAlgo() method
- This function is the core implementation of the minimax algorithm along with alpha beta pruning and max cutoff information. At each stage we calculate the best possible value and pass the best node object to the caller function. 
- We take in arguments like isVerboseRequired to check how the output has to be printed. isAlphaBetaRequired stores information about whether user wants alpha beta pruning or not
c) multipleRootsPresent() 
- This method handles a negative case of when two or more nodes are root nodes. We print an appropiate statement and exit if this occurs.
- How do we check this? If a node is stored in parentChildSet but is not present in chiildParentSet, we know that that node is root. We keep a count of how many such nodes appear.
d) checkAllTerminalsHaveValues()
- This method is for leaf node failure and internal node failure scenarios - to check whether all terminal nodes have a value or not.
e) cyclesExist()
- This function runs over the two hashmaps childParent and parentChild set to check if a node is a parent of itself and returns true if so. The program doesn't handle cycles so we exit if cycles are present.




