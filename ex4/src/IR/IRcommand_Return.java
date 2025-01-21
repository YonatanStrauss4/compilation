package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Return extends IRcommand {

    public TEMP return_value;
    int lineNumber;
    
    public IRcommand_Return(TEMP return_value,int line) {
        this.return_value = return_value;
        this.lineNumber = line;
    }

    public Set<String> genVars() {
		return new HashSet<>();
	}

	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

    public void printIR() {
        System.out.println("return " + return_value.toString());
    }

}
