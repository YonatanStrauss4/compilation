
package IR;
import java.util.*;
import MIPS.*;
import java.io.*;

public class IRcommand_Allocate_Local_Args extends IRcommand {
	
	String var_name;
	int lineNumber;

	public IRcommand_Allocate_Local_Args(String var_name,int line) {
		this.var_name = var_name;
		this.lineNumber = line;
	}

	
	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		System.out.println("allocate " + var_name);
	}

	public void MIPSme(){

	}

}
