package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Load_Param extends IRcommand {

	TEMP dst;
	String var_name;
	int offset;
	int lineNumber;
	
	public IRcommand_Load_Param(TEMP dst,int offset, String var_name, int line) {
		this.dst = dst;
		this.var_name = var_name;
		this.offset = offset;
		this.lineNumber = line;
		def.add(dst.toString());
	}


	public void printIR() {
			System.out.println(String.format("%s := load(function param: %s)", dst.toString(), var_name));
	}

	public void MIPSme(){
			MIPSGenerator.getInstance().loadParam(dst, offset);
	}


}
