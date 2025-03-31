
package IR;

import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Joker_Call_Function_No_Args_Void extends IRcommand {
    String name;
    int offset;
    int line;

    public IRcommand_Joker_Call_Function_No_Args_Void(String name, int offset, int line) {
        this.name = name;
        this.offset = offset;
        this.line = line;
    }

	public void printIR() {
        System.out.println("Joker call (no args): " + name + "()");
	}

    public void MIPSme(){
        MIPSGenerator.getInstance().jokerCallNotArgsVoid(offset);
    }


}