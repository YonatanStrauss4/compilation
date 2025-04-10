package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_CallFunction extends IRcommand {
	
    String funcName;
    TEMP args;
	int lineNumber;

    public IRcommand_CallFunction(String funcName, TEMP args,int line) {
        this.funcName = funcName;
        this.args = args;
		this.lineNumber = line;
    }

	/**
	 * Generates a set of variables generated by this command.
	 * 
	 * @return An empty set as no variables are generated in function calls.
	 */
	public Set<String> genVars() {
		return new HashSet<>();
	}

	/**
	 * Generates a set of variables killed by this command.
	 * 
	 * @return An empty set as no variables are killed in function calls.
	 */
	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

	public void printIR(){
		System.out.println("call function: " + funcName + "(" + args.toString() + ")");
	}
}
