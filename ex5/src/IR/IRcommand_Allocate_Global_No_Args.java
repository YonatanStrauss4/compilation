
package IR;
import java.util.*;
import MIPS.*;
import java.io.*;

public class IRcommand_Allocate_Global_No_Args extends IRcommand {
	
	String var_name;
	int lineNumber;

	public IRcommand_Allocate_Global_No_Args(String var_name,int line) {
		this.var_name = var_name;
		this.lineNumber = line;
	}


	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		System.out.println("allocate global " + var_name);
	}

	public void MIPSme()
	{
		MIPSGenerator.getInstance().allocate_global_no_args(var_name);
	}
}
