package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Virtual_Call_Args_Not_Void extends IRcommand {
    TEMP var;
    List<TEMP> args;
	TEMP dst;
    String name;
    int offset;
    int line;

	public IRcommand_Virtual_Call_Args_Not_Void(TEMP dst, TEMP var, List<TEMP> args, int offset, String name, int line) {
		this.dst = dst;
		this.var = var;
        this.args = args;
        this.offset = offset;
        this.name = name;
        this.line = line;
        use.add(var.toString());
        for (TEMP arg : args) {
            use.add(arg.toString());
        }
        def.add(dst.toString());
    }

	public void printIR() {
        System.out.println(dst.toString() + " := virtual call (args): " + var.toString() + " " + name + " " + args.toString());
	}

    public void MIPSme(){
        for (int i = args.size() - 1; i >= 0; i--) {
			TEMP arg = args.get(i);
			MIPSGenerator.getInstance().storeParamForCall(arg);
		}
        
        MIPSGenerator.getInstance().virtualCallArgsNotVoid(dst, var, offset, args.size());
    }


}