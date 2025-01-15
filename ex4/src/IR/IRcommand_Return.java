package IR;
import java.util.*;

import TEMP.*;

public class IRcommand_Return extends IRcommand {
    public TEMP return_value;
    
    public IRcommand_Return(TEMP return_value) {
        this.return_value = return_value;
    }

    public Set<String> usedVars() {
		return new HashSet<>(Arrays.asList(return_value.toString()));
	}

	public Set<String> definedVars() {
		return new HashSet<>();
	}

    public void printIR() {
        System.out.println("return " + return_value.toString());
    }

}
