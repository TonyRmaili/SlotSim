package SlotSim;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigLoader {
    // paths
    private static final String WEIGHTS_PATH = "src/main/resources/weights/";
    private static final String PAY_TABLE_PATH = "src/main/resources/paytables/";


    static public Map<Symbol, Double> loadWeights(String filename) throws IOException {
        Gson gson = new Gson();

        Type type = new TypeToken<LinkedHashMap<String, Double>>() {}.getType();

        Map<String, Double> stringMap;
        Map<Symbol, Double> symbolMap = new EnumMap<>(Symbol.class);

        String filepath =  WEIGHTS_PATH + filename;

        try (FileReader reader = new FileReader(filepath)){
            stringMap = gson.fromJson(reader, type);
        }

        for (Map.Entry<String, Double> entry : stringMap.entrySet()){
            Symbol symbol = Symbol.valueOf(entry.getKey());
            symbolMap.put(symbol, entry.getValue());
        }

        return symbolMap;
    }


    static public Map<Symbol, List<Double>> loadPaytable(String filename) throws IOException {
        Gson gson = new Gson();

        Type type = new TypeToken<LinkedHashMap<String, List<Double>>>() {}.getType();
        Map<String, List<Double>> stringMap;
        Map<Symbol, List<Double>> symbolMap = new EnumMap<>(Symbol.class);

        String filepath =  PAY_TABLE_PATH + filename;

        try (FileReader reader = new FileReader(filepath)){
            stringMap = gson.fromJson(reader, type);
        }

        for (Map.Entry<String, List<Double>> entry : stringMap.entrySet()){
            Symbol symbol = Symbol.valueOf(entry.getKey());
            symbolMap.put(symbol, entry.getValue());
        }
        return symbolMap;
    }


}
