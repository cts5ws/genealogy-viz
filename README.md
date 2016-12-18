# genealogy-viz
Cole Schafer's Fall 2016 Capstone Research supervised by Luther Tychonievich
  * Topic: improving visualization methods used to represent family history data. 
  * This body of work comprises a collection of layout algorithms that show and handle family history data in a variety of ways.

##Setup
Upon cloning the repository two path variables must be changed in _/java/familylayout/src/modules/StaticUtils.java_. The variables of interest are '_dataIn_' and '_dataOut_.' '_dataIn_' is the location of your JSON to be read in. The inputted JSON must be in the format specified in _/java/person_model.json_. This JSON is created using a GEDCOM -> JSON parser. The '_dataOut_' variable should set the path location where you would like the output JSON to be written. This should be the path to the cloned reposity + _/geneaology-viz/d3/layout.json_.

To view the output of a layout algorithm, you need a web-server of some sort. I recommend running __python -m SimpleHTTPServer__ in the _/geneaology-viz/d3_ folder.

##Description of Layout Algorithms
1. __Descendancy Layout (Layout.java)__
  * __Description:__
This approach was my first attempt at visualization. The algorithm displays the specified number of generations of an individual.
2. __Descendancy Layout: OO (LayoutD.java)__
  * __Description:__
This approach also displays a descendancy tree of a queried individual. Instead of using data structures for book keeping this implementation uses object oriented features of the Java programming language to better keep track of the data. This implementation is input orientated and retrieves multiple parameters before processing the output.
  * __Features:__
    * __Generation Spanning:__
Generation spanning occurs when an individual marries someone within their family in another generation (marrying niece or nephew). This results in an edge connection that skips a generation. This issue was handled in this implementation by graphing the older spouse twice. Firstly in their place as as a child, and secondly in their place as a spouse. When mousing over this individual their name will pop up in both locations to inform the user that the individual has two roles within the family. These nodes are also connected by a special blue edge to signify that they are the same person.
      * __Spouses:__
         1.  __Customizable Spouse Spacing : Uniform__
Spouses are added to the family tree with uniform spacing on the levels. This means that all nodes on a generation are an equal distance apart.
         2.  __Customizable Spouse Spacing : Adjacent__
Spouses are added adjacently to their spouse after all nodes have been positioned. This reduces cluttering when attempting to render a large number of nodes.
      * __Parents of Spouses:__
This feature iterates up a second time allowing the parents of spouses to be graphed. This is done in an attempt to see if the parents of spouses have any other roles in the family.
      * __Post Processing : Y coordinate as birth year__
This feature uses the birth year of each person to determine the Y coordinate when graphed. This layout adds additional context that isn’t seen in a normal layout.
3. __Ancestry Layout : OO (LayoutA.java)__
  * __Description:__
This layout uses the same logic is as the descendancy layout, but instead to show the ancestry tree of a queried node. 
  * __Features:__
     * __Siblings__
This feature allows for siblings of each node to be displayed along with the rest of the ancestors of the queried node. The implementation of this followed the same logic used to graph spouses in the descendancy layout.
     * __Post Processing : Y coordinate as birth year__
Just like above, this feature uses the birthday to determine the Y coordinate relative to the other nodes being graphed.
4. __Combined Ancestry and Descendants Layout : OO (LayoutB.java)__
  * __Description:__
This feature combines the functionality of both the descendancy and ancestry trees and combines them into one. The descendants are graphed below the queried node and the ancestors are graphed above. This implementation incorporates all features associated with the two layouts described above.
5. __Centered BFS Layout : OO (LayoutBFS.java)__
  * __Description:__
This layout differs from the others in that it asks users for a person to query and also asks how many edges away from the node the user would like to visualize. This is essentially a BFS approach to determine which nodes to graph.
6. __Optimal Tree Layout : (OOLayoutOTL.java)__
  * __Description:__
This layout will take a different approach to determining the X and Y of each node. While the layering approach will be utilized, this implementation will ensure that the child of any node will always have X value greater/less grandparent node. This will prevent gaps in data from causing 3 nodes from taking up an entire line and will ideally make the graph look better. 
7. __Weighted Tree Layout : (OOLayoutWeighted.java)__
  * __Description:__
This layout uses the number of descendants of each node to determine the spacing on a given layer. This provides much more room for people with many descendants, than those with one descendant.
8. __Generalized Handling of Inter-Family Marriage__
  * __Description:__
The current generalized case handles any interfamily marriage in a descendancy tree. My implementation redraws the spouse of the highest generation adjacent to their spouse of the lower generation. This makes the most to me with respect to locating the children in their generation. This approach uses the same front end components as the generation spans. Mouse-over on the copied node results in names appearing on both instances. Also a blue edge connects the two same nodes.
9. __Displaying Parents of Spouses in Descendancy Tree (SpouseParent.java)__
  * __Description__
This layout attempts to intuitively place place the parents of spouses on a descendancy tree. My solution leverages the minX and maxX information assigned to the person who is married the spouse being graphed and places the parents directly above. This solution is not optimal since FamilyNodes can be placed in the same location, but it is an attempt at the issue.
10. __Family Rooted Descendancy Tree (InvertedFamilyTree.java)__
  * __Description__
This layout queries a FamilyNode and places parents to the left and children to the right. This continues recursively in a similar manner to the optimal tree layout. This layout needs work to more clearly show the desired information.


##Approach to Layout Algorithms
The general approach taken to implement the layouts below is similar. I will first highlight the similarities between all the layouts below and for each layout I will discuss what was done in the approach to make the unique layout. 
	 
All layout algorithms below start by reading in the family data from a JSON file. This is done using the [Jackson](https://github.com/FasterXML/jackson) JSON parsing library. The JSON file contains a list of people objects with their specified data. Jackson marshals the entire JSON file into an array of ‘Person Objects.’ 
	 
Next the algorithm iterates through the array of ‘Person’ object and sets up data structures for bookkeeping that are eventually used for ordering purposes. Once this is complete the algorithm iterates through families and people and creates the edges in the graph.
	 
Once this is complete the algorithm creates each level of the tree and uses the family information to order the nodes in a way to minimize edge crossings. Lastly the the relevant nodes and edges are written to a JSON file that is used by [D3](https://github.com/d3/d3/wiki/gallery) on the front end to render the family tree.
   
1. __Descendancy Layout__
  * __Approach: __
This approach uses only data structures to store the information. This worked initially, but as the code base became larger the overhead of the data structures made the code to complicated to work with.
2. __Descendancy Layout : OO__
  * __Approach:__
In this implementation I used an abstract ‘Node’ class as the starting point for keeping track of the information. I then created two classes ‘PersonNode’ and ‘FamilyNode’ that implemented the Node class. The most important attribute of the ‘Node’ class is each Node’s incoming and outgoing edges. Using these classes in an OO approach made it much easier to work with the data.
      * __Generation Spanning__
A generation span is detected by finding a node that has an outgoing edge in the wrong level. Once this node is identified the properly location of both instances is determined. At this point a duplicate node is created and both nodes representing the same person are graphed. A field is also passed with the nodes to let D3 know if a duplicate is present and with this information D3 knows to highlight both instances of the node when a mouseover occurs. A blue edge also connects the two nodes representing the same person.
      * __Spouses__
          1. __Customizable Spouse Spacing : Uniform__
Spouses are found by iterating from the bottom of the graph up and identifying incoming edges of nodes that are not yet graphed. Once one of these nodes is identified it is stored in a special class for updating the Map that stores the levels. This is done because it is not possible to add to a data structure while it is being iterated over. After determining relevant spouses and where they should be located they are added into the Map that stores the levels of the family tree. This is done before the location of each node is determined resulting in an even spacing between all nodes in a level.
          2. __Customizable Spouse Spacing : Adjacent__
In this feature spouses are found the same way but instead inserted once the locations of each node in the descendancy graph have been determined. Each spouse has it’s location set to 5 pixels to the right of their respective spouse. This is done in an effort to minimize clutter in the graph while still displaying the spouses of all members of the descendancy tree.
      * __Parents of Spouses__
This is achieved in the same way spouses are. Because they are two generations above members of the descendancy tree it is more difficult to locate them to minimize edge crossings. This aspect of my work could use some more work.
      * __Post Processing : Y coordinate as birth year__
This is done as the last step before writing the data to the JSON file. First the minimum and maximum ages of the to-be-graphed nodes are determined. This determines the range that needs to be used when converting birth year to an actual Y on the graph. This is achieved through simple algebra and the Y coordinate of each node is updated in the very last step.
3. __Ancestry Layout : OO__
  * __Approach:__
This implementation works exactly the same as the Descendancy Layout described above. The only difference is the logic is reversed to iterate up instead of down.
     1. __Siblings:__
This is achieved by iterating through the levels in the opposite direction and looking for the outgoing edges of each node. This is exactly the same logic that was used to find spouses in the descendancy tree.
     2. __Post Processing : Y coordinate as birth year__
This uses the same logic described earlier to use birth year to determine the Y location.
4. __Combined Ancestry and Descendants Layout : OO__
  * __Approach:__
This approach combines the logic described in the ancestry and descendancy layout algorithms to view both graphs together at the same time. In this implementation the ancestry and descendancy graphs extend out from the center node that was queried upon.
5. __Centered BFS Layout : OO__
  * __Approach:__
This implementation differs from the others in that it takes a BFS approach to determining which nodes to graph. The user is asked for a node to query and a number of hops to traverse in order to find relatives. This approach displays every node that is within the inputted number of hops away from the relevant node. This is achieved by adding two fields to each Node: numHopsAway and level. The variable representing the number of hops away from the queried node stores just that information. The level field determines which level the node should be placed on with the queried node on level 0, descendants, at negative levels, and ancestors at positive levels. This information allows for a BFS approach to family history to be displayed. My implementation works well for viewing 2 hops away. When processing larger queries more edge crossings occur. This stems from another previous issue. Inserting nodes a level above or below their source can be done using the PersonNodeUpdate class and simple algorithm I have used many times. It when trying to graph for example, parents of spouses in a descendancy graph, Ththat determining the location to place the nodes for the parents becomes challenging. This could be another issue to explore.
6. __Optimal Tree Layout:__
  * __Approach:__
This layout divides each layer based on the number of nodes in that layer. This layout also uses the constraint that the child of any node must be within the range that the parent is allowed to span. There are certain issues with this as the children multiple layers down get very clustered. I’m considering the possibility of of having some constant to multiply as the graph iterates down. This would allow the children to expand to some degree.
7. __Weighted Tree Layout:__
__Approach:__
  * This implementation first calculates the descendants for each node. For each given layer the total number of descendants of each node is summed. Each nodes X range is then determined by what percentage of the total their descendants make up. This creates a lot of space for nodes that have many children and provides very little space to nodes with no children. This is a more efficient utilization of space.
8. __Generalized Handling of Inter-Family Marriage__
  * __Approach:__
This solution is a generalized solution to the generation span problem discussed above. I identified the issue in a descendancy tree by comparing nodes famS against each other. If the famS is the same and the nodes are not the same then we have identified an inter-family marriage. I stored the intermediate information in the format as source => spouse of higher generation and target => spouse of lower generation. This is used in that a copy of the source will be displayed adjacent to the target. After identifying the inter-family marriages a copy is made of each source node. It’s copyNum is set to 1 and is placed adjacent to the spouse. Next the incoming and outgoing edges of the source and sourceCopy must be adjusted and the incoming edges of the family node must be adjusted to not contain the original source. From here the linking function identifies copies and passes a special attribute to the front end to inform D3 that there are  two instances of a node. The front end uses this information to display both nodes names on a mouseover (thus showing they are the same) and draws a blue edge connecting the identical nodes.
9. __Displaying Parents of Spouses in Descendancy Tree (SpouseParent.java)__
  * __Approach:__
The location of spouses parents is determined by placing them within the range of the married descendant. Issues occur when there is a descendant placed in the same X location as the FamilyNode in the level above, causing the FamilyNode of the spouse to be placed on top of the FamilyNode of the spouse’s spouses.
10. __Family Rooted Descendancy Tree (InvertedFamilyTree.java)__
  * __Approach:__ 
  This approach uses the same layout logic as the optimal tree layout. Starting with the queried FamilyNode, it places parents to the left and then children to the right. In lower levels in connects children to the FamilyNode in the level below and continues the desired number of levels.
  
##Unexplored Topics
1. Locating loops in the data and determine locations that loops can be split
  * i.e. what is the smallest change we can make to a graph to break loops
2. Add multiple spouses to a Node and see how my code handles this situation
  * Make appropriate changes to handle this
3. When Y is determined by age sort X value by age as well for the children of each person
4. What other information can we show with the data we have?
5. Let X location be determined by some recursive formula
  * F_x = S_x - 1/hops away
  * M_x = S_x + 1/ hops away
  * ^ just an idea towards the formula to be used
6. Break even spacing and take an iterative approach to locating nodes
  * Post processing
7. Experiment with spacing
8. Change distance in Y between levels
  * Farther spacing near most relevant node and closer spacing farther out
9. Reduce length of edges after each successive hop, resulting in no edge crossings

