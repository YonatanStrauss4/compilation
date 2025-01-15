package IR;

import java.util.*;
import TEMP.*;

public class IRcommandConstInt extends IRcommand
{
	TEMP t;
	int value;
	
	public IRcommandConstInt(TEMP t,int value)
	{
		this.t = t;
		this.value = value;
	}

	public Set<String> usedVars() {
		return new HashSet<>();
	}

	public Set<String> definedVars() {
		return new HashSet<>(Arrays.asList(t.toString()));
	}

	public void printIR() {
		System.out.println(t.toString() + " := " + String.format("%d", value));
	}
}
