package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Nil extends IRcommand {

    TEMP dst;
    int lineNumber;
    
    public IRcommand_Nil(TEMP dst, int line) {
        this.dst = dst;
        this.lineNumber = line;
    }

    public void printIR() {
        System.out.println(dst.toString() + " := Nil");
    }

    public void MIPSme(){}

}
