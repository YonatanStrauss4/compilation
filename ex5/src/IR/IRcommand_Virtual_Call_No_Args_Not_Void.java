package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Virtual_Call_No_Args_Not_Void extends IRcommand {
    TEMP var;
    String name;
	TEMP dst;
    int line;

    public IRcommand_Virtual_Call_No_Args_Not_Void(TEMP dst, TEMP var, String name, int line) {
        this.var = var;
		this.dst = dst;
        this.name = name;
        this.line = line;
    }

	public void printIR() {
        System.out.println(dst.toString() + " := virtual call (no args): " + var.toString() + " " + name + "()");
	}


    public void MIPSme(){}


}