package org.example;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        GameGraph game = new GameGraph();
        Map<GameState, GameState> shortestPath = game.BFS();
        // Computes all of the probabilities for each state
        Map<GameState, Double> probabilities = game.computeAllProbabilities();
        /*
         * Prints out all of the probability results for each State
         */
        int count0_5 = 0;
        int loose = 0;
        int win = 0;
        double avg = 0.0;
        int count = 0;
        int highProbWin = 0;
        for (Map.Entry<GameState, Double> entry : probabilities.entrySet()) {
            GameState state = entry.getKey();  // The state
            Double prob = entry.getValue();    // The probability
            if(prob == 0.5){
                count0_5++;
            }
            if(prob == 0.0){
                loose++;
            }
            if(prob == 1.0){
                win++;
            }
            if(prob >= 0.7){
                highProbWin++;
            }
            avg += prob;
            count++;
            System.out.println("State: " + state + " -> Probability: " + prob);
        }
        avg /= count;
        System.out.println("No. Loose States: " + loose);
        System.out.println("No. Win States: " + win);
        System.out.println("No. Infinite Cycles: " + count0_5);
        System.out.println("Avg win percentage: " + (int)(avg * 100) + "%");
        System.out.println("Number of High Prob win states more than 70%: " + highProbWin );
        double winPercentage = (double)win / count;
        System.out.println("Win Probability: " + (int)(winPercentage * 100) + "%");
        System.out.println("Loose Probability: " + (int)(((double)loose / count)*100) + "%");
        System.out.println("Infinite Game Probability: " + (int)(((double)count0_5 / count)*100) + "%");
        System.out.println("Ratio of no. high prob / total States: " + (int)(((double)highProbWin / count) * 100) + "%");
        System.out.println("total States: " + count);
        game.report();

        int cnt = game.countComponents();
        System.out.println("\nconnected nodes =  " + cnt);

        GameState start = new GameState(new int[]{1,1}, new int[]{1,1},true);
        GameState start2 = new GameState(new int[]{1,1}, new int[]{1,1},false);
        GameState winst = new GameState(new int[]{-1, -1}, new int[]{-1, -1}, true);
        Map<GameState, List<GameState>> senario = game.optimizedSenario(start);
        //Graph Stream
        System.setProperty("org.graphstream.ui", "swing");
        Graph gs_graph = new SingleGraph("Directed Graph");
        gs_graph.setAttribute("ui.stylesheet",
                "node { fill-color: grey; size: 20px; text-size: 16;}" +
                        "edge { shape: cubic-curve; arrow-size: 5px, 4px; }");
        gs_graph.setAttribute("ui.antialias");
        gs_graph.addNode(start.toString());
        Node snode = gs_graph.getNode(start.toString());
        double sprob = game.getProbabilityMemo().get(start);
        sprob = Math.round(sprob*10000)/100.0;

        snode.setAttribute("ui.label", snode.getId()+"\n " + sprob + "%");
        snode.setAttribute("ui.style", "fill-color: rgb(6,64,43);");
        snode.setAttribute("xyz", -1, 0, 0);
        snode.setAttribute("layout.frozen", true);

        System.out.println("Optimized Senario start");
        int xpos = 0;
        while(!start.equals(winst)) {
            System.out.print(start);
            double prob = game.getProbabilityMemo().get(start);
            System.out.println(" "+prob);
            GameState nextstart = senario.get(start).get(0);
            prob = Math.round(prob*10000)/100.0;
            gs_graph.addNode(nextstart.toString());
            Node node = gs_graph.getNode(nextstart.toString());
            node.setAttribute("ui.label", node.getId() +"\n"+ " " + prob + "%");
            if(nextstart.equals(winst)){
                node.setAttribute("ui.style", "fill-color: blue;");
            }else if(game.isUlt(start)) {
                node.setAttribute("ui.style", "fill-color: rgb(255,165,0);");
            } else if(prob>=70) {
                node.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
            }
            node.setAttribute("xyz", xpos, (xpos%2)*2-2, 0);
            node.setAttribute("layout.frozen", true);
            gs_graph.addEdge(start.toString() + " -> " + nextstart.toString(),
                    start.toString(), nextstart.toString(), true);
            start = nextstart;
            xpos++;
        }
        Viewer viewer = gs_graph.display();
        System.out.println("Optimized Senario end");

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("\ninput state [Ex : 1 1 1 1 ] ");
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            int c = scanner.nextInt();
            int d = scanner.nextInt();
            
            GameState state = new GameState(new int[]{a,b}, new int[]{c,d},true);
            // OutOfMemory
            /*
            List<List<GameState>> allPath = game.DFS(state);

            System.out.println("all path : ");
            for(List<GameState> paths : allPath){
                for(GameState p : paths) {
                    System.out.print(p);
                    System.out.print(" ");
                }
                System.out.print("\n");
            }
            System.out.println("path number : " + allPath.size());
            */

            //get shortest path from 'prev'
            List<GameState> path = new LinkedList<>();
            while (state!=null) {
                path.add(0,state);
                state = shortestPath.get(state);
            }
            Collections.reverse(path);
            System.out.println("shortest path : ");
            for (GameState gs : path) {
                System.out.print(gs);
                System.out.print(" ");
            }
        }
    }
}