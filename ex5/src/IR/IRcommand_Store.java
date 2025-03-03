
package IR;

import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Store extends IRcommand {

	String var_name;
	TEMP src;
	int offset;
	int lineNumber;
	
	public IRcommand_Store(String var_name, TEMP src, int offset, int line) {
		this.src = src;
		this.var_name = var_name;
		this.offset = offset;
		this.lineNumber = line;
	}

	public void printIR() {
		if(offset == -1){
			System.out.println(String.format("store @%s, %s ", var_name, src.toString()));
		}
		else{
			System.out.println(String.format("store %s, %s ", var_name, src.toString()));
		}
	}

	public void MIPSme(){
		if(offset == -1){
			MIPSGenerator.getInstance().storeGlobal(src, var_name);
		}
		else{
			MIPSGenerator.getInstance().storeLocal(src, offset);
		}
	}
}
