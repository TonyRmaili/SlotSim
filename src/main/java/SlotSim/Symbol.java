package SlotSim;


import java.util.Arrays;
import java.util.List;


public enum Symbol {
    BLACK_DIAMOND,
    DIAMOND,
    RUBY,
    OPAL,
    SAPPHIRE,
    EMERALD,
    TOPAZ,
    QUARTZ,
    WILD,
    SCATTER;

    public static List<Symbol> getList() {
        return Arrays.asList(values());
    }
}


