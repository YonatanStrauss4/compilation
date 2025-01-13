package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;

public class AST_STMT_RETURN_NO_EXP extends AST_STMT
{
    
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_STMT_RETURN_NO_EXP(int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== stmt -> RETURN SEMICOLON\n");

        this.line = line;

    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = STMT RETURN NO EXP */
        /*********************************/
        System.out.println("AST NODE STMT RETURN NO EXP");

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "STMT\nRETURN NO EXP");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
    }

    public TYPE SemantMe()
    {
        // check if return statemnt is outside of function scope
        if(!SYMBOL_TABLE.getInstance().get_inside_function()){
            System.out.format(">> ERROR(%d) return statement outside of function scope\n",line);
            printError(line);
        }

        // this is an empty return statement, so check if the function
        if(!(SYMBOL_TABLE.getInstance().get_current_function().returnType instanceof TYPE_VOID))
        {
            System.out.format(">> ERROR(%d) return statement should be empty fo function with return type void\n",line);
            printError(line);
        }

        // return TYPE_VOID (because return doesnt have an expression)
        return TYPE_VOID.getInstance();
    }
}
