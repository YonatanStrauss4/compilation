package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Virtual_Call_No_Args_Not_Void extends IRcommand {
    TEMP var;
    String name;
	TEMP dst;
    int offset;
    int line;

    public IRcommand_Virtual_Call_No_Args_Not_Void(TEMP dst, TEMP var, String name, int offset, int line) {
        this.var = var;
		this.dst = dst;
        this.name = name;
        this.offset = offset;
        this.line = line;
        use.add(var.toString());
        def.add(dst.toString());
    }


	public void printIR() {
        System.out.println(dst.toString() + " := virtual call (no args): " + var.toString() + " " + name + "()");
	}


    public void MIPSme(){
        MIPSGenerator.getInstance().virtualCallNotArgsNotVoid(dst, var, offset);
    }


}