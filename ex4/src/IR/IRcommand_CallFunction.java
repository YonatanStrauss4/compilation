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

	public Set<String> genVars() {
		return new HashSet<>();
	}

	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

	public void printIR(){
		System.out.println("call function: " + funcName + "(" + args.toString() + ")");
	}
}
