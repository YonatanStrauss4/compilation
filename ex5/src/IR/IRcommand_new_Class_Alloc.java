package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_new_Class_Alloc extends IRcommand {
	
	int lineNumber;	
	TEMP dst;
    String className;
	boolean isGlobal = false;
	
	public IRcommand_new_Class_Alloc(TEMP dst, String className, int line, boolean isGlobal) {
        this.lineNumber = line;
        this.dst = dst;
        this.className = className;
        this.isGlobal = isGlobal;
	}

	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		if (isGlobal)
			System.out.println(dst.toString() + " := new global class " + className);
		else{
			System.out.println(dst.toString() + " := new class " + className);
		}
	}

	public void MIPSme(){}
}
