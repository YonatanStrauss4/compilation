package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Load_Const_String extends IRcommand {

	TEMP dst;
	String stringName;
	int lineNumber;
	
	public IRcommand_Load_Const_String(TEMP dst,String stringName, int line) {
		this.dst = dst;
		this.stringName = stringName;
		this.lineNumber = line;
		def.add(dst.toString());

	}


	public void printIR() {
			System.out.println(String.format("%s := load(constant: %s)", dst.toString(), stringName));
	}

	public void MIPSme(){
		MIPSGenerator.getInstance().load_string(dst, stringName);
	}

}
