package IR;
import java.util.*;
import TEMP.*;	
import IR.*;
import MIPS.*;

public class IRcommand_Call_Function_Args_Not_Void extends IRcommand {
	
    String funcName;
    List<TEMP> args;
	TEMP dst;
	int lineNumber;

    public IRcommand_Call_Function_Args_Not_Void(TEMP dst, String funcName, List<TEMP> args,int line) {
        this.funcName = funcName;
		this.dst = dst;
        this.args = args;
		this.lineNumber = line;
		if (dst != null) {
		def.add(dst.toString());
		}
		for (TEMP arg : args) {
			use.add(arg.toString());
		}
    }

	public void printIR(){
		System.out.println(dst.toString() + " := call function (args): " + funcName + args.toString());
	}

	public void MIPSme(){
		for (int i = args.size() - 1; i >= 0; i--) {
			TEMP arg = args.get(i);
			MIPSGenerator.getInstance().storeParamForCall(arg);
		}

		MIPSGenerator.getInstance().jalNotVoid(dst, funcName, args.size());
	}
}
