package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Return extends IRcommand {

    TEMP return_value;
    int lineNumber;
    
    public IRcommand_Return(TEMP return_value, int line) {
        this.return_value = return_value;
        this.lineNumber = line;
    }

    public void printIR() {
        System.out.println("return " + return_value.toString());
    }

    public void MIPSme(){
            MIPSGenerator.getInstance().ret(return_value);
    }

}
