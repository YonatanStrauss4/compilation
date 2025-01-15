package IR;
import java.util.*;


public abstract class IRcommand {
	// Label Factory
	protected static int label_counter=0;
	public ArrayList<IRcommand> next = new ArrayList<IRcommand>();
	public Set<String> in = new HashSet<>();
	public Set<String> out = new HashSet<>();
	public Set<String> gen = new HashSet<>();
	public Set<String> kill = new HashSet<>();

	public static String getFreshLabel(String msg) {
		return String.format("Label_%d_%s",label_counter++,msg);
	}

	public abstract Set<String> usedVars();
	public abstract Set<String> definedVars();

	public void updateKillGen() {
		gen.addAll(definedVars());
		kill.addAll(usedVars()); 
	}

	public void printIR(){
		System.out.println("IRcommand error");
	}

}
