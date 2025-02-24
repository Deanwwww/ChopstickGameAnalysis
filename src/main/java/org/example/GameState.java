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
        return currentPlayer[0] == 0 && currentPlayer[1] == 0;
    }

    public List<GameState> possibleActions(){
        List<GameState> actions = new ArrayList<>();
        // attack
        for(int i=0; i<2; i++) {
            for (int j = 0; j < 2; j++) {
                int[] oppo = new int[]{this.opponentPlayer[0], this.opponentPlayer[1]};
                int[] curr = new int[]{this.currentPlayer[0], this.currentPlayer[1]};
                if(curr[j]==0) continue;
                oppo[i] += curr[j];
                if (oppo[i]>4) oppo[i]=0;

                GameState next = new GameState(oppo, curr);
                actions.add(next);
            }
        }

        // split
        int split = this.currentPlayer[0]+this.currentPlayer[1];
        for(int i=0; i<=split; i++) {
            if(i>4 || split-i>4) continue;
            if(i*(split-i) == this.currentPlayer[0]*this.currentPlayer[1]) continue;
            int[] oppo = new int[]{this.opponentPlayer[0], this.opponentPlayer[1]};
            int[] curr = new int[]{i, split-i};

            GameState next = new GameState(oppo, curr);
            actions.add(next);
        }

        //check duplicate
        Set<GameState> set = new LinkedHashSet<>(actions);
        List<GameState> uniqueActions = new ArrayList<>(set);

        return uniqueActions;
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

    @Override
    public String toString() {
        return "["+this.currentPlayer[0] + " , "+this.currentPlayer[1] + " / "
                + this.opponentPlayer[0] + " , "+this.opponentPlayer[1]+"]";
    }
}
