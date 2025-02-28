package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        GameGraph game = new GameGraph();
        Map<GameState, GameState> shortestPath = game.analysis();
        game.report();

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("\ninput state [Ex : 1 1 1 1 ] ");
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            int c = scanner.nextInt();
            int d = scanner.nextInt();
            GameState state = new GameState(new int[]{a,b}, new int[]{c,d});

            //get shortest path from 'prev'
            List<GameState> path = new LinkedList<>();
            while (state!=null) {
                path.add(0,state);
                state = shortestPath.get(state);
            }

            System.out.println("shortest path : ");
            Collections.reverse(path);
            for (GameState gs : path) {
                System.out.print(gs);
                System.out.print(" ");
            }
        }
    }
}