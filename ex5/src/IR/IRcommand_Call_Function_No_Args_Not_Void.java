package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Call_Function_No_Args_Not_Void extends IRcommand {
    String funcName;
	TEMP dst;
    int line;

    public IRcommand_Call_Function_No_Args_Not_Void(TEMP dst, String funcName, int line) {
        this.funcName = funcName;
		this.dst = dst;
        this.line = line;
        if (dst != null) {
            def.add(dst.toString());
        }
    }




	public void printIR() {
        System.out.println(dst.toString() + " := call function (no args): " + funcName + "()"); 
	}

    public void MIPSme(){
        MIPSGenerator.getInstance().jalNotVoid(dst, funcName, 0);
    }


}