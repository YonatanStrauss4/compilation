package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Virtual_Call_Args_Void extends IRcommand {
    TEMP var;
    List<TEMP> args;
    String name;
    int offset;
    int line;

	public IRcommand_Virtual_Call_Args_Void(TEMP var, List<TEMP> args, String name, int offset, int line) {
		this.var = var;
        this.args = args;
        this.name = name;
        this.offset = offset;
        this.line = line;
        use.add(var.toString());
        for (TEMP arg : args) {
            use.add(arg.toString());
        }
    }


	public void printIR() {
        System.out.println("virtual call (args): " + var.toString() + " " + name + " " + args.toString());
	}

    public void MIPSme(){
        for (int i = args.size() - 1; i >= 0; i--) {
			TEMP arg = args.get(i);
			MIPSGenerator.getInstance().storeParamForCall(arg);
		}
        MIPSGenerator.getInstance().virtualCallArgsVoid(var, offset, args.size());
    }


}