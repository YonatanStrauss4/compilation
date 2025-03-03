package IR;
import java.util.*;
import MIPS.*;

public class IRcommand_EndFunction extends IRcommand {

	String label_name;
	int lineNumber;
	
	public IRcommand_EndFunction(String label_name, int line)
	{
		this.label_name = label_name;
		this.lineNumber = line;
	}

	public void printIR() {
		System.out.println(label_name + ":");
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().function_epilogue();
    }
}
