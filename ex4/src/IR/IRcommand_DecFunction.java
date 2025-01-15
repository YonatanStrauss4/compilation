package IR;
import java.util.*;

import TEMP.*;

public class IRcommand_DecFunction extends IRcommand {
    TEMP arg1;
    TEMP arg2;
    TEMP arg3;

    public IRcommand_DecFunction(TEMP arg1, TEMP arg2, TEMP arg3) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }

	public Set<String> usedVars() {
		return new HashSet<>();
	}

	public Set<String> definedVars() {
		return new HashSet<>();
	}

}
