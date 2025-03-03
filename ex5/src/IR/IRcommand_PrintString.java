package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_PrintString extends IRcommand {

	TEMP t;
	int lineNumber;
	
	public IRcommand_PrintString(TEMP t, int line) {
		this.t = t;
		this.lineNumber = line;
	}

	public void printIR() {
		System.out.println("call PrintString(" + t.toString() + ")");
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().PrintStringSyscall(t);
	}

}
