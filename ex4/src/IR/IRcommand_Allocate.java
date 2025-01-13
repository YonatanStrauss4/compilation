package IR;

public class IRcommand_Allocate extends IRcommand
{
	String var_name;
	
	public IRcommand_Allocate(String var_name){
		this.var_name = var_name;
	}

	public void printIR(){
		System.out.println("allocate " + var_name);
	}
}
