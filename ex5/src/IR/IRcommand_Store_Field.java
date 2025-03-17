
package IR;

import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Store_Field extends IRcommand {

	String var_name;
	TEMP src;
	int offset;
	int lineNumber;
	
	public IRcommand_Store_Field(String var_name, TEMP src, int offset, int line) {
		this.src = src;
		this.var_name = var_name;
		this.offset = offset;
		this.lineNumber = line;
		use.add(src.toString());
	}
	

	public void printIR() {
		System.out.println(String.format("store Field %s, %s ", var_name, src.toString()));
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().storeField(src, offset);
	}
}
