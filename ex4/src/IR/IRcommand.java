package IR;
import java.util.*;


public abstract class IRcommand {
	// Label Factory
	protected static int label_counter=0;
	public int lineNumber;
	public boolean isBinop = false;
	public ArrayList<IRcommand> next = new ArrayList<IRcommand>();
	public ArrayList<IRcommand> prev = new ArrayList<IRcommand>();
	public Set<String> in = new HashSet<>();
	public Set<String> out = new HashSet<>();
	public Set<String> gen = new HashSet<>();
	public Set<String> kill = new HashSet<>();

	public static String getFreshLabel(String msg) {
		return String.format("Label_%d_%s",label_counter++,msg);
	}

	public abstract Set<String> genVars();
	public abstract Set<String> killVars(int maxLine);

	public void updateKillGen(int maxLine) {
		gen.addAll(genVars());
		kill.addAll(killVars(maxLine)); 
	}

	public void printIR(){
		System.out.println("Error in printIR(), this message from abstract IRcommand");
	}

}
