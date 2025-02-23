package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello");

        GameGraph game = new GameGraph();
        game.analysis();
        game.report();
    }
}