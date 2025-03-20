package IR;
import java.util.*;
import MIPS.*;

public class IRcommand_EndFunction extends IRcommand {

	String label_name;
	String funcName;
	boolean isMethod;
	String className;
	int lineNumber;
	
	public IRcommand_EndFunction(String label_name, String funcName, boolean isMethod, String className, int line)
	{
		this.label_name = label_name;
		this.isMethod = isMethod;
		this.className = className;
		this.funcName = funcName;
		this.lineNumber = line;
		IR.getInstance().controlGraph.computeLiveness();
	}


	public void printIR() {
		if (isMethod) {
			System.out.println(className + "_" + label_name + ":");
		} else {
			System.out.println(label_name + ":");
		}
	}

	public void MIPSme(){	
		if (isMethod) {
			MIPSGenerator.getInstance().function_epilogue(className + "_" + funcName);
		}
		else{
        	MIPSGenerator.getInstance().function_epilogue(funcName);
		}
    }
}
