package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_FUNC_STMT_VAR_DOT_NO_ARGS extends AST_FUNC_STMT
{
    public AST_VAR var;
    public String funcName;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNC_STMT_VAR_DOT_NO_ARGS(AST_VAR var, String funcName, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== funcStmt-> var DOT ID( %s ) LPAREN RPAREN SEMICOLON\n", funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.var = var;
        this.funcName = funcName;
        this.line = line;
    }


    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = FUNC STMT VAR DOT NO ARGS */
        /*********************************/
        System.out.println("AST NODE FUNC STMT VAR DOT NO ARGS");

        /******************************************/
        /* RECURSIVELY PRINT var and name ... */
        /******************************************/
        if (var != null) var.PrintMe();
        System.out.println("Function name: " + funcName);

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "FUNC STMT VAR DOT NO ARGS\nFunction name: " + funcName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the variable to get his type
        TYPE varType = var.SemantMe();

        // check if the variable is TYPE_CLASS
		if(!(varType instanceof TYPE_CLASS)){
			System.out.format(">> ERROR(%d) variable is not an instance of a class\n", this.line);
				printError(this.line);
		}

        // check if the function is indeed a data member of the class or the classes it extends
		varType = (TYPE_CLASS)varType;
		TYPE funcSearch = SYMBOL_TABLE.getInstance().currentClassFunctionMembers.search(funcName);
		if(funcSearch == null){
			funcSearch = ((TYPE_CLASS)varType).findFunctionInInheritanceTree(funcName);
			if(funcSearch == null){
				System.out.format(">> ERROR(%d) %s is not a data memeber of the class or the classes it extends\n", this.line, funcName);
				printError(this.line);
			}
			
		}

        // return the return type of the function
		return ((TYPE_FUNCTION)funcSearch).returnType;
    }
}
