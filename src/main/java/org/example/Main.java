package org.example;

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
        System.out.println(" loose: " + loose);
        System.out.println(" win: " + win);
        System.out.println(" 0.5: " + count0_5);
        System.out.println(" avg win percentage: " + avg);
        System.out.println("high prob win states more than 70%: " + highProbWin);
        double winPercentage = (double)win / count;
        System.out.println("Win Percentage: " + winPercentage);
        System.out.println("loose Percentage: " + ((double)loose / count));
        System.out.println("tie Percentage: " + ((double)count0_5 / count));
        System.out.println("total States: " + count);
        game.report();

        int cnt = game.countComponents();
        System.out.println("\nconnected nodes =  " + cnt);

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