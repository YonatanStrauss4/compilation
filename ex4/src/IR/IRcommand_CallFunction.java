package IR;
import java.util.*;

import TEMP.*;

public class IRcommand_CallFunction extends IRcommand {
    String funcName;
    TEMP args;

    public IRcommand_CallFunction(String funcName, TEMP args) {
        this.funcName = funcName;
        this.args = args;
    }

	public Set<String> usedVars() {
		return new HashSet<>(Arrays.asList(args.toString()));
	}

	public Set<String> definedVars() {
		return new HashSet<>();
	}

	public void printIR(){
		System.out.println("call function: " + funcName + "(" + args.toString() + ")");
	}
}
