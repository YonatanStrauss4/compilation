package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_DecFunction extends IRcommand {

    TEMP arg1;
    TEMP arg2;
    TEMP arg3;
	int lineNumber;

    public IRcommand_DecFunction(TEMP arg1, TEMP arg2, TEMP arg3,int line) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.lineNumber = line;
    }

	public Set<String> genVars() {
		return new HashSet<>();
	}

	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

}
