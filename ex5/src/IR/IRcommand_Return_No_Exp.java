package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Return_No_Exp extends IRcommand {

    String funcName;
    boolean isMethod;
    String className;
    int lineNumber;
    
    public IRcommand_Return_No_Exp(String funcName, boolean isMethod, String className, int line) {
        this.funcName = funcName;
        this.isMethod = isMethod;
        this.className = className;
        this.lineNumber = line;
    }

 

    public void printIR() {
        System.out.println("return no exp");
    }

    public void MIPSme(){
        if(isMethod)
        {
            MIPSGenerator.getInstance().ret_no_exp(className + "_" + funcName);
        }
        else
        {
            MIPSGenerator.getInstance().ret_no_exp(funcName);
        }
    }

}
