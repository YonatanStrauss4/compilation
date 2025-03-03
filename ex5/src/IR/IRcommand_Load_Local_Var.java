package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Load_Local_Var extends IRcommand {

	TEMP dst;
	String var_name;
	int offset;
	int lineNumber;
	
	public IRcommand_Load_Local_Var(TEMP dst, String var_name, int offset, int line) {
		this.dst = dst;
		this.var_name = var_name;
		this.offset = offset;
		this.lineNumber = line;
	}

	public void printIR() {
			System.out.println(String.format("%s := load(local: %s)", dst.toString(), var_name));
	}

	public void MIPSme(){
			MIPSGenerator.getInstance().loadLocal(dst, offset);
	}


}
