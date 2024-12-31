package AST;
import SYMBOL_TABLE.*;
import TYPES.*;

public class AST_FUNC_DEC_ARGS_LIST extends AST_Node
{

	public AST_TYPE t;
  	public String variable;
	public AST_FUNC_DEC_ARGS_LIST args;
	public int line;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_FUNC_DEC_ARGS_LIST(AST_TYPE t, String variable, AST_FUNC_DEC_ARGS_LIST args, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (args != null) System.out.print("====================== funcDecArgs ->  type ID( %s ) COMMA funcDecArgs\n" + variable);
		if (args == null) System.out.print("====================== funcDecArgs  -> type ID( %s )\n" + variable);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.t = t;
		this.variable = variable;
    	this.args = args;
		this.line = line;
	}

	public void PrintMe() {
        	/*********************************/
        	/* AST NODE TYPE = FUNC DEC ARGS LIST */
        	/*********************************/
        	System.out.println("AST NODE FUNC DEC ARGS LIST");

        	/******************************************/
        	/* RECURSIVELY PRINT t and args ... */
        	/******************************************/
        	if (t != null) t.PrintMe();
			System.out.format("Function name( %s )\n", variable);
        	if (args != null) args.PrintMe();

        	/***************************************/
        	/* PRINT Node to AST GRAPHVIZ DOT file */
        	/***************************************/
        	AST_GRAPHVIZ.getInstance().logNode(
            		SerialNumber,
            		"FUNC DEC ARGS LIST\nFunction name:" + variable);

        	/****************************************/
        	/* PRINT Edges to AST GRAPHVIZ DOT file */
        	/****************************************/
        	if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, t.SerialNumber);
        	if (args != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, args.SerialNumber);
	}

	public TYPE_LIST SemantMe()
	{
		TYPE type  = t.SemantMe();

		// check if a type has been declared for the argument
		if (type == null){
			System.out.format(">> ERROR(%d) function argument with no type\n",line);
			printError(line);
		}

		// check if a the type of the argument is void
		if(type instanceof TYPE_VOID)
		{
			System.out.format(">> ERROR(%d) function argument cannot be of void type\n",line);
			printError(line);
		}

		// find if variable name is already used in scope
		if(SYMBOL_TABLE.getInstance().findInScope(variable) != null || isReservedWord(variable)){
			System.out.format(">> ERROR(%d) function argument name already exists in scope or is a reserved name\n",line);
			printError(line);
		}


		// enter the variable to the symbol table
		SYMBOL_TABLE.getInstance().enter(variable, type);

		// semant the argument list recursively
		if(args == null)
		{
			return new TYPE_LIST(type ,null);
		}
		else
		{
			return new TYPE_LIST(type, args.SemantMe());
		}
	}

}
