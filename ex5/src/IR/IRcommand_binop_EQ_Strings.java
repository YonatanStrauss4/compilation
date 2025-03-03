package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_binop_EQ_Strings extends IRcommand {
	
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	int lineNumber;

	public IRcommand_binop_EQ_Strings(TEMP dst,TEMP t1,TEMP t2,int line) {
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
		this.lineNumber = line;
	}

	public void printIR() {
		System.out.println(dst.toString() + " := " + t1.toString() + " == " + t2.toString());
	}
	
	public void MIPSme(){
		MIPSGenerator.getInstance().string_equality(t1,t2,dst);
	}
}
