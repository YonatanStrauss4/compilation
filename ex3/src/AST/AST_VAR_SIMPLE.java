package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
public class AST_VAR_SIMPLE extends AST_VAR
{

	public String varName;
	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SIMPLE(String varName, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",varName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.varName = varName;
		this.line = line;
	}


	public void PrintMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST VAR SIMPLE */
		/**********************************/
		System.out.format("AST NODE VAR SIMPLE( %s )\n",varName);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nSIMPLE\n(%s)",varName));
	}

	public TYPE SemantMe()
    {
        TYPE varFindType = null;

		// Check if we're inside a class
		if (SYMBOL_TABLE.getInstance().get_inside_class()) {
    		// We're inside a class, search in the class' variables (including inheritance tree)
    		varFindType = SYMBOL_TABLE.getInstance().currentClassVariableMembers.search(varName);

    		// Get the current class
    		TYPE currClass = SYMBOL_TABLE.getInstance().get_current_class();
    		if (currClass != null) {
        		// If the variable was not found in the current class, check the superclass (inheritance tree)
        		if (varFindType == null || !((varFindType instanceof TYPE_INT) || (varFindType instanceof TYPE_CLASS) || (varFindType instanceof TYPE_NIL))) {
            		// If the class has no father (no superclass), print error
            		if (((TYPE_CLASS) currClass).father == null) {
                		System.out.format(">> ERROR(%d) variable %s has not been declared\n", this.line, varName);
                		printError(this.line);
            		}

           			// Search in the inheritance tree
            		varFindType = ((TYPE_CLASS)currClass).findVariableInInheritanceTree(varName);

            		// If still not found, print error
            		if (varFindType == null || !((varFindType instanceof TYPE_INT) || (varFindType instanceof TYPE_CLASS) || (varFindType instanceof TYPE_NIL))) {
                		System.out.format(">> ERROR(%d) variable %s has not been declared\n", this.line, varName);
                		printError(this.line);
            		}
        		}
    		}
		} 
		else {
    		// If not inside a class, search in the global scope
    		varFindType = SYMBOL_TABLE.getInstance().find(varName);

    		// If the variable is still not found in the global scope, print error
    		if (varFindType == null || !((varFindType instanceof TYPE_INT) || (varFindType instanceof TYPE_ARRAY) || (varFindType instanceof TYPE_CLASS) || (varFindType instanceof TYPE_NIL))) {
        		System.out.format(">> ERROR(%d) variable %s has not been declared\n", this.line, varName);
        		printError(this.line);
    		}
		}
        
        return varFindType;
    }
}
