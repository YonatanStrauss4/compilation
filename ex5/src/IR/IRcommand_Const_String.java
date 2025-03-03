package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Const_String extends IRcommand {
	
	String value;
	String label;
	int lineNumber;
	
	public IRcommand_Const_String(String value, String label, int line) {
		this.lineNumber = line;
		this.value = value;
		this.label = label;
	}

	public void printIR() {
		System.out.println("const string: " + value + " label: " + label);
	}
	
	public void MIPSme(){
		MIPSGenerator.getInstance().allocate_string(value, label);
	}
}
