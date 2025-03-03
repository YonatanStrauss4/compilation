
package IR;
import java.util.*;
import MIPS.*;
import TEMP.*;


public class IRcommand_Allocate_Local_No_Args extends IRcommand {
	
	int offset;
    TEMP dst;
	String varName;
	int lineNumber;

	public IRcommand_Allocate_Local_No_Args(TEMP dst, int offet, String varName, int lineNumber){
		this.varName = varName;
		this.dst = dst;
		this.offset = offet;
		this.lineNumber = lineNumber;
	}


	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		System.out.println("allocate local var: " + varName);
	}

	public void MIPSme()
	{
		MIPSGenerator.getInstance().allocate_local_no_args(offset, dst);
	}
}
