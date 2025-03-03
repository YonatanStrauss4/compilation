package IR;
import java.util.*;
import TEMP.*;	
import IR.*;

public class IRcommand_Call_Function_Args_Void extends IRcommand {
	
    String funcName;
    List<TEMP> args;
	int lineNumber;

    public IRcommand_Call_Function_Args_Void(String funcName, List<TEMP> args,int line) {
        this.funcName = funcName;
        this.args = args;
		this.lineNumber = line;
    }

	public void printIR(){
		System.out.println("call function (args): " + funcName + args.toString());
	}

    public void MIPSme(){}
}
