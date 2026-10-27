package SlotSim;

import java.util.*;

public class Features {
    public static final int FREE_SPINS_SCATTERS = 3;
    public static final int SUPER_SCATTERS = 4;
    public static final int FREE_SPINS_AWARDED = 10;
    public static final int RETRIGGER_SCATTERS = 3;
    public static final int RETRIGGER_SPINS = 5;
    public static final int MULTIPLIER_START = 1;
    public static final int SHATTER_METER_SIZE = 10;


    public static Map<Symbol, Integer> shatterCollect(Board board,boolean[][] removed, Map<Symbol, List<Double>> paytable){
        Map<Symbol, Integer> collectedSymbols = new EnumMap<>(Symbol.class);

        for (int reel = 0; reel < board.reelCount;reel++){
            for (int row = 0; row < board.rowCount; row++){
                if ( removed[reel][row] ){
                    Symbol symbol = board.get(reel,row);
                    if (paytable.get(symbol) != null){
                        collectedSymbols.put(symbol, collectedSymbols.getOrDefault(symbol, 0) + 1);
                    }
                }
            }
        }
        return collectedSymbols;
    }

    public static double shatterPay(Symbol symbol, int multiplier,Map<Symbol, List<Double>> paytable) {
        List<Double> pays = paytable.get(symbol);
        double highestPay = pays.getLast();

        return highestPay*multiplier;
    }

    public static double uniqueGemsInRowPay(Board board,int row,Map<Symbol, List<Double>> paytable){
        // place all symbols in a list, skip Wild and Scatter. finally check if they are unique
        List<Symbol> rowSymbols = new ArrayList<>();
        for (int reel = 0; reel < board.reelCount; reel++){
            if (board.get(reel,row) == Symbol.WILD || board.get(reel,row) == Symbol.SCATTER){
                return 0;
            }
            rowSymbols.add(board.get(reel,row));
        }
        boolean allUnique = rowSymbols.stream()
                .distinct()
                .count() == rowSymbols.size();

        // find the highest paying Symbol
        if (allUnique){
            List<Double> highestPays = new ArrayList<>(board.reelCount);
            for (Symbol symbol : rowSymbols){
                List<Double> pays = paytable.get(symbol);
                double highestPay = pays.getLast();
                highestPays.add(highestPay);
            }
            return Collections.max(highestPays);
        }
        return 0;
    }


    public static int freeSpinsAwarded(int scatterCount) {
        if (scatterCount >= FREE_SPINS_SCATTERS){
            return FREE_SPINS_AWARDED;
        }
        return 0;
    }

    public static boolean isSuperTrigger(int scatterCount) {
        if (scatterCount >= SUPER_SCATTERS){
            return true;
        }
        return false;
    }

    public int retriggerSpins(int scatterCount) {
        if (scatterCount >= RETRIGGER_SCATTERS){
            return RETRIGGER_SPINS;
        }
        return 0;
    }

    public static int startMultiplier() {
        return MULTIPLIER_START;
    }


}
