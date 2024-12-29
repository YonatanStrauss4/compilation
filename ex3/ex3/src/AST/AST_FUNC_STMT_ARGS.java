package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_FUNC_STMT_ARGS extends AST_FUNC_STMT
{

    public String funcName;
    public int line;
    public AST_EXP_ARGUMENTS funcArgs;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNC_STMT_ARGS(String funcName, AST_EXP_ARGUMENTS funcArgs, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== funcStmt -> var DOT ID( %s ) LPAREN RPAREN SEMICOLON\n" , funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.funcName = funcName;
        this.funcArgs = funcArgs;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = FUNC STMT ARGS */
        /*********************************/
        System.out.println("AST NODE FUNC STMT ARGS");

        /******************************************/
        /* PRINT name */
        /******************************************/
        System.out.println("Function name: " + funcName);

        /******************************************/
        /* RECURSIVELY PRINT eA */
        /******************************************/
        if (funcArgs != null) funcArgs.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "FUNC STMT ARGS\nFunction name: " + funcName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (funcArgs != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, funcArgs.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // get the current class
        TYPE_CLASS currClass = SYMBOL_TABLE.getInstance().get_current_class();
        TYPE funcFindType = null;
        
        // class is null, so we are in global scope
        if(currClass == null){
            // search for the function and check if it is a function
            funcFindType = SYMBOL_TABLE.getInstance().find(funcName);
            if(funcFindType == null || (funcFindType != null && !(funcFindType instanceof TYPE_FUNCTION))){
                System.out.format(">> ERROR(%d) function %s has not been declared\n", this.line, funcName);
				printError(this.line);
            }
        }
        
        // we are in a class scope
        else{
            //first, we search the function in the data members of the function
            funcFindType = SYMBOL_TABLE.getInstance().currentClassFunctionMembers.search(funcName);
            if(funcFindType == null || (funcFindType != null && !(funcFindType instanceof TYPE_FUNCTION))){
                if(((TYPE_CLASS)currClass).father == null){
                    System.out.format(">> ERROR(%d) function %s has not been declared\n", this.line, funcName);
                    printError(this.line);
                }
                // there is an inheritance tree, search in the data members of the fathers
                funcFindType = ((TYPE_CLASS)currClass).findFunctionInInheritanceTree(funcName);
                if(funcFindType == null || (funcFindType != null && !(funcFindType instanceof TYPE_FUNCTION))){
                    System.out.format(">> ERROR(%d) function %s has not been declared\n", this.line, funcName);
                    printError(this.line);
                }
            }
        }

        // function has been declared, we need to check if the decleration and the call have the same arguments and types
        TYPE_LIST args = null;
        if(funcArgs != null){
            args = funcArgs.SemantMe();  // semant the arguments of the function call
        }

        if(!(args.checkArgumentsList(((TYPE_FUNCTION)funcFindType).params))){
            System.out.format(">> ERROR(%d) mismatch in types of arguments in function call (\n", this.line, funcName);
            printError(this.line);
        }

        // return the return type of the function
        return ((TYPE_FUNCTION)funcFindType).returnType;
    }
}
