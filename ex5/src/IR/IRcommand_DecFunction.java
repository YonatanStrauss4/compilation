package IR;
import java.util.*;
import MIPS.*;

public class IRcommand_DecFunction extends IRcommand {

	String label_name;
	String funcName;
    int numOfLocals;
	int lineNumber;
	
	public IRcommand_DecFunction(String label_name, int numOfLocals, String funcName, int line)
	{
		this.label_name = label_name;
		this.funcName = funcName;
		this.numOfLocals = numOfLocals;
		this.lineNumber = line;
	}
	public void updateNumOfLocals(int n){
		numOfLocals = n;
	}


	public void printIR() {
		System.out.println(label_name + ":");
	}

	public void MIPSme(){	
        MIPSGenerator.getInstance().function_prologue(funcName, numOfLocals*4);
    }
}
