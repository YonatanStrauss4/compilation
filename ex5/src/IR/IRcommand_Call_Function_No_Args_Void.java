package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Call_Function_No_Args_Void extends IRcommand {
    String funcName;
    int line;

    public IRcommand_Call_Function_No_Args_Void(String funcName, int line) {
        this.funcName = funcName;
        this.line = line;
    }


	public void printIR() {
        System.out.println("call function (no args): " + funcName + "()"); 
	}

    public void MIPSme(){
        MIPSGenerator.getInstance().jalVoid(funcName, 0);
    }


}