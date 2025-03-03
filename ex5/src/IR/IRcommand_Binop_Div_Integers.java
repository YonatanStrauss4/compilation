package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Binop_Div_Integers extends IRcommand{
    
    public TEMP t1;
    public TEMP t2;
    public TEMP dst;
    int lineNumber;
	String dstLabeled;
    
    public IRcommand_Binop_Div_Integers(TEMP dst,TEMP t1,TEMP t2,int line) {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
        this.lineNumber = line;
		this.dstLabeled = dst.toString()+";"+line;
    }

    public void printIR(){
        System.out.println(dst.toString() + " := " + t1.toString() + " / " + t2.toString());
    }
	public void MIPSme(){
        MIPSGenerator.getInstance().div(dst,t1,t2);
    }
}
