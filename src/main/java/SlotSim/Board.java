package SlotSim;

import java.util.*;

public class Board {
    public final int reelCount;
    public final int rowCount;
    public final Symbol[][] cells; // board[reel][row], row 0 = top
    public final boolean[][] sticky;




    public Board(int reelCount,int rowCount){
        this.reelCount = reelCount;
        this.rowCount = rowCount;
        this.cells = new Symbol[reelCount][rowCount];
        this.sticky = new boolean[reelCount][rowCount];

    }


    public void set(int reel,int row, Symbol symbol){
        cells[reel][row] = symbol;
    }

    public Symbol get(int reel,int row){
        return cells[reel][row];
    }

    public boolean isSticky(int reel, int row) {
        return sticky[reel][row];
    }

    public void setSticky(int reel, int row, boolean locked) {
        sticky[reel][row] = locked;
    }

    /** Unlocks every cell (start of every round and every bonus). */
    public void clearSticky() {
        for (boolean[] reel : sticky) {
            Arrays.fill(reel, false);
        }
    }



    public void fill(List<ReelWeights> reelsWeights, Random rng){
        for (int row = 0; row < rowCount; row++){
            for (int reel = 0; reel < reelCount; reel++){
                if (isSticky(reel,row)){
                    continue;
                }
                set(reel,row, reelsWeights.get(reel).drawSymbol(rng));
            }
        }
    }

    public boolean[][] avalanche(boolean[][] cellsToRemove, Random rng, List<ReelWeights> reelsWeights) {

        boolean[][] newCells = new boolean[reelCount][rowCount];

        for (int reel = 0; reel < reelCount; reel++ ){
            List<Symbol> survivors = new ArrayList<>();
            for (int row = rowCount - 1; row >= 0; row--){
                // finds the cells that needs removal
                if (!cellsToRemove[reel][row] && !isSticky(reel,row)){
                    survivors.add(get(reel,row));
                }

                if (cellsToRemove[reel][row] && isSticky(reel,row)){
                    setSticky(reel,row,false);
                }

            }
            int next = 0;
            for (int row = rowCount - 1; row >= 0; row--){
                if (isSticky(reel,row)){
                    continue;
                }
                if (next < survivors.size()){
                    set(reel,row,survivors.get(next));
                    next++;
                }
                else{
                    set(reel,row,reelsWeights.get(reel).drawSymbol(rng));
                    newCells[reel][row] = true;
                }
            }
        }
        return newCells;
    }

    public void lockStickyWilds() {
        for (int reel = 0; reel < reelCount; reel++) {
            for (int row = 0; row < rowCount; row++) {
                if (get(reel, row) == Symbol.WILD) {
                    setSticky(reel, row, true);
                }
            }
        }
    }




    public Symbol[][] snapshot() {
        Symbol[][] copy = new Symbol[reelCount][];
        for (int reel = 0; reel < reelCount; reel++) {
            copy[reel] = cells[reel].clone();
        }
        return copy;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rowCount; row++) {
            for (int reel = 0; reel < reelCount; reel++) {
                String name = cells[reel][row] + (sticky[reel][row] ? "*" : "");
                sb.append(String.format("%-14s", name));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
