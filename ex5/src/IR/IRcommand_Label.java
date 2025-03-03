package IR;
import java.util.*;
import MIPS.*;

public class IRcommand_Label extends IRcommand {

	String label_name;
	int lineNumber;
	boolean isScopeStart;
	
	public IRcommand_Label(String label_name,int line, boolean isScopeStart)
	{
		this.label_name = label_name;
		this.lineNumber = line;
		this.isScopeStart = isScopeStart;
	}

	public void printIR() {
		System.out.println(label_name + ":");
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().label(label_name);


	}
}
