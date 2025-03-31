package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Joker_Call_Function_Args_Not_Void extends IRcommand {
    List<TEMP> args;
	TEMP dst;
    String name;
    int offset;
    int line;

	public IRcommand_Joker_Call_Function_Args_Not_Void(TEMP dst, List<TEMP> args, int offset, String name, int line) {
		this.dst = dst;
        this.args = args;
        this.offset = offset;
        this.name = name;
        this.line = line;
        for (TEMP arg : args) {
            use.add(arg.toString());
        }
        def.add(dst.toString());
    }

	public void printIR() {
        System.out.println(dst.toString() + " := virtual call (args): " + name + " " + args.toString());
	}

    public void MIPSme(){
        for (int i = args.size() - 1; i >= 0; i--) {
			TEMP arg = args.get(i);
			MIPSGenerator.getInstance().storeParamForCall(arg);
		}
        
        MIPSGenerator.getInstance().jokerCallArgsNotVoid(dst, offset, args.size());
    }


}