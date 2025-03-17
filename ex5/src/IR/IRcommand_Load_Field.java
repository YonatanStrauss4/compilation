package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Load_Field extends IRcommand {

	TEMP dst;
	String fieldName;
    int offset;
	int lineNumber;
	
	public IRcommand_Load_Field(TEMP dst, String fieldName, int offset, int line) {
		this.dst = dst;
		this.fieldName = fieldName;
		this.offset = offset;
		this.lineNumber = line;
		def.add(dst.toString());

	}

	public void printIR() {
			System.out.println(String.format("%s := load(field: %s)", dst.toString(), fieldName));
	}

	public void MIPSme(){
			MIPSGenerator.getInstance().loadField(dst, offset);
	}


}
