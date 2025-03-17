package IR;
import java.util.*;
import TEMP.*;	
import IR.*;
import MIPS.*;

public class IRcommand_Call_Function_Args_Void extends IRcommand {
	
    String funcName;
    List<TEMP> args;
	int lineNumber;

    public IRcommand_Call_Function_Args_Void(String funcName, List<TEMP> args,int line) {
        this.funcName = funcName;
        this.args = args;
		this.lineNumber = line;
        for (TEMP arg : args) {
            use.add(arg.toString());
        }
    }


	public void printIR(){
		System.out.println("call function (args): " + funcName + args.toString());
	}

    public void MIPSme(){
        for (int i = args.size() - 1; i >= 0; i--) {
			TEMP arg = args.get(i);
			MIPSGenerator.getInstance().storeParamForCall(arg);
		}
        
        MIPSGenerator.getInstance().jalVoid(funcName, args.size());
    }
}
