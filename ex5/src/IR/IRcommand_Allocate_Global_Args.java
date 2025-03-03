
package IR;
import java.util.*;
import MIPS.*;
import java.io.*;

public class IRcommand_Allocate_Global_Args extends IRcommand {
	
	String var_name;
    Object value;
	int lineNumber;

	public IRcommand_Allocate_Global_Args(Object value, String var_name,int line) {
		this.var_name = var_name;
        this.value = value;
		this.lineNumber = line;
	}

	
	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		System.out.println("allocate global " + var_name + " = " + value);
	}

	public void MIPSme(){

        if (value instanceof String) 
		{
            MIPSGenerator.getInstance().allocate_global_string(var_name, (String)value);
        } 
		else if (value instanceof Integer) 
		{
            MIPSGenerator.getInstance().allocate_global_int(var_name, (int)value);
        }
	}

}
