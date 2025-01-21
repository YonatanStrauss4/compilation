package IR;
import java.util.*;

public class IRcommand_Allocate extends IRcommand {
	
	String var_name;
	int lineNumber;
	
	public IRcommand_Allocate(String var_name,int line) {
		this.var_name = var_name;
		this.lineNumber = line;
	}

    public Set<String> genVars() {
        return new HashSet<>();
    }

	public Set<String> killVars(int maxLine) {
		return new HashSet<>();
	}

	public void printIR(){
		System.out.println("allocate " + var_name);
	}
}
