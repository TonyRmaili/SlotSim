package SlotSim;

import java.util.*;

public class GameEngine {
    WinEvaluator winEval = new WinEvaluator();

    public Result playSpin(
            Board board,
            List<ReelWeights> reels,
            Map<Symbol, List<Double>> paytable,
            Random rng,
            int multiplier,
            int multiplierStep,
            boolean shatterEnabled,
            boolean isSuper,
            Map<Symbol, Integer> collectedSymbols
    ){

        board.fill(reels,rng);

        List<WinEvaluator.Win> wins = winEval.findWins(board,paytable);

        double rowWin = 0;
        if (isSuper){
            board.lockStickyWilds();

            for( int row = 0; row < board.rowCount; row++ ){

                rowWin += Features.uniqueGemsInRowPay(board,row,paytable) * multiplier;
            }
        }

         Result result = playAvalanche(
                board,reels,paytable,rng,
                multiplier,multiplierStep,
                shatterEnabled,isSuper,
                collectedSymbols,wins
        );



        return new Result(result.win() + rowWin, result.endMultiplier());

    }

    public Result playAvalanche(
            Board board,
            List<ReelWeights> reels,
            Map<Symbol, List<Double>> paytable,
            Random rng,
            int multiplier,
            int multiplierStep,
            boolean shatterEnabled,
            boolean isSuper,
            Map<Symbol, Integer> collectedSymbols,
            List<WinEvaluator.Win> wins
    ){


        double totalWin = 0;
        double rowWin = 0;

        while (!wins.isEmpty()){

            for (WinEvaluator.Win win : wins){
                totalWin += win.totalPay * multiplier;
            }

            boolean[][] cellsToRemove = winEval.cellsToRemove(board,wins);

            if (shatterEnabled){
                Map<Symbol, Integer> stepCollected = Features.shatterCollect(board, cellsToRemove, paytable);
                for (Map.Entry<Symbol, Integer> entry : stepCollected.entrySet()) {
                    collectedSymbols.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }

                // loop symbols and pay
                for ( Map.Entry<Symbol, Integer> entry : collectedSymbols.entrySet()) {
                    // evaluate in a loop ?
                    if (entry.getValue() >= Features.SHATTER_METER_SIZE){
                        totalWin += Features.shatterPay(entry.getKey(),multiplier,paytable);
                        entry.setValue(entry.getValue() - Features.SHATTER_METER_SIZE);
                    }
                }
            }

            board.avalanche(cellsToRemove,rng,reels);
            wins = winEval.findWins(board,paytable);

            if (isSuper){
                board.lockStickyWilds();

                for( int row = 0; row < board.rowCount; row++ ){
                    rowWin += Features.uniqueGemsInRowPay(board,row,paytable) * multiplier;
                }
            }
            multiplier += multiplierStep;
        }

        return new Result(totalWin + rowWin, multiplier);
    }


    public Result playFreeSpin(
            Board board,
            List<ReelWeights> reels,
            Map<Symbol, List<Double>> paytable,
            Random rng,
            boolean isSuper
    ){
        board.clearSticky();

        int remainingSpins = Features.FREE_SPINS_AWARDED;
        double totalWin = 0;

        Map<Symbol, Integer> collectedSymbols = new EnumMap<>(Symbol.class);
        int multiplier  = Features.startMultiplier();

        while ( remainingSpins > 0){
            remainingSpins--;

            Result result = playSpin(board,reels,paytable,rng,multiplier ,1,true,isSuper,collectedSymbols);
            totalWin += result.win();
            multiplier = result.endMultiplier();

            if ( winEval.countScatters(board.snapshot()) >= Features.RETRIGGER_SCATTERS ){
                remainingSpins += Features.RETRIGGER_SPINS;
            }
        }

        return new Result(totalWin, multiplier);
    }
}

