package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Label extends IRcommand {

	String label_name;
	int lineNumber;
	boolean isScopeStart;
	
	public IRcommand_Label(String label_name,int line,boolean isScopeStart)
	{
		this.label_name = label_name;
		this.lineNumber = line;
		this.isScopeStart = isScopeStart;
	}

	public Set<String> genVars() {
		return new HashSet<>();
	}

	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

	public void printIR() {
		System.out.println(label_name + ":");
	}
}
