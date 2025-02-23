package org.example;
import java.util.*;

public class GameGraph {
    private Set<GameState> nodes;
    private Map<GameState, List<GameState>> graph;
    public GameGraph() {
        nodes = new HashSet<>();
        graph = new HashMap<>();
        // TODO : ADD every Nodes (GameStates)
        //nodes.add();

        // ADD every Edges (Action)
        for(GameState currNode : nodes) {
            for (GameState nextNode : currNode.possibleActions()){
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(nextNode);
            }
        }

    }

    public void analysis(){
        //TODO : run graph search algorithm here
    }

    public void report(){
        //TODO : print interested information here
    }
}
