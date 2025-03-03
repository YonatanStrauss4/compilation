package IR;
import java.util.*;


public abstract class IRcommand {
	// Label Factory
	protected static int label_counter=0;
	public int lineNumber;
	public ArrayList<IRcommand> next = new ArrayList<IRcommand>();
	public ArrayList<IRcommand> prev = new ArrayList<IRcommand>();


	public static String getFreshLabel(String msg) {
		return String.format("Label_%d_%s",label_counter++,msg);
	}


	public void printIR(){
		System.out.println("Error in printIR(), this message from abstract IRcommand");
	}

	public abstract void MIPSme();

}
