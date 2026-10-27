package SlotSim;

import java.io.IOException;
import java.util.*;

public class Simulator {

    private static final long ROUNDS = 10_000_000;


    static void main(String[] args) throws IOException {
        GameEngine game = new GameEngine();
        Board board = new Board(5,3);
        Random rng = new Random();


        Map<Symbol, Double> map1 = ConfigLoader.loadWeights("set1.json");
        Map<Symbol, Double> map2 = ConfigLoader.loadWeights("set2.json");
        Map<Symbol, List<Double>> table1 = ConfigLoader.loadPaytable("set1.json");

        ReelWeights reel1 = new ReelWeights(map1);
        ReelWeights reel2 = new ReelWeights(map2);

        List<ReelWeights> reels = new ArrayList<>();
        reels.add(reel1);
        reels.add(reel2);
        reels.add(reel2);
        reels.add(reel2);
        reels.add(reel2);

        Map<Symbol, Integer> collectedSymbols = new EnumMap<>(Symbol.class);

        double totalWin = 0;

        double cost = 1;

        double totalBet = ROUNDS * cost;

        long reportEvery = Math.max(1, ROUNDS / 100);   // every 1%
        long start = System.nanoTime();

        for (long i = 0; i < ROUNDS; i++) {

//            Result result = game.playFreeSpin(board, reels, table1, rng, true);

            Result result = game.playSpin(board,reels,table1,rng,1,0,false,false,collectedSymbols);

            totalWin += result.win();

            if ((i + 1) % reportEvery == 0) {
                printProgress(i + 1, ROUNDS, start, totalWin, cost);
            }
        }
        System.out.println();   // move to a new line after the progress line

        double rtp = SlotMath.rtp(totalWin,totalBet);
        System.out.println("final rtp = " + rtp);
    }



    private static void printProgress(long done, long total, long startNanos,
                                      double totalWin, double costPerRound) {
        double elapsedSec = (System.nanoTime() - startNanos) / 1e9;
        double fraction   = (double) done / total;
        double leftSec    = elapsedSec / fraction - elapsedSec;
        double rtpSoFar   = totalWin / (done * costPerRound) * 100;

        System.out.printf("\r%5.1f%%  %,d / %,d  elapsed %s  left ~%s  RTP so far %.3f%%",
                fraction * 100, done, total, formatTime(elapsedSec), formatTime(leftSec), rtpSoFar);
    }

    private static String formatTime(double seconds) {
        long s = (long) seconds;
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}
