package AST;
import TYPES.*;
import java.io.PrintWriter;

public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public static PrintWriter fileWriter;
	public int SerialNumber;

	
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void PrintMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
	public TYPE SemantMe() {
        	return null;
    	}
	public void printError(int line) {
		try {
			fileWriter.write("ERROR (" + line + ")\n");
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	// a function to check if a string is a reserved word in the L language
	public boolean isReservedWord(String name) {
		// add equals to class, nil, extends, new, if, while, int, string, array, void, return
		return name.equals("class") || name.equals("nil") || name.equals("extends") || name.equals("new") || name.equals("if") || name.equals("while") || name.equals("int") || name.equals("string") || name.equals("array") || name.equals("void") || name.equals("return") || name.equals("PrintInt") || name.equals("PrintString");
	}
}


