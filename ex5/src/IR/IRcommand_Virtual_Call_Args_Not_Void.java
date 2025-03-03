package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Virtual_Call_Args_Not_Void extends IRcommand {
    TEMP var;
    List<TEMP> args;
	TEMP dst;
    String name;
    int line;

	public IRcommand_Virtual_Call_Args_Not_Void(TEMP dst, TEMP var, List<TEMP> args, String name, int line) {
		this.dst = dst;
		this.var = var;
        this.args = args;
        this.name = name;
        this.line = line;
    }

	public void printIR() {
        System.out.println(dst.toString() + " := virtual call (args): " + var.toString() + " " + name + " " + args.toString());
	}


    public void MIPSme(){}


}