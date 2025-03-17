package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Virtual_Call_No_Args_Void extends IRcommand {
    TEMP var;
    String name;
    int offset;
    int line;

    public IRcommand_Virtual_Call_No_Args_Void(TEMP var, String name, int offset, int line) {
        this.var = var;
        this.name = name;
        this.offset = offset;
        this.line = line;
        use.add(var.toString());
    }

	public void printIR() {
        System.out.println("virtual call (no args): " + var.toString() + " " + name + "()");
	}




    public void MIPSme(){
        MIPSGenerator.getInstance().virtualCallNotArgsVoid(var, offset);
    }


}