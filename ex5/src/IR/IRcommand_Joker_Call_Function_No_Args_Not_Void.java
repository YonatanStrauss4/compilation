package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Joker_Call_Function_No_Args_Not_Void extends IRcommand {
	TEMP dst;
    String name;
    int offset;
    int line;

	public IRcommand_Joker_Call_Function_No_Args_Not_Void(TEMP dst, int offset, String name, int line) {
		this.dst = dst;
        this.offset = offset;
        this.name = name;
        this.line = line;
        def.add(dst.toString());
    }

	public void printIR() {
        System.out.println(dst.toString() + " := virtual call (args): " + name);
	}

    public void MIPSme(){
        MIPSGenerator.getInstance().jokerCallNotArgsNotVoid(dst, offset);
    }


}