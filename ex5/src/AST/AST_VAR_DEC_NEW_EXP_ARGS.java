package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;


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
        System.out.format("====================== varDec -> type ID( %s ) ASSIGN newExp SEMICOLON\n", variable);

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
            printError(this.line);
        }

        TYPE varType = t.SemantMe();

        // check if variable is of type void
        if(varType instanceof TYPE_VOID){
            System.out.format(">> ERROR(%d) variable cannot be of void type\n", this.line);
            printError(this.line);
        }

        // check if type is null
        if(varType == null){
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
		TYPE_CLASS_VAR_DEC curr_variable = new TYPE_CLASS_VAR_DEC(varType, variable);
		if(!AST_VAR_DEC_ARGS.checkIfShadowingIsCorrect(curr_cls, curr_variable)){
			System.out.format(">> ERROR(%d) Shadowing is not allowed\n", this.line, variable);
			printError(this.line);
		}

        TYPE newType = new_exp.SemantMe();
        TYPE elemType = null;

        if (varType instanceof TYPE_ARRAY){
            elemType = ((TYPE_ARRAY)varType).type;
        }
        else {
            elemType = varType;
        }

        // check if variable types are equal
        if(!elemType.equals(newType)){
            // check if the type of the variable is TYPE_CLASS
            if(!(varType instanceof TYPE_CLASS) && !(varType instanceof TYPE_ARRAY)){
                System.out.format(">> ERROR(%d) type missmatch, cannot assign %s to %s\n",line, newType.name, varType.name);
                printError(line);
            }

            // variable type is a class but not equal to newExp type, so we check for inheritance
            if(!((TYPE_CLASS)newType).checkIfInherit((TYPE_CLASS)varType)){
                System.out.format(">> ERROR(%d) type missmatch, cannot assign %s to %s\n",line, newType.name, varType.name);
                printError(line);
                }
            }

        // check if we assign a non array type to an array
        if(varType instanceof TYPE_ARRAY && !(new_exp instanceof AST_NEW_TYPE_EXP_IN_BRACKS)){
            System.out.format(">> ERROR(%d) type missmatch, cannot assign non array to array\n",line);
            printError(line);
        }

        // check if we assign assign array type to a non array type
        if(!(varType instanceof TYPE_ARRAY) && !(new_exp instanceof AST_NEW_TYPE)){
            System.out.format(">> ERROR(%d) type missmatch, cannot assign array to non array\n",line);
            printError(line);
        }

        
        // enter the variable declaration to the symbol table
		SYMBOL_TABLE.getInstance().enter(variable, varType, false, false);

		// return the varible as a class member
		return new TYPE_CLASS_VAR_DEC(varType, variable);
    }

    public TEMP IRme()
    {
        // check if variable with new is a class instance
        String className = null;
        if(new_exp instanceof AST_NEW_TYPE){
            AST_NEW_TYPE t_id = (AST_NEW_TYPE)new_exp;
            className = t_id.getTypeName();
            
            // push the class intance to the offset table
            OFFSET_TABLE.getInstance().pushVariable(variable, "CLASS", true, className);
        }
        
        else{
            // push the array intance to the offset table
            OFFSET_TABLE.getInstance().pushVariable(variable, "ARRAY", false, null);
        }

        // IRme for new_exp
        TEMP new_exp_temp = new_exp.IRme();

        // get a Fresh TEMP
        TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();

        // get the offset of the variable
        int offset = OFFSET_TABLE.getInstance().findVariableOffset(variable);

        // check if the expression is a new array
        if(new_exp instanceof AST_NEW_TYPE_EXP_IN_BRACKS){
            IR.getInstance().Add_IRcommand(new IRcommand_new_Array_Alloc(dst, new_exp_temp, IR.getInstance().currLine));
            IR.getInstance().Add_IRcommand(new IRcommand_Store_Local(variable, dst, offset, IR.getInstance().currLine));
        }
        
        // the expression is a new class
        else{
            IR.getInstance().Add_IRcommand(new IRcommand_new_Class_Alloc(dst, className, IR.getInstance().currLine));
            IR.getInstance().Add_IRcommand(new IRcommand_Store_Local(variable, dst, offset,IR.getInstance().currLine));
        }

        // return the dst register
        return dst;
    }
}


