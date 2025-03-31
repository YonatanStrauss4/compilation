package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Return extends IRcommand {

    TEMP return_value;
    String funcName;
    boolean isMethod;
    String className;
    int lineNumber;
    
    public IRcommand_Return(TEMP return_value, String funcName, boolean isMethod, String className, int line) {
        this.return_value = return_value;
        this.funcName = funcName;
        this.isMethod = isMethod;
        this.className = className;
        this.lineNumber = line;
        use.add(return_value.toString());
    }

 

    public void printIR() {
        System.out.println("return " + return_value.toString());
    }

    public void MIPSme(){
        if(isMethod)
        {
            MIPSGenerator.getInstance().ret(return_value, className + "_" + funcName);
        }
        else
        {
            MIPSGenerator.getInstance().ret(return_value, funcName);
        }
    }

}
