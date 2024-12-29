package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_VAR_DEC_ARGS extends AST_VAR_DEC
{

	public AST_TYPE t;
	public String varName;
	public AST_EXP exp;
  	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_DEC_ARGS(AST_TYPE t, String varName, AST_EXP exp, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== varDec -> type ID( %s ) ASSIGN exp SEMICOLON\n" + varName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.t = t;
		this.varName = varName;
		this.exp = exp;
		this.line = line;
	}

	public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = VAR DEC ARGS */
        /*********************************/
        System.out.println("AST NODE VAR DEC ARGS");

        /******************************************/
        /* RECURSIVELY PRINT t and e ... */
        /******************************************/
        if (t != null) t.PrintMe();
		System.out.println("ID Name: " + varName);
        if (exp != null) exp.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "VAR DEC\nARGS" + varName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, t.SerialNumber);
        if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, exp.SerialNumber);
    }

	public TYPE SemantMe()
	{
		//semant variable type
		TYPE type = t.SemantMe();

		// check if variable is of type void
		if(type instanceof TYPE_VOID){
			System.out.format(">> ERROR(%d) variable cannot be of void type\n", this.line);
			printError(this.line);
		}

		// check if type is null
        if(type == null){
            System.out.format(">> ERROR(%d) no type declared\n", this.line);
            printError(this.line);
        }

		// check if variable has a name that already been used in scope, or is a reserved word
        TYPE findName = SYMBOL_TABLE.getInstance().findInScope(varName);
        if(findName != null || isReservedWord(varName)){
            System.out.format(">> ERROR(%d) the name %s already been used scope or is a reserved word\n", this.line, varName);
            printError(this.line);
        }

		// check if variable is shadowing a variable in the current class
		TYPE_CLASS curr_cls = SYMBOL_TABLE.getInstance().get_current_class();
		TYPE_CLASS_VAR_DEC curr_variable = new TYPE_CLASS_VAR_DEC(type, varName);
		if(!checkIfShadowingIsCorrect(curr_cls, curr_variable)){
			System.out.format(">> ERROR(%d) Shadowing is not allowed\n", this.line, varName);
			printError(this.line);
		}
		
		// check, for a variable that is a data member of a class, if it is assigned a constant
		if(!SYMBOL_TABLE.getInstance().get_inside_function() && SYMBOL_TABLE.getInstance().get_inside_class() && !(exp instanceof AST_EXP_INT || exp instanceof AST_EXP_STRING || exp instanceof AST_EXP_NIL)){
			System.out.format(">> ERROR(%d) variable data members of classes can only be assigned constants\n", this.line, varName);
			printError(this.line);
		}

		// enter the variable declaration to the symbol table
		SYMBOL_TABLE.getInstance().enter(varName, type);

		// enter the variable as a class member of current class
        if(curr_cls != null && !(SYMBOL_TABLE.getInstance().get_inside_function())){
            SYMBOL_TABLE.getInstance().currentClassVariableMembers.insertAtEnd(new TYPE_CLASS_VAR_DEC(type, varName));
        }

		// return the varible as a class member
		return new TYPE_CLASS_VAR_DEC(type, varName);
	}

	// a function to check if shadowing is correct
	public static boolean checkIfShadowingIsCorrect(TYPE_CLASS curr_cls, TYPE_CLASS_VAR_DEC curr_variable) {
	if(curr_cls == null){
		return true;
	}
    TYPE_CLASS father_cls = curr_cls.father;

    while (father_cls != null) {
        // Check if the variable exists in the father class
        TYPE_CLASS_VAR_DEC variable_in_father = father_cls.findVariable(curr_variable.name);
        
        // If the variable exists and its type is the same as the current variable, return false (shadowing)
        if (variable_in_father != null) {
            if (variable_in_father.type.equals(curr_variable.type)) {
                return false;   
            }
        }
        // Check if a function with the same name exists in the father class
        TYPE_FUNCTION function_in_father = father_cls.findFunction(curr_variable.name);
        // If the function exists, return false (shadowing)
        if (function_in_father != null) {
            return false;
        }
        // Move to the next parent class
        father_cls = father_cls.father;
    }
    // If no shadowing is detected, return true
    return true;
}

}
