package IR;
import java.util.*;

public class IRcommand_Jump_Label extends IRcommand {

	String label_name;
	int lineNumber;
	
	public IRcommand_Jump_Label(String label_name,int line) {
		this.label_name = label_name;
		this.lineNumber = line;
	}
	
	public Set<String> genVars() {
		return new HashSet<>();
	}

	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

	public void printIR() {
		System.out.println("goto " + label_name);
	}
}
