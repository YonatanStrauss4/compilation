package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Array_Access extends IRcommand {
	
	int lineNumber;	
	TEMP arrayTemp;
    TEMP index;
	TEMP dst;
	
	public IRcommand_Array_Access(TEMP dst, TEMP arrayTemp, TEMP index, int line) {
        this.lineNumber = line;
        this.arrayTemp = arrayTemp;
        this.index = index;
        this.dst = dst;
    
	}


	/**
	 * Prints the IR command for allocating the variable.
	 */
	public void printIR(){
		System.out.println(dst.toString() + " := array_access " + arrayTemp.toString() + " " + index.toString());
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().array_access(dst, arrayTemp, index);
	}
}
