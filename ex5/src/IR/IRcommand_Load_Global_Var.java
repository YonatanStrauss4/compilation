package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Load_Global_Var extends IRcommand {

	TEMP dst;
	String var_name;
	boolean isString;
	int lineNumber;
	
	public IRcommand_Load_Global_Var(TEMP dst,String var_name, boolean isString, int line) {
		this.dst = dst;
		this.var_name = var_name;
		this.isString = isString;
		this.lineNumber = line;
	}

	public void printIR() {
			System.out.println(String.format("%s := load(@%s)", dst.toString(), var_name));
	}

	public void MIPSme(){
		if(isString){
			MIPSGenerator.getInstance().loadGlobalString(dst, var_name);
		}
		else{
			MIPSGenerator.getInstance().loadGlobal(dst, var_name);
		}
	}

}
