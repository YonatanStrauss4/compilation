package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Return_No_Exp extends IRcommand {

    String funcName;
    int lineNumber;
    
    public IRcommand_Return_No_Exp(String funcName, int line) {
        this.funcName = funcName;
        this.lineNumber = line;
    }

 

    public void printIR() {
        System.out.println("return no exp");
    }

    public void MIPSme(){
            MIPSGenerator.getInstance().ret_no_exp(funcName);
    }

}
