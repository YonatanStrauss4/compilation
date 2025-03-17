package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_new_Class_Alloc extends IRcommand {
	
	int lineNumber;	
	TEMP dst;
    String className;

	public IRcommand_new_Class_Alloc(TEMP dst, String className, int line) {
        this.lineNumber = line;
        this.dst = dst;
        this.className = className;
		def.add(dst.toString());

	}

	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		System.out.println(dst.toString() + " := new class instance of class: " + className);
	}

	public void MIPSme(){
		int numOfFields = CLASSES_MAP.getInstance().getNumOfFields(className);
		MIPSGenerator.getInstance().allocate_class_instance(dst, className, numOfFields);
	}
}
