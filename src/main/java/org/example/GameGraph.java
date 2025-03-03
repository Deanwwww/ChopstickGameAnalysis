package org.example;
import java.util.*;

public class GameGraph {
    private Set<GameState> nodes;
    private Map<GameState, List<GameState>> graph;
    private GameState winState = new GameState(new int[]{-1,-1}, new int[]{-1,-1},true);
    private GameState loseState = new GameState(new int[]{-1,-1}, new int[]{-1,-1},false);

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
            for (GameState nextNode : currNode.possibleActions()){
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


    public void report(){
        System.out.println("< TOTAL GRAPH >");
        for(GameState currNode : nodes) {
            System.out.print(currNode);
            System.out.print(" -> ");
            System.out.println(graph.get(currNode));
        }
    }
}
