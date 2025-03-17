package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_new_Array_Alloc extends IRcommand {
	
	int lineNumber;
    TEMP arraySize;
	TEMP dst;


	public IRcommand_new_Array_Alloc(TEMP dst, TEMP arraySize, int line) {
		this.lineNumber = line;
        this.arraySize = arraySize;
		this.dst = dst;
		def.add(dst.toString());
		use.add(arraySize.toString());
	}


	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
			System.out.println(dst.toString() + " := new array " + arraySize.toString());
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().new_array_alloc(dst, arraySize);
	}
}
