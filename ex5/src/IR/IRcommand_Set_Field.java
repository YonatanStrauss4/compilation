package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Set_Field extends IRcommand {
    TEMP classInstanceTemp;
    TEMP var;
    String name;
    int offset;
    int line;

    public IRcommand_Set_Field(TEMP classInstanceTemp, TEMP var, String name, int offset, int line) {
        this.classInstanceTemp = classInstanceTemp;
        this.var = var;
        this.name = name;
        this.offset = offset;
        this.line = line;
    }

	public void printIR() {
        System.out.println("field set: " + classInstanceTemp.toString() + " " + name + " " + var.toString());
	}

    public void MIPSme(){
        MIPSGenerator.getInstance().set_field(classInstanceTemp, var, name, offset);

    }


}