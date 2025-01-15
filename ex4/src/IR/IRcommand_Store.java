
package IR;

import java.util.*;
import TEMP.*;

public class IRcommand_Store extends IRcommand {
	String var_name;
	TEMP src;
	
	public IRcommand_Store(String var_name,TEMP src) {
		this.src      = src;
		this.var_name = var_name;
	}

	public Set<String> usedVars() {
		return new HashSet<>(Arrays.asList(src.toString()));
	}

	public Set<String> definedVars() {
		return new HashSet<>(Arrays.asList(var_name));
	}

	public void printIR() {
		System.out.println(String.format("store(%s := %s)", var_name, src.toString()));
	}


}
