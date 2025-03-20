package AST;
import SYMBOL_TABLE.*;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_FUNCTION_VAR_DOT_NO_ARGS extends AST_FUNCTION
{

    public AST_VAR var;
    public String funcName;
    public int line;
    
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNCTION_VAR_DOT_NO_ARGS(AST_VAR var, String funcName, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== function -> varDot ID( %s ) LPAREN RPAREN\n", funcName);

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
        /* AST NODE TYPE = FUNCTION VAR DOT NO ARGS */
        /*********************************/
        System.out.println("AST NODE FUNCTION VAR DOT NO ARGS");

        /******************************************/
        /* RECURSIVELY PRINT v and name ... */
        /******************************************/
        if (var != null) var.PrintMe();
        System.out.println("FUNCTION: " + funcName);

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "FUNCTION VAR DOT NO ARGS\nFUNCTION: " + funcName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the variable to get his type
        TYPE varType = var.SemantMe();
        var.varClassName = varType.name;

        // check if the variable is TYPE_CLASS
		if(!(varType instanceof TYPE_CLASS)){
			System.out.format(">> ERROR(%d) variable is not an instance of a class\n", this.line);
			printError(this.line);
		}

        TYPE funcSearch = null;

        TYPE currClass = SYMBOL_TABLE.getInstance().get_current_class();
        if (currClass != null){
            if (((TYPE_CLASS)currClass).name.equals(((TYPE_CLASS)varType).name)) {
                funcSearch = SYMBOL_TABLE.getInstance().currentClassFunctionMembers.search(funcName);
                if (funcSearch == null){
                    if (((TYPE_CLASS)varType).father == null){
                        System.out.format(">> ERROR(%d) %s is not declared in the current class\n", this.line, funcName);
                        printError(this.line);
                    }
                    else {
                        funcSearch = ((TYPE_CLASS)varType).findFunctionInInheritanceTree(funcName);
                        if (funcSearch == null) {
                            System.out.format(">> ERROR(%d) %s is not a data memeber of the classes it extends\n", this.line, funcName);
                            printError(this.line);
                        }
                    }
                }
            }
            else {
                funcSearch = ((TYPE_CLASS)varType).findFunctionInInheritanceTree(funcName);
                if (funcSearch == null) {
                    System.out.format(">> ERROR(%d) %s is not a data memeber of the class or the classes it extends111\n", this.line, funcName);
                    printError(this.line);
                }
            }
        }
        else {
            funcSearch = ((TYPE_CLASS)varType).findFunctionInInheritanceTree(funcName);
            if (funcSearch == null) {
                System.out.format(">> ERROR(%d) %s is not a data memeber of the class or the classes it extends222\n", this.line, funcName);
                printError(this.line);
            }
        }

        // return the return type of the function
		return ((TYPE_FUNCTION)funcSearch).returnType;
    }

    public TEMP IRme()
    {
        TEMP t = var.IRme();
        TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
        String className = var.varClassName;
        int offset = CLASSES_MAP.getInstance().getMethodOffset(className, funcName);
        IR.getInstance().Add_IRcommand(new IRcommand_Virtual_Call_No_Args_Not_Void(dst, t, funcName, offset, IR.getInstance().currLine));
        return dst;
    }   
}
