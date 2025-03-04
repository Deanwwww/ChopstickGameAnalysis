package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        GameGraph game = new GameGraph();
        Map<GameState, GameState> shortestPath = game.BFS();
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