package org.example;
import java.util.*;

public class GameState {
    public int[] currentPlayer;
    public int[] opponentPlayer;
    public boolean myTurn;

    public GameState(int[] currentPlayer, int[] opponentPlayer, boolean myTurn) {
        this.currentPlayer = Arrays.copyOf(currentPlayer, currentPlayer.length);
        this.opponentPlayer = Arrays.copyOf(opponentPlayer, opponentPlayer.length);
        this.myTurn = myTurn;
    }

    // Win : return 1
    // Lose : return -1
    // Neither : return 0
    public int checkWin()
    {
        if(myTurn) {
            if(currentPlayer[0] == 0 && currentPlayer[1] == 0) return -1;
            else if (opponentPlayer[0] == 0 && opponentPlayer[1] == 0) return 1;
            else return 0;
        }
        else{
            if (opponentPlayer[0] == 0 && opponentPlayer[1] == 0) return -1;
            else if(currentPlayer[0] == 0 && currentPlayer[1] == 0) return 1;
            else return 0;
        }
    }

    // returns all the possible actions at each state
    public List<GameState> possibleActions(){
        List<GameState> actions = new ArrayList<>();
        // attack
        for(int i=0; i<2; i++) {
            for (int j = 0; j < 2; j++) {
                int[] oppo = new int[]{this.opponentPlayer[0], this.opponentPlayer[1]};
                int[] curr = new int[]{this.currentPlayer[0], this.currentPlayer[1]};
                if(curr[j]==0) continue;
                if(oppo[i]==0) continue;
                oppo[i] += curr[j];
                if (oppo[i]>4) oppo[i]=0;

                GameState next = new GameState(oppo, curr,!this.myTurn);
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

            GameState next = new GameState(oppo, curr,!this.myTurn);
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
                Arrays.equals(this.opponentPlayer, other.opponentPlayer) &&
                this.myTurn == other.myTurn;
    }
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(currentPlayer), Arrays.hashCode(opponentPlayer))
                + Boolean.hashCode(myTurn);
    }

    @Override
    public String toString() {
        if(myTurn) {
            if (currentPlayer[0]==-1)
                return "[ I Win! ]";
            return "[(" + this.currentPlayer[0] + " , " + this.currentPlayer[1] + ") / "
                    + this.opponentPlayer[0] + " , " + this.opponentPlayer[1] + "]";
        }
        else {
            if (currentPlayer[0]==-1)
                return "[ I Lose! ]";
            return "[" + this.opponentPlayer[0] + " , " + this.opponentPlayer[1] + " / ("
                    + this.currentPlayer[0] + " , " + this.currentPlayer[1] + ")]";
        }
    }
}
