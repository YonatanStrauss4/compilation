package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommandConstInt extends IRcommand {
	
	TEMP t;
	int value;
	int lineNumber;
	
	public IRcommandConstInt(TEMP t,int value,int line) {
		this.lineNumber = line;
		this.t = t;
		this.value = value;
		def.add(t.toString());
	}


	public void printIR() {
		System.out.println(t.toString() + " := " + String.format("%d", value));
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().li(t,value);
	}
}
