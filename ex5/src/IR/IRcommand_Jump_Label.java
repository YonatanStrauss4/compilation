package IR;
import java.util.*;
import MIPS.*;

public class IRcommand_Jump_Label extends IRcommand {

	String label_name;
	int lineNumber;
	
	public IRcommand_Jump_Label(String label_name,int line) {
		this.label_name = label_name;
		this.lineNumber = line;
	}

	public void printIR() {
		System.out.println("goto " + label_name);
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().jump(label_name);
	}
}
