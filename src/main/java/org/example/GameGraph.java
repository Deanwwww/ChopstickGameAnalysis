package org.example;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import java.util.List;
import java.util.Queue;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class GameGraph {
    private Set<GameState> nodes;
    private Map<GameState, List<GameState>> graph;
    private GameState winState = new GameState(new int[]{-1, -1}, new int[]{-1, -1}, true);
    private GameState loseState = new GameState(new int[]{-1, -1}, new int[]{-1, -1}, false);
    private GameState startState1 = new GameState(new int[]{1, 1}, new int[]{1, 1}, true); // Us Start
    private GameState startState2 = new GameState(new int[]{1, 1}, new int[]{1, 1}, false); // Opp Start
    private Map<GameState, Integer> ultDP = new HashMap<>();
    private Map<GameState, Double> probabilityMemo = new HashMap<>();
    private Set<GameState> visiting = new HashSet<>();


    // Creates all of the possible states and all of the edges
    public GameGraph() {
        nodes = new HashSet<>();
        graph = new HashMap<>();

        // ADD every Nodes (GameStates)
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                for (int k = 0; k < 5; k++)
                    for (int l = 0; l < 5; l++) {
                        GameState newNode1 = new GameState(new int[]{i, j}, new int[]{k, l}, true);
                        GameState newNode2 = new GameState(new int[]{i, j}, new int[]{k, l}, false);
                        nodes.add(newNode1);
                        nodes.add(newNode2);
                    }

        // ADD every Edges (Action)
        for (GameState currNode : nodes) {
            for (GameState nextNode : currNode.possibleActions(probabilityMemo, visiting)) {
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(nextNode);
            }
        }

        // ADD end Edges
        for (GameState currNode : nodes) {
            if (currNode.checkWin() == 1) {
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(winState);
            } else if (currNode.checkWin() == -1) {
                graph.computeIfAbsent(currNode, k -> new ArrayList<>()).add(loseState);
            }
        }
    }

    // Traverse through all of the nodes and returns them in reverse.
    public Map<GameState, GameState> BFS() {
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

        while (!perimeter.isEmpty()) {
            GameState currState = perimeter.poll();

            for (GameState nextState : reversedGraph.getOrDefault(currState, Collections.emptyList())) {
                if (!visited.contains(nextState)) {
                    perimeter.add(nextState);
                    prev.put(nextState, currState);
                    visited.add(nextState);
                }
            }

        }

        return prev;
    }

    public Map<GameState, Double> getProbabilityMemo() {
        return this.probabilityMemo;
    }

    /*
     * Iterate through the nodes and call computeProbability to compute the probabilities on each nodes
     */
    public Map<GameState, Double> computeAllProbabilities() {
        Map<GameState, Double> probabilityMemo = this.getProbabilityMemo();

        for (GameState n : nodes) {
            if (!probabilityMemo.containsKey(n)) {
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


    public int countComponents() {
        int cnt = 0;
        Set<GameState> visited = new HashSet<>();
        Stack<GameState> stack = new Stack<>();
        stack.push(startState1);
        visited.add(startState1);
        cnt++;

        while (!stack.isEmpty()) {
            cnt++;
            GameState currState = stack.pop();
            for (GameState nextstate : currState.possibleActions(probabilityMemo, visiting)) {
                if (!visited.contains(nextstate)) {
                    stack.push(nextstate);
                    visited.add(nextstate);
                }
            }
        }

        for (GameState node : nodes) {
            if (!visited.contains(node)) {
                //print not-connected nodes from start node
                //System.out.println(node);
            }
        }
        return cnt;
    }

    public void report() {
        System.out.println("< TOTAL GRAPH >");
        for (GameState currNode : nodes) {
            System.out.print(currNode);
            System.out.print(" -> ");
            System.out.println(graph.get(currNode));
        }

        //Graph Stream
        System.setProperty("org.graphstream.ui", "swing");
        Graph gs_graph = new SingleGraph("Directed Graph");
        gs_graph.setAttribute("ui.stylesheet",
                "node { fill-color: grey; size: 10px; text-size: 9;}" +
                        "edge { shape: cubic-curve; arrow-size: 5px, 4px; }");
        gs_graph.setAttribute("ui.antialias");

        for (GameState node : nodes) {
            gs_graph.addNode(node.toString());
        }
        gs_graph.addNode(winState.toString());
        gs_graph.addNode(loseState.toString());

        for (GameState node : nodes) {
            for (GameState nextNode : graph.get(node)) {
                gs_graph.addEdge(node.toString() + " -> " + nextNode.toString(),
                        node.toString(), nextNode.toString(), true);
            }
        }

        for (Node node : gs_graph) {
            String id = node.getId();
            node.setAttribute("ui.label", id);
            if (Objects.equals(id, winState.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(0,0,255);");
                node.setAttribute("xyz", 15, 35, 0);
            } else if (Objects.equals(id, loseState.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(0,0,0);");
                node.setAttribute("xyz", 50, 35, 0);
            } else {
                GameState st = parsing(id);
                int x = 6 * st.currentPlayer[0] + st.currentPlayer[1];
                int y = 6 * st.opponentPlayer[0] + st.opponentPlayer[1];
                if (!st.myTurn) {
                    x += 36;
                }
                node.setAttribute("xyz", x, y, 0);
                if (probabilityMemo.get(st) >= 0.7) {
                    node.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                }
                if (isUlt(st)) {
                    node.setAttribute("ui.style", "fill-color: rgb(255,165,0);");
                }
            }
            if (Objects.equals(id, startState1.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(6,64,43);");
            } else if (Objects.equals(id, startState2.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
            }
            node.setAttribute("layout.frozen", true);
        }
        Viewer viewer = gs_graph.display();

        //simplified graph
        Graph gs_graph2 = new SingleGraph("Directed Graph");
        gs_graph2.setAttribute("ui.stylesheet",
                "node { fill-color: grey; size: 10px; text-size: 9;}" +
                        "edge { shape: cubic-curve; arrow-size: 5px, 4px; }");
        gs_graph2.setAttribute("ui.antialias");

        for (GameState node : nodes) {
            if (probabilityMemo.get(node) >= 0.5) {
                gs_graph2.addNode(node.toString());
            } else if (isUlt(node)) {
                gs_graph2.addNode(node.toString());
            }
        }
        gs_graph2.addNode(winState.toString());
        gs_graph2.addNode(loseState.toString());
        gs_graph2.addNode(startState1.toString());

        for (GameState node : nodes) {
            for (GameState nextNode : graph.get(node)) {
                if (gs_graph2.getNode(node.toString()) != null && gs_graph2.getNode(nextNode.toString()) != null) {
                    gs_graph2.addEdge(node.toString() + " -> " + nextNode.toString(),
                            node.toString(), nextNode.toString(), true);
                }
            }
        }

        for (Node node : gs_graph2) {
            String id = node.getId();
            node.setAttribute("ui.label", id);
            if (Objects.equals(id, winState.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(0,0,255);");
                node.setAttribute("xyz", 15, 35, 0);
            } else if (Objects.equals(id, loseState.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(0,0,0);");
                node.setAttribute("xyz", 50, 35, 0);
            } else {
                GameState st = parsing(id);
                int x = 6 * st.currentPlayer[0] + st.currentPlayer[1];
                int y = 6 * st.opponentPlayer[0] + st.opponentPlayer[1];
                if (!st.myTurn) {
                    x += 36;
                }
                node.setAttribute("xyz", x, y, 0);
                if (probabilityMemo.get(st) >= 0.7) {
                    node.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                }
                if (isUlt(st)) {
                    node.setAttribute("ui.style", "fill-color: rgb(255,165,0);");
                }
            }
            if (Objects.equals(id, startState1.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(6,64,43);");
            } else if (Objects.equals(id, startState2.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
            }
            node.setAttribute("layout.frozen", true);
        }
        Viewer viewer2 = gs_graph2.display();


        //simplified graph with rank
        Graph gs_graph3 = new SingleGraph("Directed Graph");
        gs_graph3.setAttribute("ui.stylesheet",
                "node { fill-color: grey; size: 10px; text-size: 9;text-color:rgba(0, 0, 0, 0);}" +
                        "node.hover { size: 30px; text-size: 18;text-color:red;text-alignment: above;}" +
                        "edge { fill-color:black; shape: cubic-curve; arrow-size: 5px, 4px;}"+
                        "edge.hover {fill-color:red; arrow-size: 7px, 5px; size:4px;}");
        gs_graph3.setAttribute("ui.antialias");

        for (GameState node : nodes) {
            if (probabilityMemo.get(node) >= 0.5) {
                gs_graph3.addNode(node.toString());
            } else if (isUlt(node)) {
                gs_graph3.addNode(node.toString());
            }
        }
        gs_graph3.addNode(winState.toString());
        gs_graph3.addNode(loseState.toString());
        gs_graph3.addNode(startState1.toString());

        for (GameState node : nodes) {
            for (GameState nextNode : graph.get(node)) {
                if (gs_graph3.getNode(node.toString()) != null && gs_graph3.getNode(nextNode.toString()) != null) {
                    gs_graph3.addEdge(node.toString() + " -> " + nextNode.toString(),
                            node.toString(), nextNode.toString(), true);
                }
            }
        }

        List<String> toRemove = new ArrayList<>();
        for (Node node : gs_graph3) {
            if (node.getInDegree() == 0) {
                toRemove.add(node.getId());
            }
        }
        for (String nodeId : toRemove) {
            if (!nodeId.equals(startState2.toString())) {
                gs_graph3.removeNode(nodeId);
            }
        }

        Map<GameState, GameState> shortestPath = this.BFS();
        int[] xcount = new int[30];
        probabilityMemo.put(winState, 0.0);
        probabilityMemo.put(loseState, 0.0);
        for (Node node : gs_graph3) {
            String id = node.getId();
            node.setAttribute("ui.label", id);
            GameState st = parsing(id);
            int dep = 1;
            GameState path_state = new GameState(st.currentPlayer, st.opponentPlayer, st.myTurn);
            if (path_state.equals(winState) || path_state.equals(loseState)) {
                dep = 2;
            } else if (path_state.equals(startState1) || path_state.equals(startState2)) {
                dep = 13;
            } else {
                while (path_state != null) {
                    dep++;
                    path_state = shortestPath.get(path_state);
                }
            }
            int x = xcount[dep] % 100;
            xcount[dep]++;
            int y = dep * 5 + xcount[dep] / 100;
            if (st.equals(winState) || st.equals(startState1)) {
                x += 33;
            } else if (st.equals(loseState) || st.equals(startState2)) {
                x += 66;
            }
            node.setAttribute("xyz", x, y, 0);
            node.setAttribute("layout.frozen", true);
            if (Objects.equals(id, winState.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(0,0,255);");
                continue;
            } else if (Objects.equals(id, loseState.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(0,0,0);");
                continue;
            }
            if (probabilityMemo.get(st) >= 0.7) {
                node.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
            }
            if (isUlt(st)) {
                node.setAttribute("ui.style", "fill-color: rgb(255,165,0);");
            }
            if (Objects.equals(id, startState1.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(6,64,43);");
            } else if (Objects.equals(id, startState2.toString())) {
                node.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
            }
        }
        Viewer viewer3 = gs_graph3.display();
        View view3 = viewer3.getDefaultView();
        view3.addMouseMotionListener(new MouseMotionAdapter() {
            private Node lastNode = null;
            @Override
            public void mouseMoved(MouseEvent e) {
                GraphicElement element = view3.findNodeOrSpriteAt(e.getX(), e.getY());
                Node currentNode = (element != null) ? gs_graph3.getNode(element.getId()) : null;
                if (lastNode != null && lastNode != currentNode) {
                    lastNode.removeAttribute("ui.class");
                    for (Edge edge : lastNode.getEachEdge()) {
                        edge.removeAttribute("ui.class");
                        Node nextNode = edge.getOpposite(lastNode);
                        nextNode.removeAttribute("ui.class");
                    }
                }
                if (currentNode != null) {
                    Node node = gs_graph3.getNode(element.getId());
                    lastNode = currentNode;
                    if (node != null) {
                        node.addAttribute("ui.class", "hover");
                        for (Edge edge : node.getEachEdge()) {
                            edge.addAttribute("ui.class", "hover");
                            Node nextNode = edge.getOpposite(node);
                            nextNode.addAttribute("ui.class", "hover");
                        }
                    }
                }
            }
        });
    }

    private GameState parsing(String input) {
        GameState res;
        if (input.equals("[ I Win! ]")) return this.winState;
        if (input.equals("[ I Lose! ]")) return this.loseState;
        String str = input.substring(1, 16);
        if (str.charAt(0) == '(') {
            int a = Integer.parseInt(str.substring(1, 2));
            int b = Integer.parseInt(str.substring(5, 6));
            int c = Integer.parseInt(str.substring(10, 11));
            int d = Integer.parseInt(str.substring(14, 15));
            res = new GameState(new int[]{a, b}, new int[]{c, d}, true);
        } else {
            int a = Integer.parseInt(str.substring(0, 1));
            int b = Integer.parseInt(str.substring(4, 5));
            int c = Integer.parseInt(str.substring(9, 10));
            int d = Integer.parseInt(str.substring(13, 14));
            res = new GameState(new int[]{c, d}, new int[]{a, b}, false);
        }
        return res;
    }

    /**
     * Determines whether the current state can be forced into a winning outcome for the current player.
     * 
     * - If it's not the current player's turn, mark the DP as -1 (losing/no guarantee) and return false immediately.
     * - If the current state is already a winning state, store 1 in the DP and return true.
     * - If we've seen this state, return the seen result.
     * - Otherwise, explore each possible "nextState". For each nextState, look at its possible next-next-states.
     *   If in every next-next-state, the current player can still force a win (isUlt is true), then we can mark the
     *   current state as 1 (winning) and return true.
     * - If we exhaust all nextStates without finding a guaranteed winning path, we store -1 (not winning) and return false.
     * 
     * This method uses a depth-first style search combined with memoization (ultDP) to avoid recomputing states.
     */
    public boolean isUlt(GameState currState) {
        if (!currState.myTurn) {
            boolean res = true;
            for (GameState nextNextState : currState.possibleActions(probabilityMemo, visiting)) {
                if (!isUlt(nextNextState)) {
                    res = false;
                    break;
                }
            }
            if (res) {
                ultDP.put(currState, 1);
                return true;
            }
            ultDP.put(currState, -1);
            return false;
        }
        if (currState.checkWin() == 1) {
            ultDP.put(currState, 1);
            return true;
        }
        if (ultDP.containsKey(currState)) {
            return ultDP.get(currState) == 1;
        }
        ultDP.put(currState, 0);
        for (GameState nextState : currState.possibleActions(probabilityMemo, visiting)) {
            boolean res = true;
            for (GameState nextNextState : nextState.possibleActions(probabilityMemo, visiting)) {
                if (!isUlt(nextNextState)) {
                    res = false;
                    break;
                }
            }
            if (res) {
                ultDP.put(currState, 1);
                return true;
            }
        }
        ultDP.put(currState, -1);
        return false;
    }
}