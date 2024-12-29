package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_FUNC_STMT_NO_ARGS extends AST_FUNC_STMT 
{
    
    public String funcName;
    public int line;
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNC_STMT_NO_ARGS(String funcName, int line) {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== funcStmt -> ID( %s ) LPAREN RPAREN SEMICOLON\n" + funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.funcName = funcName;
        this.line = line;
    }


    public void PrintMe() 
    {
        /*********************************/
        /* AST NODE TYPE = FUNC STMT NO ARGS */
        /*********************************/
        System.out.println("AST NODE FUNC STMT NO ARGS");

        /******************************************/
        /* PRINT name */
        /******************************************/
        System.out.println("Function name: " + funcName);

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                "FUNC STMT NO ARGS\nFunction name: " + funcName);
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

        // return the return type of the function
        return ((TYPE_FUNCTION)funcFindType).returnType;
    }
}
