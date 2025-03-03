package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Set_Field extends IRcommand {
    TEMP classInstanceTemp;
    TEMP var;
    String name;
    int line;

    public IRcommand_Set_Field(TEMP classInstanceTemp, TEMP var, String name, int line) {
        this.classInstanceTemp = classInstanceTemp;
        this.var = var;
        this.name = name;
        this.line = line;
    }

	public void printIR() {
        System.out.println("field set: " + classInstanceTemp.toString() + " " + name + " " + var.toString());
	}

    public void MIPSme(){}


}