# DSProject
Building a xml editor from scratch

```python
stack.push("CSE CSE331s | Data Structures and Algorithms")
stack.push("Final Project")
```

<div align="center">
<img src="https://gcdnb.pbrd.co/images/xkXZWBLMhWq1.png?o=1" width="250" height="250" >  

![GitHub language count](https://img.shields.io/github/languages/count/islamzedd/DSProject?color=%2300&logo=GitHub)
![GitHub contributors](https://img.shields.io/github/contributors/islamzedd/DSProject?color=%2300&logo=GitHub)
![GitHub top language](https://img.shields.io/badge/Java-100.0%25-brightgreen)

The project is an XML Editor to do different operations on XML files.
</div> 

## Goals
 To build a GUI application able to: 
 - Correct xml files.
 - Format and Identation.
 - Convert xml files into JSON.
 - Minify file by removing extra spaces.
 - compress the file to reduce its size.
 - decompress a compressed XML file.
 - provide SNA on social network data
 - search through posts using word or topic
 - visualize a graph of the social network (bonus)
## Background
XML files is common format to transfer data across the internet gained its popularity in the early days of the web due to the vast similarity with HTML syntax.
Now, a better alternative is JSON format which stands for JavaScript Object Notation. both of these formats are used to send data in HTTP requests.
Due to the similarity with HTML we will adopt the DOM - Document Object Model - model used in modern browsers by parsing the file as a tree and 
applying different operations as needed by the user. Not only that but since the data represents a social network we can represent it as a graph to be able
to visualize it more clearly and perform operations on it

## Technologies
Java with JavaFX framework and GraphViz 3rd party visualization software

## Implementation Details

### 1. [Choosing an XML file, Reading and Parcing](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L53)
First we use [Choose xml file](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L53) to open a browser , read a file and checks if it’s .txt or .xml.
If it is, then proceeds to read each line in the file and stores it
in a **String Builder** of the raw input. Then removes all tabs and empty lines and store it in another string builder to be used later by other functions.
Then instantiate the xmlTree object to parse the text into a tree data structure using its built in [parse XML](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/XMLTree.java#L13) function.

### 2. [The XML Tree](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/XMLTree.java)
Having the XML file in string format, now we convert it to a Tree to make the best use of it.

Our approach was to create a TreeNode as general as possible while in the same time
solving all issues needed in the program so we decided on suitable data fieldsd to represent each node in the tree
which are represented in the figure below:

<p align="center">
  <img src="https://gcdnb.pbrd.co/images/Pp4kARFY4EIL.png?o=1">
</p>

### 3. [Checking and correcting errors](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L133)
The code will be divided into cases and in each case, we will be detecting the error, after we detect each error, we will know which type of error we
are dealing with, then we print that error and what is supposed to be done. After this, we push into the queue the correct tags and data that
we are expecting, after finishing all of this we will have a queue with the correction and all we have to do now is printing it in an organized form.
Function is divided into 4 sections and each section has its own cases to be dealt with: 
 - Open Tags
 - Spaces, New lines and Tabs
 - Data
 - Close Tags
 <p align="center">
  <img src="https://gcdnb.pbrd.co/images/pqB54l2qNJSt.png?o=1">
</p>

### 4. [Formatting](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L486)

We iterate in the xml tree in preorder traverse way, where it checks if the node is a parent and has children then
it prints the node name then the function calls itself on every child to that parent node from left to right and follows by the closing tag.
If the node is a leaf (no children) then it just prints the leaf node name followed by its data, then the closing tag. 
The format is done by adding a tab (/t) before every tag equal to its depth and start a new line (/n) after every tag.
This is called when the [Add Format](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L53) button is clicked

### 5. [Convering to JSON](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L570)
In this part the work is split amongst a few functions with the most noteable being the [pre order traverse](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L606)
We break down the conversion from xml to json to four cases. 
 - Case (1) leaf node with no sibling with same name: iterate over the depth of the node to get the name and the value and put them in json format. 
 - Case (2) leaf node with siblings of same name: iterate over the node and its siblings of the same name. where we don’t print the leaf nodes names but print their data under their parent’s name. 
 - Case (3) parent node with siblings of different names: loop over the depth of the node and print it in json format for parent nodes.
 - Case (4) parent node with siblings of same name: iterate over the node and its siblings marking visited once with visited attribute flag to not iterate over it again. Then use the proper json format for parents with siblings of same name while iterating. Then add the proper closing brackets.
### 6. [Minifying XML file](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L557)
It uses the principle of preorder travers to iterate over the xml tree then append all nodes after each other with no tabs,
spaces or new lines. This is done by first appending the node name to the empty string then call the function on all children
to the parent node to do the same then follow by appending the closing tags after them.

### 7. [Compressing](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L701) and [Decompressing](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L735)
 Huffman codes is a greedy algorithms which relys on "make the common case smaller" principle. In short, it compresses the data by preserving shorter bit streams for the more frequent letter and words. This means that the file to be constructed need to be studied for characters frequency. We used a simple script to count the number of occurences of each character in the file and store the result in a map then we sort them by value. This is done for every file before being processed instead of relying on generalization of characters frequency which degrades the alorithm performance of the alorithms in practical cases.
 
 A tree in which the most frequent characters are near the root is constructed greedly starting from the less to the more frequent characters. code is generated by traversing the tree, left generates a zero and right generates a one.
 after code generation the data is written in a binary file.
 Data is retrevied by traversing the tree using the codes until reaching the characters.
 
 The Algorithm generated half the size on average.
 - For compression we use the writeByte method of the Encoder class to encode and compress the string then we save the compressed file
 - For decompression we call the read and decode method of the Encoder class to decode and decompress the file. The decoded string is stored and displayed on the text flow input display window in the gui and other functions can be applied to it.
 
 ### 8. [The graph](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Graph.java)
 since the XML file represents the
users data in a social network (their posts, followers, ...etc).
The user data is his id (unique across the network), name, list of his posts and followers.
So we should represent the relation between the followers using the graph data
structure as it will be very helpful for the network analysis.
In this structure we implement the graph using a **Map** to represent its adjacency list as a map of **ArrayLists** 
this is poppulated by clever parsing of the XML using Matchers into the graph knowing that its tags dont vary too much
 <p align="center">
  <img src="https://gcdnb.pbrd.co/images/r4O6tfBKRdps.png?o=1">
</p>

### 9. [Network Analysis](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/networkAnalysis.java)
In social network analysis we analyze and extract 4 main pieces of data
 - [The most influencer user](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/networkAnalysis.java#L33) : This is done by checking in the adjacency list of the graph represnted by a **HashMap** on which user has the longest **LinkedLis**
 of followers by iterating over the users in the adjacency list
 - [The most active user](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/networkAnalysis.java#L52) : Checks the user who has the most connections by iterating over the **HashMap** to find the user with the highest outdegree by counting the
 number of appearances of each uses in another user's followers list
 - [mutual followers between two users](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/networkAnalysis.java#L128) : This function takes two inputs from the application user it can be either their ids or their names which we excpect
 a typical end user not an admin to use . after acquiring this information it compares the lists of their followers to find the common ones
 - [follower suggestion for users](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/networkAnalysis.java#L83) : This suggests followers to a certain user by returning all the followers of his followers (without duplication) and making
 sure he doesnot follow them already
 
 ### 10.[Searching posts](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/postFinder.java)
 Users in the social network can post different posts with different topics . To implement a search functionality we figured it was best to use
 raw XML and through clever use of Matchers find words or topics being searched for within their respective tags. This is implemented in two functions
  - [search by word](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/postFinder.java#L47) : searches a specific word in the post by first finding the body tag and searching it for the word and adding all posts that include it
  into a **LinkedList** then returning it when its done
  - [search by topic](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/postFinder.java#L16) : searches a specific word in the topic by first finding the topic tag and searching it for the word and adding all posts that include it
  into a **LinkedList** then returning it when its done

 ### 11.[Draw Graph](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L971)
 in this **Bonus** part we use a 3rd party tool to visualize the graph by first parsing the graph into a string that the 3rd party tool expects
 then passing it . the parsing is done by iterating over the adjacenct list element and pointing every follower to its following (duplication is automatically handled by the 3rd party tool)
 The tool used is [Graphviz](https://graphviz.org/).
 <br/>
 In order to use it it needs to be 
  1. downloaded from this [link](https://graphviz.org/download/)
  2. installed while making sure this option is selected when installing
  <p align="center">
  <img src="https://gcdnb.pbrd.co/images/rZfGgtkfBSWt.png?o=1">
</p>
  3. Restarting your machine
  <br/>
  After these 3 steps it can run smoothly and produce a result similar to the one shown above in the graph section

 ### 12.[saving the result](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Controller.java#L1033)
  Uses the printwriter java class to open a saving window and saving the string in the desired location.
 
 ### 13.[GUI](https://github.com/islamzedd/DSProject/blob/3046634592b62cca6521e860a42b36d0000549af/src/xml/Main.fxml)
 A simple GUI using JavaFx scenebuilder as the focus of this project wasnot on designing of a pretty interface 
 
 ## Testing
For testing, in addition to the sample files provided in the report extra files were used to test different scenarios . They can be found on the
drive folder for this project

 ## Sources
- converting from XML to JSON https://www.xml.com/pub/a/2006/05/31/converting-between-xml-and-json.html?fbclid=IwAR1Y5xWga_pcYSSR3SwCDyXKDpnKZp1ZG0otr53ZT8sk0eypqSzH5b_36kQ
- Huffman code Compression Algorithms https://www.coursera.org/learn/algorithms-greedy
- Programming tips and tricks https://geeksforgeeks.org and https://stackoverflow.com/
- Graphviz documentation https://graphviz.org/documentation/
 
