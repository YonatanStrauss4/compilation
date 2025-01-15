package IR;

import java.util.*;
import TEMP.*;

public class IRcommand_Binop_EQ_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;

	public IRcommand_Binop_EQ_Integers(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	public Set<String> usedVars() {
		return new HashSet<>(Arrays.asList(t1.toString(), t2.toString()));
	}

	public Set<String> definedVars() {
		return new HashSet<>(Arrays.asList(dst.toString()));
	}

	public void printIR() {
		System.out.println(dst.toString() + " := " + t1.toString() + " == " + t2.toString());
	}
}
