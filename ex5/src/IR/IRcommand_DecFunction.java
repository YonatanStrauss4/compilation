package IR;
import java.util.*;
import MIPS.*;

public class IRcommand_DecFunction extends IRcommand {

	String label_name;
	public String funcName;
	boolean isMethod;
	String className;
    int numOfLocals;
	int lineNumber;
	
	public IRcommand_DecFunction(String label_name, int numOfLocals, String funcName, boolean isMethod, String className, int line)
	{
		this.label_name = label_name;
		this.funcName = funcName;
		this.isMethod = isMethod;
		this.className = className;
		this.numOfLocals = numOfLocals;
		this.lineNumber = line;
	}
	public void updateNumOfLocals(int n){
		numOfLocals = n;
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
			MIPSGenerator.getInstance().function_prologue(className + "_" + funcName, numOfLocals*4);
		}
		else{
        	MIPSGenerator.getInstance().function_prologue(funcName, numOfLocals*4);
		}
    }
}
