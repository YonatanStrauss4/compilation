
package IR;

import java.util.*;
import TEMP.*;

public class IRcommand_Load extends IRcommand {
	TEMP dst;
	String var_name;
	
	public IRcommand_Load(TEMP dst,String var_name) {
		this.dst      = dst;
		this.var_name = var_name;
	}

	public Set<String> usedVars() {
		return new HashSet<>(Arrays.asList(var_name));
	}

	public Set<String> definedVars() {
		return new HashSet<>(Arrays.asList(dst.toString()));
	}

	public void printIR() {
		System.out.println(String.format("%s := load(%s)", dst.toString(), var_name));
	}
}
