package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Return extends IRcommand {

    TEMP return_value;
    String varName;
    String funcName;
    int lineNumber;
    
    public IRcommand_Return(TEMP return_value, String varName, String funcName, int line) {
        this.return_value = return_value;
        this.varName = varName;
        this.funcName = funcName;
        this.lineNumber = line;
    }

    public void printIR() {
        System.out.println("return " + return_value.toString());
    }

    public void MIPSme(){
            MIPSGenerator.getInstance().ret(return_value);
    }

}
