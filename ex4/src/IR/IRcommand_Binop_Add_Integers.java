package IR;
import TEMP.*;

public class IRcommand_Binop_Add_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	
	public IRcommand_Binop_Add_Integers(TEMP dst,TEMP t1,TEMP t2){
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	public void printIR(){
		System.out.println(dst.toString() + " := " + t1.toString() + " + " + t2.toString());
	}
}
