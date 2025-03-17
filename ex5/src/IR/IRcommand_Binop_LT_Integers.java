package IR;
import java.util.*;
import TEMP.*;	
import MIPS.*;

public class IRcommand_Binop_LT_Integers extends IRcommand {
	
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	int lineNumber;

	public IRcommand_Binop_LT_Integers(TEMP dst,TEMP t1,TEMP t2,int line) {
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
		this.lineNumber = line;
        def.add(dst.toString());
        use.add(t1.toString());
        use.add(t2.toString());
	}



	public void printIR() {
		System.out.println(dst.toString() + " := " + t1.toString() + " < " + t2.toString());
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().slt(t1,t2,dst);
	}
}
