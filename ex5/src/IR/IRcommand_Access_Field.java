package IR;
import java.util.*;
import TEMP.*;
import IR.*;

public class IRcommand_Access_Field extends IRcommand {
	
	int lineNumber;	
	TEMP dst;
    TEMP var;
    String fieldName;
	
	public IRcommand_Access_Field(TEMP dst, TEMP var, String fieldName, int line) {
        this.lineNumber = line;
        this.dst = dst;
        this.fieldName = fieldName;
        this.var = var;
	}

	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
        System.out.println(dst.toString() + " := field access " + var.toString() + " " + fieldName);
	}
	
	public void MIPSme(){
		
	}
}
