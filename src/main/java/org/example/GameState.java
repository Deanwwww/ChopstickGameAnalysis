package org.example;
import java.util.*;

public class GameState {
    public int[] currentPlayer;
    public int[] opponentPlayer;
    public boolean myTurn;
    //public double probability;

   
    /*
     * Constructor, includes the hands of the current player and opponent player
     * note that current player may be the opponent player.
     * 
     * myTurn specifies the turn of the player
     * 
     * probabilityMemo determines the probability at each state by calling proability method
     */
    public GameState(int[] currentPlayer, int[] opponentPlayer, boolean myTurn) {
        this.currentPlayer = Arrays.copyOf(currentPlayer, currentPlayer.length);
        this.opponentPlayer = Arrays.copyOf(opponentPlayer, opponentPlayer.length);
        this.myTurn = myTurn;
        //this.probability = probability(probabilityMemo, visiting);
    }

    /*
     * computes the probability of this current state
     */
    public double computeProbability(Map<GameState, Double> probabilityMemo, 
                                    Map<GameState, List<GameState>> graph,
                                    Set<GameState> visiting
                                     ){
         // If we've already computed this state's probability, return it.
         if(probabilityMemo.containsKey(this)) {
            return probabilityMemo.get(this);
        }

        if(visiting.contains(this)){
            return 0.5;
        }

        visiting.add(this);

        // Terminal state checks:
        int winCheck = checkWin();
        if(winCheck == 1){
            probabilityMemo.put(this, 1.0);
            return 1.0;
        } else if(winCheck == -1){
            probabilityMemo.put(this, 0.0);
            return 0.0;
        }

        List<GameState> nextStates = graph.getOrDefault(this, Collections.emptyList());
        double result = 0.0;
        for(GameState nextState : nextStates){
            result += nextState.computeProbability(probabilityMemo, graph, visiting); // Recursive call uses memoized results if available.
        }
        result = (nextStates.isEmpty()) ? 0.0 : (result / nextStates.size());

        visiting.remove(this);
        // Store the computed probability for this state.
        probabilityMemo.put(this, result);

        return result;
    }

    // Win : return 1
    // Lose : return -1
    // Neither : return 0
    public int checkWin(){
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
    public List<GameState> possibleActions(Map<GameState, Double> probabilityMemo, Set<GameState> visiting){
        // Stores the possible actions
        List<GameState> actions = new ArrayList<>();
        // attack
        for(int i=0; i<2; i++) {
            for (int j = 0; j < 2; j++) {
                // Creates the new state for each type of attacks
                int[] oppo = new int[]{this.opponentPlayer[0], this.opponentPlayer[1]};
                int[] curr = new int[]{this.currentPlayer[0], this.currentPlayer[1]};
                // If the current hand of the opponent or your is zero skip any attacks
                if(curr[j]==0) continue;
                if(oppo[i]==0) continue;
                oppo[i] += curr[j]; // commence the attack
                if (oppo[i]>4) oppo[i]=0; // kill the hand that has 5 fingers

                // create a new GameState with the new parameters
                GameState next = new GameState(oppo, curr, !this.myTurn);
                // Add it to the list of possible actions
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
