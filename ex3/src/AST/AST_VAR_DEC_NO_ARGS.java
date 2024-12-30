package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_VAR_DEC_NO_ARGS extends AST_VAR_DEC
{

    public AST_TYPE t;
    public String varName;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_VAR_DEC_NO_ARGS(AST_TYPE t, String varName, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== varDec -> type ID( %s ) SEMICOLON\n" + varName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.t = t;
        this.varName = varName;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = VAR DEC NO ARGS */
        /*********************************/
        System.out.println("AST NODE VAR DEC NO ARGS");

        /******************************************/
        /* RECURSIVELY PRINT typAndId ... */
        /******************************************/
        if (t != null) t.PrintMe();
        System.out.println("ID Name: " + varName);

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "VAR DEC\nNO ARGS\n" + varName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, t.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the type of the variable
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
		TYPE currFunc = SYMBOL_TABLE.getInstance().get_current_function();

		TYPE findParam = null;
		if (currFunc != null && ((TYPE_FUNCTION)currFunc).params != null){
			findParam = ((TYPE_FUNCTION)currFunc).params.search(varName);
		}

        if(findName != null || isReservedWord(varName) || findParam != null){
            System.out.format(">> ERROR(%d) the name %s already been used scope or is a reserved word or is an argument\n", this.line, varName);
            printError(this.line);
        }

        // check if variable is shadowing a variable in the current class
		TYPE_CLASS curr_cls = SYMBOL_TABLE.getInstance().get_current_class();
		TYPE_CLASS_VAR_DEC curr_variable = new TYPE_CLASS_VAR_DEC(type, varName);
		if(!AST_VAR_DEC_ARGS.checkIfShadowingIsCorrect(curr_cls, curr_variable)){
			System.out.format(">> ERROR(%d) Shadowing is not allowed\n", this.line, varName);
			printError(this.line);
		}
        
		// enter the variable declaration to the symbol table
		SYMBOL_TABLE.getInstance().enter(varName, type);

        // enter the variable as a class member of current class
        if(curr_cls != null && !(SYMBOL_TABLE.getInstance().get_inside_function())){
            SYMBOL_TABLE.getInstance().currentClassVariableMembers.insertAtEnd(new TYPE_CLASS_VAR_DEC(type, varName));
        }

		// return the varible as a declared var

		return new TYPE_CLASS_VAR_DEC(type, varName);
	}
}
