
package IR;

import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Store_Global extends IRcommand {

	String var_name;
	TEMP src;
	int lineNumber;
	
	public IRcommand_Store_Global(String var_name, TEMP src, int line) {
		this.src = src;
		this.var_name = var_name;
		this.lineNumber = line;
		use.add(src.toString());

	}


	public void printIR() {
		System.out.println(String.format("store Global %s, %s ", var_name, src.toString()));
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().storeGlobal(src, var_name);
	}
}
