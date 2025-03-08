package org.example;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class GameGraph {
    private Set<GameState> nodes;
    private Map<GameState, List<GameState>> graph;
    private GameState winState = new GameState(new int[]{-1,-1}, new int[]{-1,-1},true);
    private GameState loseState = new GameState(new int[]{-1,-1}, new int[]{-1,-1},false);
    private GameState startState1 = new GameState(new int[]{1,1}, new int[]{1,1},true); // Us Start
    private GameState startState2 = new GameState(new int[]{1,1}, new int[]{1,1},false); // Opp Start
    private Map<GameState, Integer> ultDP = new HashMap<>();
    private  Map<GameState, Double> probabilityMemo = new HashMap<>();
    private  Set<GameState> visiting = new HashSet<>();
    

    // Creates all of the possible states and all of the edges
    public GameGraph() {
        nodes = new HashSet<>();
        graph = new HashMap<>();

        // ADD every Nodes (GameStates)
        for(int i=0; i<5; i++)
            for (int j=0; j<5; j++)
                for(int k=0; k<5; k++)
                    for(int l=0; l<5; l++) {
                        GameState newNode1 = new GameState(new int[]{i, j}, new int[]{k, l},true);
                        GameState newNode2 = new GameState(new int[]{i, j}, new int[]{k, l},false);
                        nodes.add(newNode1);
                        nodes.add(newNode2);
                    }

        // ADD every Edges (Action)
        for(GameState currNode : nodes) {
            for (GameState nextNode : currNode.possibleActions(probabilityMemo, visiting)){
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(nextNode);
            }
        }

        // ADD end Edges
        for(GameState currNode : nodes) {
            if(currNode.checkWin()==1) {
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(winState);
            } else if(currNode.checkWin()==-1) {
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(loseState);
            }
        }
    }

    // Traverse through all of the nodes and returns them in reverse.
    public Map<GameState, GameState> BFS(){
        Map<GameState, GameState> prev = new HashMap<>();
        Queue<GameState> perimeter = new LinkedList<>();
        Set<GameState> visited = new HashSet<>();

        Map<GameState, List<GameState>> reversedGraph = new HashMap<>();
        for (GameState from : graph.keySet()) {
            for (GameState to : graph.get(from)) {
                reversedGraph.computeIfAbsent(to, k -> new ArrayList<>()).add(from); // 방향 반대로 추가
            }
        }

        GameState start = winState;
        perimeter.add(start);
        visited.add(start);
        prev.put(start, null);

        while(!perimeter.isEmpty()){
            GameState currState = perimeter.poll();

            for (GameState nextState : reversedGraph.getOrDefault(currState, Collections.emptyList())){
                if(!visited.contains(nextState)){
                    perimeter.add(nextState);
                    prev.put(nextState, currState);
                    visited.add(nextState);
                }
            }

        }

        return prev;
    }

    public Map<GameState, Double> getProbabilityMemo(){
        return this.probabilityMemo;
    }

    /*
     * Iterate through the nodes and call computeProbability to compute the probabilities on each nodes
     */
    public Map<GameState, Double> computeAllProbabilities(){
        Map<GameState, Double> probabilityMemo = this.getProbabilityMemo();

        for(GameState n : nodes){
            if(!probabilityMemo.containsKey(n)){
                n.computeProbability(probabilityMemo, graph, visiting);
            }
        }

        return probabilityMemo;
    }

    // Finding every path but too long
    public List<List<GameState>> DFS(GameState start) {
        Map<GameState, List<List<GameState>>> memo = new HashMap<>();
        Set<GameState> visited = new HashSet<>();
        return DFS_recursive(start, visited, memo);
    }

    private List<List<GameState>> DFS_recursive(GameState current, Set<GameState> visited,
                                      Map<GameState, List<List<GameState>>> memo) {
        if (memo.containsKey(current)) {
            return memo.get(current);
        }

        List<List<GameState>> paths = new ArrayList<>();

        if (current.equals(winState)) {
            List<GameState> path = new ArrayList<>();
            path.add(current);
            paths.add(path);
            memo.put(current, paths);
            return paths;
        }

        visited.add(current);
        if (graph.containsKey(current)) {
            for (GameState next : graph.get(current)) {
                if (!visited.contains(next)) {
                    List<List<GameState>> subPaths = DFS_recursive(next, visited, memo);
                    for (List<GameState> subPath : subPaths) {
                        List<GameState> newPath = new ArrayList<>();
                        newPath.add(current);
                        newPath.addAll(subPath);
                        paths.add(newPath);
                    }
                }
            }
        }

        visited.remove(current);
        memo.put(current, paths);
        return paths;
    }

    
    public int countComponents(){
        int cnt=0;
        Set<GameState> visited = new HashSet<>();
        Stack<GameState> stack = new Stack<>();
        stack.push(startState1);
        visited.add(startState1);
        cnt++;

        while(!stack.isEmpty()) {
            cnt++;
            GameState currState = stack.pop();
            for (GameState nextstate : currState.possibleActions(probabilityMemo, visiting)) {
                if (!visited.contains(nextstate)) {
                    stack.push(nextstate);
                    visited.add(nextstate);
                }
            }
        }

        for(GameState node:nodes){
            if(!visited.contains(node)){
                //print not-connected nodes from start node
                //System.out.println(node);
            }
        }
        return cnt;
    }

    public void report(){
        System.out.println("< TOTAL GRAPH >");
        for(GameState currNode : nodes) {
            System.out.print(currNode);
            System.out.print(" -> ");
            System.out.println(graph.get(currNode));
        }

        //Graph Stream
        System.setProperty("org.graphstream.ui", "swing");
        Graph gs_graph = new SingleGraph("Directed Graph");
        gs_graph.setAttribute("ui.stylesheet",
                "node { fill-color: blue; size: 10px; text-size: 9;}" +
                        "edge { shape: cubic-curve; arrow-size: 5px, 4px; }");
        gs_graph.setAttribute("ui.antialias");

        for (GameState node : nodes) {
            gs_graph.addNode(node.toString());
        }
        gs_graph.addNode(winState.toString());
        gs_graph.addNode(loseState.toString());

        for (GameState node : nodes) {
            for(GameState nextNode : graph.get(node)){
                gs_graph.addEdge(node.toString()+" -> "+nextNode.toString(),
                        node.toString(),nextNode.toString(),true);
            }
        }

        for (Node node : gs_graph) {
            String id = node.getId();
            node.setAttribute("ui.label", id);
            if(Objects.equals(id, winState.toString())){
                node.setAttribute("ui.style", "fill-color: rgb(0,0,255);");
                node.setAttribute("xyz",15,35,0);
            }
            else if(Objects.equals(id, loseState.toString())){
                node.setAttribute("ui.style", "fill-color: rgb(0,0,0);");
                node.setAttribute("xyz",50,35,0);
            }
            else {
                GameState st = parsing(id);
                int x = 6*st.currentPlayer[0]+st.currentPlayer[1];
                int y = 6*st.opponentPlayer[0]+st.opponentPlayer[1];
                if(!st.myTurn){
                    x+=36;
                }
                node.setAttribute("xyz",x,y,0);
                if(isUlt(st)){
                    node.setAttribute("ui.style", "fill-color: rgb(255,165,0);");
                }
            }
            if(Objects.equals(id, startState1.toString()) || Objects.equals(id, startState2.toString())){
                node.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
            }
            node.setAttribute("layout.frozen", true);
        }
        Viewer viewer = gs_graph.display();
    }

    private GameState parsing(String input){
        GameState res;
        if(input.equals("[ I Win! ]")) return this.winState;
        if(input.equals("[ I Lose! ]")) return this.loseState;
        String str = input.substring(1,16);
        if(str.charAt(0)=='('){
            int a = Integer.parseInt(str.substring(1,2));
            int b = Integer.parseInt(str.substring(5,6));
            int c = Integer.parseInt(str.substring(10,11));
            int d = Integer.parseInt(str.substring(14,15));
            res = new GameState(new int[]{a,b}, new int[]{c,d},true);
        }
        else {
            int a = Integer.parseInt(str.substring(0,1));
            int b = Integer.parseInt(str.substring(4,5));
            int c = Integer.parseInt(str.substring(9,10));
            int d = Integer.parseInt(str.substring(13,14));
            res = new GameState(new int[]{c,d}, new int[]{a,b},false);
        }
        return res;
    }

    public boolean isUlt(GameState currState){
        if(!currState.myTurn) {
            System.out.println("this is not my turn");
            ultDP.put(currState,-1);
            return false;
        }
        if(currState.checkWin()==1) {
            ultDP.put(currState,1);
            return true;
        }
        if(ultDP.containsKey(currState)){
            return ultDP.get(currState)==1;
        }
        ultDP.put(currState,0);
        for(GameState nextState:currState.possibleActions(probabilityMemo, visiting)){
            boolean res = true;
            for(GameState nextNextState:nextState.possibleActions(probabilityMemo, visiting)){
                if(!isUlt(nextNextState)){
                    res = false;
                    break;
                }
            }
            if (res) {
                ultDP.put(currState,1);
                return true;
            }
        }
        ultDP.put(currState,-1);
        return false;
    }
}
