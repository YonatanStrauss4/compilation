package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;


public class IRcommand_Jump_If_Eq_To_Zero extends IRcommand {
	
	TEMP t;
	String label_name;
	int lineNumber;
	
	public IRcommand_Jump_If_Eq_To_Zero(TEMP t, String label_name,int line)
	{
		this.t          = t;
		this.label_name = label_name;
		this.lineNumber = line;
	}

	public void printIR() {
		System.out.println("if (" + t.toString() + " == 0) goto " + label_name);
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().beqz(t,label_name);
	}
}
