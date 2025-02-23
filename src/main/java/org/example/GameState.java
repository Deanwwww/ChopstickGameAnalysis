package org.example;
import java.util.*;

public class GameState {
    public int[] currentPlayer;
    public int[] opponentPlayer;
    public GameState(int[] currentPlayer, int[] opponentPlayer) {
        this.currentPlayer = Arrays.copyOf(currentPlayer, currentPlayer.length);
        this.opponentPlayer = Arrays.copyOf(opponentPlayer, opponentPlayer.length);
    }

    private boolean checkLose()
    {
        //TODO
        return false;
    }

    public List<GameState> possibleActions(){
        List<GameState> actions = new ArrayList<>();
        //TODO
        return actions;
    }

    // Overriding for HashSet/HashMap
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        GameState other = (GameState) obj;
        return Arrays.equals(this.currentPlayer, other.currentPlayer) &&
                Arrays.equals(this.opponentPlayer, other.opponentPlayer);
    }
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(currentPlayer), Arrays.hashCode(opponentPlayer));
    }
}
