package IR;
import java.util.*;

public class IRcommand{
    // Label Factory
    protected static int label_counter = 0;
    public int lineNumber;
    public ArrayList<IRcommand> next = new ArrayList<>();
    public ArrayList<IRcommand> prev = new ArrayList<>();

    // Liveness Analysis Sets
    public Set<String> def = new HashSet<>();  // Variables defined in this instruction
    public Set<String> use = new HashSet<>();  // Variables used in this instruction
    public Set<String> inSet = new HashSet<>();   // Live-in variables
    public Set<String> outSet = new HashSet<>();  // Live-out variables

	public IRcommand() {
        
    }
    public static String getFreshLabel(String msg) {
        return String.format("Label_%d_%s", label_counter++, msg);
    }

    public void printIR() {
        System.out.println("IR Command: def=" + def + ", use=" + use + ", in=" + inSet + ", out=" + outSet);
    }

    public void MIPSme(){};
}
