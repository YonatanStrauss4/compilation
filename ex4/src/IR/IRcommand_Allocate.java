
package IR;
import java.util.*;

public class IRcommand_Allocate extends IRcommand {
	
	String var_name;
	int lineNumber;
	
	public IRcommand_Allocate(String var_name,int line) {
		this.var_name = var_name;
		this.lineNumber = line;
	}

	/**
	 * Generates a set of variables used in this command.
	 * 
	 * @return An empty set as no variables are used in allocation.
	 */
	public Set<String> genVars() {
		return new HashSet<>();
	}

	/**
	 * Generates a set of variables killed by this command.
	 * 
	 * @param maxLine The maximum line number to consider.
	 * @return An empty set as no variables are killed in allocation.
	 */
	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		System.out.println("allocate " + var_name);
	}
}
