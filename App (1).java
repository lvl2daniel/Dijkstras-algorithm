import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;


// Custom Graph Class for running the algorithm
class Graph {

    int source;
    int[][] adjacencyList;

    //Only need adjacency list and source node for algo
    public Graph( int[][] adjacencyList, int source) {
        this.adjacencyList = adjacencyList;
        for(int i = 0; i < adjacencyList.length; i++){
            for(int j = 0; j < adjacencyList.length; j++){
                adjacencyList[i][j] = -1;
            }
        }
        // //System.out.println("Before add");
        // for(int[] e : adjacencyList){
        //     for (int j : e){
        //         //System.out.print(j + " ");
        //     }
        //     //System.out.print("\n");
        // }
        this.source = source;
    }


    

}

//Main class
public class App{
    public static void main(String[] args){

        //Load the graph from the file with loadGraph function
       Graph graph = loadGraph();
       //Custom Print Statements
       for(int i = 1; i < graph.adjacencyList.length; i++){
        //System.out.println("Connections and costs for vertex " + i);
        for(int j = 1; j < graph.adjacencyList.length; j++){
            // if(graph.adjacencyList[i][j] >= 0){
            //     //System.out.println("Node " + j + " Cost  " + graph.adjacencyList[i][j]);
            // }
            if(graph.adjacencyList[j][i] >= 0){
                // Set it so both ends of the adjacency list have the correct adjacency and cost
                graph.adjacencyList[i][j] = graph.adjacencyList[j][i];
                ////System.out.println("Node " + j + " Cost  " + graph.adjacencyList[j][i]);
            }
        }
       }
       //Run the algorithm
       runDijkstras(graph);
       return;
    }

    public static Graph loadGraph(){
         try {
            File file = new File("cop3502-asn2-input.txt");
            Scanner scanner = new Scanner(file);
            
            //Instantiate variables from file
            int numVertices = scanner.nextInt();
            int startVertex = scanner.nextInt();
            int numEdges = scanner.nextInt();

            // //System.out.println("Edges: " + numEdges + "Source: " + startVertex + "Number of Vertices: " + numVertices);

            //Size of adjacency list will always be num vertices + 1, it is also 1 indexed so row 0 and column 0 will have no data.
                                                                                                //^^Can probably fix this but it works
            int[][] sizeOfAdjList = new int[numVertices + 1][numVertices + 1];

            //construct graph
            Graph graph = new Graph(sizeOfAdjList, startVertex);
    
            for (int i = 0; i < numEdges; i++) {
                //After filling with -1, put correct costs 
                //System.out.println(i);
                int source = scanner.nextInt();
                int destination = scanner.nextInt();
                int cost = scanner.nextInt();
                graph.adjacencyList[source][destination] = cost;
            }

            // //System.out.println("After Add \n" );


            // Just for debugging.
            // for(int[] e : graph.adjacencyList){
            //     for(int j : e){
            //         //System.out.print(j + " ");
            //     }
            //     //System.out.print("\n");
            // }
    
            scanner.close();
            // Return the new filled graph
            return graph;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    //Honestly, what I used to run this feels very scrappy. But, it works and my understanding of adjacencyLists and the algorithm have improved ten-fold.
    public static void runDijkstras(Graph graph){
        // Instantiate array for output file
        int[][] valuesToWrite = new int[graph.adjacencyList.length - 1][3];
        // Instantiate current total Path cost.
        int pathCost = 0;
        // Instantiate source node.
        int src = graph.source;
        // For output file
        int initialSrc = src;
        // Keep track of all visited nodes.
        HashSet<Integer> visited = new HashSet<Integer>();
        visited.add(src);
        // Make a priority queue to always take the lowest costs
        PriorityQueue<QueueItem> queue = new PriorityQueue<QueueItem>();
        //While loop until visited all nodes
        while(visited.size() < graph.adjacencyList.length){
            for(int i = 1; i < graph.adjacencyList.length; i++){
                //Flag for if i changed an adjacency value
                boolean flag = false;
                // If there is an adjacency, do
                if(graph.adjacencyList[src][i] >= 0){
                    //Skip if seen
                    if(visited.contains(i))
                        continue;
                    //If queue isnt empty, do
                    if(!queue.isEmpty())
                    {
                        //This basically checks if the known costs for all visitable nodes can be decreased, decreases them to min possible value, and changes source.
                        for(QueueItem element : queue){
                            if (element.itemDestination == i){
                                flag = true;
                                //System.out.println("Found an item within the queue thats also adjacent to " + src);
                                //System.out.println("That item is " + i);
                                if(element.itemCost > graph.adjacencyList[src][i] + pathCost){
                                    //System.out.println("Replacing item " + i + " which has previous cost of " + element.itemCost + "\n" );

                                    //System.out.println("Cost of item in adjacency list " + graph.adjacencyList[src][i]);
                                    int newCost = graph.adjacencyList[src][i] + pathCost;
                                    graph.adjacencyList[src][i] = newCost;
                                    graph.adjacencyList[i][src] = graph.adjacencyList[src][i];
                                    element.itemCost = newCost;
                                    element.itemSource = src;
                                    //System.out.println("Item now has cost of " + newCost + "\n");
                                    element.printItem();
                                }
                            }
                        }
                    }
                    if(flag == true){
                        continue;
                    }

                    //Add item to queue with total Path cost
                    QueueItem q = new QueueItem(src, i, graph.adjacencyList[src][i] + pathCost);
                    q.printItem();
                    queue.add(q);
                    //System.out.println("Size of queue is now " + queue.size() + "\n");
                }
                //This is the same thing, basically safety net for reverse case/missed adjacency costs.
                if(graph.adjacencyList[i][src] >= 0){
                    if(visited.contains(i))
                        continue;
                    for(QueueItem element : queue){
                    if (element.itemDestination == i){
                        flag = true;
                        //System.out.println("Found an item within the queue thats also adjacent to " + src);
                        //System.out.println("That item is " + i);
                        if(element.itemCost > graph.adjacencyList[i][src] + pathCost){
                            //System.out.println("Replacing item " + i + " which has previous cost of " + element.itemCost + "\n" );

                            //System.out.println("Cost of item in adjacency list " + graph.adjacencyList[src][i]);

                            //Set the new cost to adjacent cost plus previous path taken.
                            int newCost = graph.adjacencyList[i][src] + pathCost;
                            //Set the cost in the adjacency list.
                            graph.adjacencyList[i][src] = newCost;
                            graph.adjacencyList[src][i] = graph.adjacencyList[i][src];
                            element.itemCost = newCost;
                            element.itemSource = src;
                            //System.out.println("Item now has cost of " + newCost + "\n");
                            element.printItem();
                            
                            }
                        }
                    }
                    if(flag == true)
                        continue;
                    QueueItem q = new QueueItem(src, i, graph.adjacencyList[i][src]);
                    q.printItem();
                    queue.add(q);
                    //System.out.println("Size of queue is now " + queue.size());
                }
            }
            //Write to file if finished 
            if (queue.peek() == null) {
                try {
                    FileWriter writer = new FileWriter(" cop3503-asn2-output-gonzalez-daniel.txt");
                    writer.write(graph.adjacencyList.length - 1 + "\n");

                    for (int[] e : valuesToWrite) {
                        for (int i = 0; i < e.length; i++) {
                            if (e[i] == 0) //Handling case for source node.
                                i++;
                            if (e[i] == 0)
                                i++;
                            if (e[i] == 0) {
                                e[i - 2] = initialSrc;
                                e[i - 1] = -1;
                                e[i] = -1;
                            }
                        }
                        writer.write(e[0] + " " + e[1] + " " + e[2] + "\n");
                    }

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
}
            //Set pathcost to newest cost from front of queue

            pathCost = queue.peek().itemCost;
            ////System.out.println("Setting pathcost to " + pathCost);
            ////System.out.println("iterating from " + queue.peek().itemSource + " to " + queue.peek().itemDestination + " with cost " + queue.peek().itemCost);
            //Set output values to correct source, destination, and cost.
            valuesToWrite[queue.peek().itemDestination - 1][0] = queue.peek().itemDestination;
            valuesToWrite[queue.peek().itemDestination - 1][1] = queue.peek().itemCost;
            valuesToWrite[queue.peek().itemDestination - 1][2] = queue.peek().itemSource;
            //Bring from front of queue
            src = queue.poll().itemDestination;
            ////System.out.println("Iterating to " + src + "\n");
            //Add to visited hashset
            visited.add(src);

            
            
        }
    }

    // Create a class queueItem to store source, destination, and item cost.
    // I also included a printItem() to debug.
    public static class QueueItem implements Comparable<QueueItem>{
            int itemSource;
            int itemDestination;
            int itemCost;
            public QueueItem(int itemSource, int itemDestination, int itemCost) {
                this.itemSource = itemSource;
                this.itemDestination = itemDestination;
                this.itemCost = itemCost;
            }

            public void printItem(){
                //System.out.println("Src: " + this.itemSource + " Dest: " + this.itemDestination + " Cost: " + this.itemCost);
            }

            @Override
            public int compareTo(QueueItem other) {
                // Compare based on the cost 
                return Integer.compare(this.itemCost, other.itemCost);
            }
        }
    


    
}
