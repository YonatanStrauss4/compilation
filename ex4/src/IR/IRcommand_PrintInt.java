package IR;
import java.util.*;

import TEMP.*;

public class IRcommand_PrintInt extends IRcommand {
	TEMP t;
	
	public IRcommand_PrintInt(TEMP t) {
		this.t = t;
	}

	public Set<String> usedVars() {
		return new HashSet<>(Arrays.asList(t.toString()));
	}

	public Set<String> definedVars() {
		return new HashSet<>();
	}

	public void printIR() {
		System.out.println("call PrintInt(" + t.toString() + ")");
	}

}
