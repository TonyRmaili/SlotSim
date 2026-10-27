package SlotSim;

import java.util.Map;
import java.util.Random;

public class ReelWeights {

    private final Symbol[] symbols;
    public final double total;
    private final double[] weights;


    public ReelWeights(Map<Symbol, Double> weightsBySymbol) {
        symbols = new Symbol[weightsBySymbol.size()];
        weights = new double[weightsBySymbol.size()];

        double sum = 0;
        int i = 0;

        for (Map.Entry<Symbol, Double> entry : weightsBySymbol.entrySet()) {

            if (entry.getValue() < 0){
                throw new IllegalArgumentException("Negative weight for " + entry.getKey() + ": " + entry.getValue());
            }

            symbols[i] = entry.getKey();
            weights[i] = entry.getValue();

            sum += entry.getValue();
            i++;
        }

        if (sum <= 0){
            throw new IllegalArgumentException("Weight total  " + sum + " less than or equal 0");
        }

        this.total = sum;

    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbols.length; i++){
            sb.append(symbols[i]).append("=").append(weights[i]).append("\n");

        }
        sb.append("total=").append(total);
        return sb.toString();
    }


    public Symbol drawSymbol(Random rng) {

        double roll = rng.nextDouble() * total;

        double counter = 0;
        int weightIndex = weights.length -1; // in case we move outside the range defaults to last symbol

        for (int i = 0; i < weights.length; i++){
            counter += weights[i];
            if (roll < counter){
                weightIndex =  i;
                break;
            }
        }
        return symbols[weightIndex];
    }

}
