package IR;
import java.util.*;

public class IRcommand_Jump_Label extends IRcommand
{
	String label_name;
	
	public IRcommand_Jump_Label(String label_name) {
		this.label_name = label_name;
	}
	public Set<String> usedVars() {
		return new HashSet<>();
	}

	public Set<String> definedVars() {
		return new HashSet<>();
	}

	public void printIR() {
		System.out.println("goto " + label_name);
	}
}
