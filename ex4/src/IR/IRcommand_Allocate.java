package IR;
import java.util.*;

public class IRcommand_Allocate extends IRcommand
{
	String var_name;
	
	public IRcommand_Allocate(String var_name){
		this.var_name = var_name;
	}

    public Set<String> usedVars() {
        return new HashSet<>();
    }

	public Set<String> definedVars() {
		return new HashSet<>();
	}


	public void printIR(){
		System.out.println("allocate " + var_name);
	}
}
