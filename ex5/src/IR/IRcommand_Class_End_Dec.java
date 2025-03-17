package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;


public class IRcommand_Class_End_Dec extends IRcommand {
    String name;
    int line;

    public IRcommand_Class_End_Dec(String name, int line){ 
        this.name = name;
        this.line = line;
    }

	public void printIR(){
		System.out.println("end class decleration: " + name);
	}

    public void MIPSme(){
        MIPSGenerator.getInstance().finiliazeClass(name);
    }
}