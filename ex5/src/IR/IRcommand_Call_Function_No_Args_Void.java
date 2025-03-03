package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Call_Function_No_Args_Void extends IRcommand {
    String name;
    int line;

    public IRcommand_Call_Function_No_Args_Void(String name, int line) {
        this.name = name;
        this.line = line;
    }

	public void printIR() {
        System.out.println("call function (no args): " + name + "()"); 
	}

    public void MIPSme(){}


}