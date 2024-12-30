package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_VAR_DEC_NEW_EXP_ARGS extends AST_VAR_DEC
{

    public AST_TYPE t;
    public String variable;
    public AST_NEW_EXP new_exp;
    public int line;
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_VAR_DEC_NEW_EXP_ARGS(AST_TYPE t, String variable, AST_NEW_EXP new_exp, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== varDec -> type ID( %s ) ASSIGN newExp SEMICOLON\n" + variable);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.t = t;
        this.variable = variable;
        this.new_exp = new_exp;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = VAR DEC NEW EXP */
        /*********************************/
        System.out.println("AST NODE VAR DEC NEW EXP");

        /******************************************/
        /* RECURSIVELY PRINT t and new_exp ... */
        /******************************************/
        if (t != null) t.PrintMe();
        System.out.println("ID Name: " + variable);
        if (new_exp != null) new_exp.PrintMe();
        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "VAR DEC\nNEW EXP" + variable);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, t.SerialNumber);
        if (new_exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, new_exp.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // check if variable with new is a data member
        if (SYMBOL_TABLE.getInstance().get_inside_class() && !SYMBOL_TABLE.getInstance().get_inside_function()){
            System.out.format(">> ERROR(%d) variable with new cannot be a data member\n", this.line);
        }

        TYPE type = t.SemantMe();
        TYPE arrayType = null;

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
        TYPE findName = SYMBOL_TABLE.getInstance().findInScope(variable);
		TYPE currFunc = SYMBOL_TABLE.getInstance().get_current_function();

		TYPE findParam = null;
		if (currFunc != null && ((TYPE_FUNCTION)currFunc).params != null){
			findParam = ((TYPE_FUNCTION)currFunc).params.search(variable);
		}

        if(findName != null || isReservedWord(variable) || findParam != null){
            System.out.format(">> ERROR(%d) the name %s already been used scope or is a reserved word or is an argument\n", this.line, variable);
            printError(this.line);
        }

        // check if variable is shadowing a variable in the current class
		TYPE_CLASS curr_cls = SYMBOL_TABLE.getInstance().get_current_class();
		TYPE_CLASS_VAR_DEC curr_variable = new TYPE_CLASS_VAR_DEC(type, variable);
		if(!AST_VAR_DEC_ARGS.checkIfShadowingIsCorrect(curr_cls, curr_variable)){
			System.out.format(">> ERROR(%d) Shadowing is not allowed\n", this.line, variable);
			printError(this.line);
		}

        if(type instanceof TYPE_ARRAY){
            arrayType = ((TYPE_ARRAY)type).type;
        }

        // semant the expression
        TYPE new_exp_type = new_exp.SemantMe();

        // check for array declaration incompatible types
        if(arrayType != null && !arrayType.equals(new_exp_type)){
            System.out.format(">> ERROR(%d) Incompatible types: array %s declared with type %s and used with type %s  \n", this.line, variable, arrayType.name, new_exp_type.name);
            printError(this.line);
        }
        
        // enter the variable declaration to the symbol table
		SYMBOL_TABLE.getInstance().enter(variable, type);

		// return the varible as a class member
		return new TYPE_CLASS_VAR_DEC(type, variable);
    }
}
