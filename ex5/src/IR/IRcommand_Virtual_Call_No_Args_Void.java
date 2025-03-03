package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Virtual_Call_No_Args_Void extends IRcommand {
    TEMP var;
    String name;
    int line;

    public IRcommand_Virtual_Call_No_Args_Void(TEMP var, String name, int line) {
        this.var = var;
        this.name = name;
        this.line = line;
    }

	public void printIR() {
        System.out.println("virtual call (no args): " + var.toString() + " " + name + "()");
	}


    public void MIPSme(){}


}