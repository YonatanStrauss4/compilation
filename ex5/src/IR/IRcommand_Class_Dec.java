package IR;
import java.util.*;
import TEMP.*;


public class IRcommand_Class_Dec extends IRcommand {
    String className;
    List<String> fields;
    int line;

    public IRcommand_Class_Dec(String className, List<String> fields, int line){ 
        this.line = line;
        this.className = className;
        this.fields = fields;
    }

	public void printIR(){
		System.out.println(className + " = class" + fields.toString());
	}

    public void MIPSme(){}
}