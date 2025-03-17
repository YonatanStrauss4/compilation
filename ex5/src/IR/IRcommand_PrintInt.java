package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_PrintInt extends IRcommand {

	TEMP t;
	int lineNumber;
	
	public IRcommand_PrintInt(TEMP t, int line) {
		this.t = t;
		this.lineNumber = line;
		use.add(t.toString());
	}

	public void printIR() {
		System.out.println("call PrintInt(" + t.toString() + ")");
	}
	
	public void MIPSme(){
		MIPSGenerator.getInstance().PrintIntSyscall(t);
	}


}
