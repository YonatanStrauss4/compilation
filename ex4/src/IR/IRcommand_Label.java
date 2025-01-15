package IR;
import java.util.*;

import TEMP.*;

public class IRcommand_Label extends IRcommand
{
	String label_name;
	
	public IRcommand_Label(String label_name)
	{
		this.label_name = label_name;
	}

	public Set<String> usedVars() {
		return new HashSet<>();
	}

	public Set<String> definedVars() {
		return new HashSet<>();
	}

	public void printIR() {
		System.out.println(label_name + ":");
	}
}
