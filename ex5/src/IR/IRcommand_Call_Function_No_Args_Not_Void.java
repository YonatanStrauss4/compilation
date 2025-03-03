package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Call_Function_No_Args_Not_Void extends IRcommand {
    String name;
	TEMP dst;
    int line;

    public IRcommand_Call_Function_No_Args_Not_Void(TEMP dst, String name, int line) {
        this.name = name;
		this.dst = dst;
        this.line = line;
    }

	public void printIR() {
        System.out.println(dst.toString() + " := call function (no args): " + name + "()"); 
	}

    public void MIPSme(){}


}