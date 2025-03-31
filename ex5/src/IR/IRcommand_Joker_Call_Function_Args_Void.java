package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Joker_Call_Function_Args_Void extends IRcommand {
    List<TEMP> args;
    String name;
    int offset;
    int line;

	public IRcommand_Joker_Call_Function_Args_Void(List<TEMP> args, String name, int offset, int line) {
        this.args = args;
        this.name = name;
        this.offset = offset;
        this.line = line;
        for (TEMP arg : args) {
            use.add(arg.toString());
        }
    }


	public void printIR() {
        System.out.println("Joker call (args): " + name + " " + args.toString());
	}

    public void MIPSme(){
        for (int i = args.size() - 1; i >= 0; i--) {
			TEMP arg = args.get(i);
			MIPSGenerator.getInstance().storeParamForCall(arg);
		}
        MIPSGenerator.getInstance().jokerCallArgsVoid(offset, args.size());
    }


}