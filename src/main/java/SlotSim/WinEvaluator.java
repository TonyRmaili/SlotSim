package SlotSim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WinEvaluator {
    public static final int MIN_WIN_LENGTH = 3;

    public List<Win> findWins(Board board, Map<Symbol, List<Double>> paytable) {
        List<Win> wins = new ArrayList<>();

        for (Symbol symbol : Symbol.values()){
            List<Double> pays = paytable.get(symbol);

            if (pays == null){
                continue;
            }

            List<Integer> ways = new ArrayList<>();
            int winningReelPosition = 0;

            boolean[][] cells = new boolean[board.reelCount][board.rowCount];

            for (int reel = 0; reel < board.reelCount; reel++){
                int symbolCounter = 0;
                for (int row = 0; row < board.rowCount; row++){
                    if (symbol == board.get(reel,row) || board.get(reel,row) == Symbol.WILD){  // wild cant land on reel1 anyway
                        symbolCounter += 1;
                        cells[reel][row] = true;
                    }
                }
                if (symbolCounter == 0 ){
                    break;
                }
                winningReelPosition++;
                ways.add(symbolCounter);
            }

            if (winningReelPosition >= MIN_WIN_LENGTH){
                int positionMultiplier = 1;

                for (int way : ways) {
                    positionMultiplier *= way;
                }
                double paysMultiplier = pays.get(winningReelPosition - MIN_WIN_LENGTH);
                double totalPay = paysMultiplier * positionMultiplier;
                Win win = new Win(
                        symbol,
                        winningReelPosition,
                        positionMultiplier,
                        paysMultiplier,
                        totalPay,
                        cells
                );
                wins.add(win);
            }
        }
        wins.sort((a, b) -> Double.compare(b.totalPay, a.totalPay));
        return wins;
    }

    public boolean[][] cellsToRemove(Board board, List<Win> wins) {
        boolean[][] cells = new boolean[board.reelCount][board.rowCount];
        for (Win win : wins){
            for (int reel = 0; reel < board.reelCount; reel++){
                for (int row = 0;row < board.rowCount; row++){
                    if (win.includes(reel,row)){
                        cells[reel][row] = true;
                    }
                }
            }
        }
        return cells;

    }

    public int countScatters(Symbol[][] grid) {
        int scatterCount = 0;
        for (int reel = 0; reel < grid.length; reel++) {
            for (int row = 0; row < grid[reel].length; row++) {
                Symbol cell = grid[reel][row];
                if (cell == Symbol.SCATTER){
                    scatterCount++;
                }
            }
        }
        return scatterCount;
    }


    public static final class Win {

        public final Symbol symbol;
        public final int reelCount;      // how many reels in a row matched
        public final int ways;           // number of ways this win pays on
        public final double payPerWay;   // from the paytable, x bet
        public final double totalPay;    // the whole win, x bet (before any free spins multiplier)
        private final boolean[][] cells; // cells[reel][row] = true if that cell is part of this win

        public Win(Symbol symbol, int reelCount, int ways, double payPerWay, double totalPay, boolean[][] cells) {
            this.symbol = symbol;
            this.reelCount = reelCount;
            this.ways = ways;
            this.payPerWay = payPerWay;
            this.totalPay = totalPay;
            this.cells = cells;
        }


        public boolean isWin(){
            if (totalPay > 0){
                return true;
            }
            return false;
        }


        public boolean includes(int reel, int row) {
            return cells[reel][row];
        }


        @Override
        public String toString() {
            return symbol + " x" + reelCount + ", " + ways + " ways, " + totalPay + "x bet";
        }

    }


}
