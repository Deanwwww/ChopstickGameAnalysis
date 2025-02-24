package org.example;
import java.util.*;

public class GameGraph {
    private Set<GameState> nodes;
    private Map<GameState, List<GameState>> graph;
    private GameState startState = new GameState(new int[]{1,1}, new int[]{1,1});
    private GameState endState = new  GameState(new int[]{-1,-1}, new int[]{-1,-1});

    public GameGraph() {
        nodes = new HashSet<>();
        graph = new HashMap<>();

        // ADD every Nodes (GameStates)
        for(int i=0; i<5; i++)
            for (int j=0; j<5; j++)
                for(int k=0; k<5; k++)
                    for(int l=0; l<5; l++) {
                        GameState newNode = new GameState(new int[]{i, j}, new int[]{k, l});
                        nodes.add(newNode);
                    }

        // ADD every Edges (Action)
        for(GameState currNode : nodes) {
            for (GameState nextNode : currNode.possibleActions()){
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(nextNode);
            }
        }

        // ADD end Edges
        for(GameState currNode : nodes) {
            if(currNode.currentPlayer[0]==0 &&  currNode.currentPlayer[1]==0) {
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(endState);
            }
        }


    }

    public void analysis(){
        //TODO : run graph search algorithm here
    }

    public void report(){
        System.out.println("< TOTAL GRAPH >");
        for(GameState currNode : nodes) {
            System.out.print(currNode);
            System.out.print(" -> ");
            System.out.println(graph.get(currNode));
        }
    }
}
